package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUIGame extends GUIScreen{
	
	private WorldManager worldManager;
	private EntityPlayer player;
	private float SIZE = (float)Constants.WIDTH/18f;
	private float HEARTSIZE = 24;
	private float SPACING = (float)Constants.WIDTH/36f;
	private float HEARTSPACING = 12;
	private float BUBBLESIZE = HEARTSIZE;
	private float BUBBLESPACING = HEARTSPACING;
	private GUISlot slots[] = new GUISlot[9];
	
	public GUIGame(WorldManager w){
		this.worldManager = w;
		player = worldManager.getEntityManager().getPlayer();
		for(int j = 0; j < 9; j++){
			float i = j;
			slots[j] = new GUISlot((int)(Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f),(int)(Constants.HEIGHT-(SIZE/2f)));
		}
		addElements(slots);
	}
	
	@Override
	public void input(int xof, int yof){
		super.input(xof, yof);
		for(int i = 0; i < slots.length; i++){
			slots[i].hover = false;
			slots[i].itemstack = player.getInventory(i);
		}
		slots[worldManager.getEntityManager().getPlayer().getSelectedSlot()].hover = true;
	}
	
	@Override
	public void render(){
		super.render();
		EntityPlayer p = worldManager.getEntityManager().getPlayer();
		Spritesheet.atlas.bind();
		if(!p.tileAtEye().isTransparent()){
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
				Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*BUBBLESIZE+i*BUBBLESPACING+BUBBLESIZE/2f,Constants.HEIGHT-(SIZE/2f)-BUBBLESIZE*3.5f, new Color4f(1,1,1,1), p.getTexCoordsForAirIndex(i), BUBBLESIZE);
			}
			glEnd();
		}
		
		glBegin(GL_QUADS);
		for(int i = 0; i < 10; i++){
			Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*HEARTSIZE+i*HEARTSPACING+HEARTSIZE/2f,Constants.HEIGHT-(SIZE/2f)-HEARTSIZE*2f, new Color4f(1,1,1,1), p.getTexCoordsForHealthIndex(i), HEARTSIZE);
		}
		glEnd();
	}
}
