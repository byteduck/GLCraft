package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUIGame extends GUIScreen{
	
	private WorldManager worldManager;
	private EntityPlayer player;
	private float SIZE = (float)Constants.WIDTH/18f;
	private float HEARTSIZE = 20;
	private float SPACING = 0;
	private float HEARTSPACING = 6;
	private float BUBBLESIZE = HEARTSIZE;
	private float BUBBLESPACING = HEARTSPACING;
	private GUISlot slots[] = new GUISlot[9];
	private ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
	
	public GUIGame(WorldManager w){
		this.worldManager = w;
		player = worldManager.getEntityManager().getPlayer();
		for(int j = 0; j < 9; j++){
			float i = j;
			slots[j] = new GUISlot((int)(Constants.WIDTH/4.5f+i*SIZE+i*SPACING+SIZE/2f)+(int)GUISlot.size/2,(int)(Constants.HEIGHT-(SIZE/2f)),player);
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
			Shape.createCenteredSquare(Constants.WIDTH/2f, Constants.HEIGHT/2f, new Color4f(1f,1f,1f,1f), texCoords, Constants.WIDTH);
			glEnd();
			glPopMatrix();
		}else if(!p.canBreathe()){
			glPushMatrix();
			glBegin(GL_QUADS);
			Shape.createCenteredSquare(Constants.WIDTH/2f, Constants.HEIGHT/2f, new Color4f(1f,1f,1f,1f), Tile.Water.getIconCoords(), Constants.WIDTH);
			glEnd();
			glPopMatrix();
		}
		
		
		Spritesheet.atlas.bind();
		
		if(p.airLevel < 10f){
			glBegin(GL_QUADS);
			for(int i = 0; i < 10; i++){
				Shape.createCenteredSquare((int)((float)Constants.WIDTH/4.5f+i*BUBBLESIZE+i*BUBBLESPACING+BUBBLESIZE/2f+(int)GUISlot.size/2),Constants.HEIGHT-(SIZE/2f)-BUBBLESIZE*3.5f-2, new Color4f(1,1,1,1), p.getTexCoordsForAirIndex(i), BUBBLESIZE);
			}
			glEnd();
		}
		
		glBegin(GL_QUADS);
		for(int i = 0; i < 10; i++){
			Shape.createCenteredSquare((int)((float)Constants.WIDTH/4.5f+i*HEARTSIZE+i*HEARTSPACING+HEARTSIZE/2f+(int)GUISlot.size/2),Constants.HEIGHT-(SIZE/2f)-HEARTSIZE*2f-2, new Color4f(1,1,1,1), p.getTexCoordsForHealthIndex(i), 24);
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
		super.update();
		Iterator<ChatMessage> i = chatMessages.iterator();
		while(i.hasNext()){
			ChatMessage c = i.next();
			c.update();
			if(c.opacity <= 0) i.remove();
		}
	}
	
	private static void renderChatMessage(ChatMessage c, int ind) {
		int y = GUIChat.CHATY-Constants.FONT.getHeight()-4-((Constants.FONT.getHeight()+4)*ind);
		GL11.glColor4f(0f, 0f, 0f, c.opacity*0.5f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createTexturelessRect2D(GUIChat.CHATX, y, GUIChat.CHATWIDTH+20, Constants.FONT.getHeight()+4, new Color4f(0,0,0,c.opacity*0.5f));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Tesselator.drawTextWithShadow(GUIChat.CHATX+2, y+2, c.msg, new Color(1,1,1,c.opacity), new Color(0.3f,0.3f,0.3f,c.opacity));
	}

	public void addChatMessage(String msg){
		for(String line : msg.split("\n")){
			String msg2 = "";
			for(Character c : line.toCharArray()){
				msg2+=c;
				if(Constants.FONT.getWidth(msg2) > GUIChat.CHATWIDTH-4){
					chatMessages.add(new ChatMessage(msg2));
					msg2 = "";
				}
			}
			if(!msg2.equals(""))
				chatMessages.add(new ChatMessage(msg2));
		}		
	}
}
