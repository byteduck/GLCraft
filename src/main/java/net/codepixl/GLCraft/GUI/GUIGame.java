package net.codepixl.GLCraft.GUI;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;

public class GUIGame extends GUIScreen {
	
	private WorldManager worldManager;
	private EntityPlayer player;
	private float SIZE = (float)Constants.getWidth()/18f;
	private float HEARTSIZE = 20;
	private float SPACING = 0;
	private float HEARTSPACING = 6;
	private float BUBBLESIZE = HEARTSIZE;
	private float BUBBLESPACING = HEARTSPACING;
	private GUISlot slots[];
	private ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
	
	public GUIGame(WorldManager w){
		this.worldManager = w;
		player = worldManager.getEntityManager().getPlayer();
		this.makeElements();
	}
	
	public void makeElements(){
		SIZE = GUISlot.size;
		HEARTSIZE = SIZE*0.455f;
		SPACING = 0;
		HEARTSPACING = HEARTSIZE*0.3f;
		BUBBLESIZE = HEARTSIZE;
		BUBBLESPACING = HEARTSPACING;
		slots = new GUISlot[9];
		for(int j = 0; j < 9; j++){
			float x = (Constants.getWidth()/2)+((j-4.5f)*(SIZE+SPACING));
			slots[j] = new GUISlot((int)x+(int)GUISlot.size/2,(int)(Constants.getHeight()-(SIZE/2f)),player);
		}
		addElements(slots);
	}
	
	@Override
	public void input(int xof, int yof){
		for(int i = 0; i < slots.length; i++){
			slots[i].itemstack = player.getInventory(i);
		}
		super.input(xof, yof);
		for(int i = 0; i < slots.length; i++){
			if(!worldManager.centralManager.guiManager.isGUIOpen()){
				slots[i].hover = false;
				slots[i].showLabel = false;
			}else{
				slots[i].showLabel = true;
			}
			player.getInventory()[i] = slots[i].itemstack;
		}
		if(!worldManager.centralManager.guiManager.isGUIOpen())
			slots[worldManager.getEntityManager().getPlayer().getSelectedSlot()].hover = true;
	}
	
	@Override
	public void render(){
		super.render();
		EntityPlayer p = worldManager.getEntityManager().getPlayer();
		Spritesheet.atlas.bind();
		if(p.tileAtEye().hasTexture() && !p.tileAtEye().isTransparent()){
			glPushMatrix();
			glBegin(GL_QUADS);
			float[] texCoords;
			if(p.tileAtEye().hasMetaTextures())
				texCoords = p.tileAtEye().getIconCoords((byte)worldManager.getMetaAtPos(p.getPos().x,p.getPos().y+1.52f,p.getPos().z));
			else
				texCoords = p.tileAtEye().getIconCoords();
			Shape.createCenteredSquare(Constants.getWidth()/2f, Constants.getHeight()/2f, new Color4f(1f,1f,1f,1f), texCoords, Constants.getWidth());
			glEnd();
			glPopMatrix();
		}else if(!p.canBreathe()){
			glPushMatrix();
			glBegin(GL_QUADS);
			Shape.createCenteredSquare(Constants.getWidth()/2f, Constants.getHeight()/2f, new Color4f(1f,1f,1f,1f), Tile.Water.getIconCoords(), Constants.getWidth());
			glEnd();
			glPopMatrix();
		}
		
		
		Spritesheet.atlas.bind();
		
		if(p.airLevel < 10f){
			glBegin(GL_QUADS);
			for(int i = 0; i < 10; i++){
				float x = (Constants.getWidth()/2)+(((float)i-4.5f)*(BUBBLESIZE+BUBBLESPACING)-SIZE*1.6f);
				Shape.createCenteredSquare((int)x,Constants.getHeight()-(SIZE/2f)-BUBBLESIZE*3.5f-2, new Color4f(1,1,1,1), p.getTexCoordsForAirIndex(i), BUBBLESIZE);
			}
			glEnd();
		}
		
		glBegin(GL_QUADS);
		for(int i = 0; i < 10; i++){
			float x = (Constants.getWidth()/2)+(((float)i-4.5f)*(HEARTSIZE+HEARTSPACING)-SIZE*1.6f);
			Shape.createCenteredSquare((int)x,Constants.getHeight()-(SIZE/2f)-HEARTSIZE*2f-2, new Color4f(1,1,1,1), p.getTexCoordsForHealthIndex(i), HEARTSIZE);
		}
		glEnd();
		
		for(int i = chatMessages.size()-1; i >= 0; i--){
			int ind = -i+chatMessages.size()-1;
			if(ind < 10)
				renderChatMessage(chatMessages.get(i),ind);
		}
		TextureImpl.unbind();
	}
	
	@Override
	public void update(){
		if(didResize)
			GUIChat.CHATY = (int) (Constants.getHeight()-GUISlot.size*3);
		super.update();
		Iterator<ChatMessage> it = chatMessages.iterator();
		while(it.hasNext()){
			ChatMessage c = it.next();
			c.update();
			if(c.opacity <= 0) it.remove();
		}
	}
	
	private static void renderChatMessage(ChatMessage c, int ind) {
		int y = GUIChat.CHATY-Tesselator.getFontHeight()-4-((Tesselator.getFontHeight()+4)*ind);
		GL11.glColor4f(0f, 0f, 0f, c.opacity*0.5f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createTexturelessRect2D(GUIChat.CHATX, y, GUIChat.CHATWIDTH+20, Tesselator.getFontHeight()+4, new Color4f(0,0,0,c.opacity*0.5f));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Tesselator.drawTextWithShadow(GUIChat.CHATX+2, y+2, c.msg, c.opacity);
	}

	public void addChatMessage(String msg){
		for(String line : msg.split("\n")){
			String msg2 = "";
			for(Character c : line.toCharArray()){
				msg2+=c;
				if(Tesselator.getFontWidth(msg2) > GUIChat.CHATWIDTH-4){
					chatMessages.add(new ChatMessage(msg2));
					msg2 = "";
				}
			}
			if(!msg2.equals(""))
				chatMessages.add(new ChatMessage(msg2));
		}		
	}
}
