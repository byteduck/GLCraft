package net.codepixl.GLCraft.world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.PipedInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;
import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIPauseMenu;
import net.codepixl.GLCraft.GUI.GUIServer;
import net.codepixl.GLCraft.GUI.GUIStartScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.sound.SoundManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.animal.EntityTestAnimal;
import net.codepixl.GLCraft.world.entity.mob.hostile.EntityTestHostile;
import net.codepixl.GLCraft.world.item.crafting.CraftingManager;
import net.codepixl.GLCraft.world.tile.Tile;

public class CentralManager extends Screen{

	private float cloudMove = 0f;
	
	private boolean renderDebug = false;
	TrueTypeFont font;
	private WorldManager worldManager;
	private int currentBlock;
	private PipedInputStream actionsToDo = new PipedInputStream();
	public GUIManager guiManager;
	private SoundManager soundManager;
	
	public static final int AIRCHUNK = 0, MIXEDCHUNK = 1;

	public CentralManager(){
		initGL();
		init();
	}
	
	@Override
	public void dispose() {
		Display.destroy();
		System.exit(0);
	}

	@Override
	public void init() {
		Constants.setWorld(this);
		initGUIManager();
		
		TextureManager.initTextures();
		CraftingManager.initRecipes();
		worldManager = new WorldManager(this);
		GLCraft.renderSplashText("Starting Central Manager...", "Starting Sound System");
		soundManager = new SoundManager();
		SoundManager.setMainManager(soundManager);
		
		//INIT DEBUGS
		DebugTimer.addTimer("total_render");
		DebugTimer.addTimer("chunk_render");
		DebugTimer.addTimer("entity_render");
		DebugTimer.addTimer("total_update");
		DebugTimer.addTimer("entity_update");
		DebugTimer.addTimer("chunk_update");
		DebugTimer.addTimer("chunk_tick");
		DebugTimer.addTimer("loop_time");
		DebugTimer.addTimer("ai_time");
	}

	private void initGUIManager(){
		guiManager = new GUIManager();
		GUIManager.setMainManager(guiManager);
		guiManager.addGUI(new GUIStartScreen(), "startScreen");
		guiManager.addGUI(new GUIServer(), "server");
		guiManager.addGUI(new GUIPauseMenu(), "pauseMenu");
		guiManager.showGUI("startScreen");
	}

	@Override
	public void initGL() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glEnable(GL_CULL_FACE);
	}

	private void input(){
		guiManager.input();
		if(Mouse.isButtonDown(0) && guiManager.mouseShouldBeGrabbed()){
			Mouse.setGrabbed(true);
		}else if(!guiManager.mouseShouldBeGrabbed()){
			Mouse.setGrabbed(false);
		}
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				if(Keyboard.isKeyDown(Keyboard.KEY_F3)){
					renderDebug = !renderDebug;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
					if(guiManager.isGUIOpen()){
						guiManager.closeGUI();
						Mouse.setGrabbed(true);
					}else{
						guiManager.showGUI("pauseMenu");
					}
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_E)){
					if(guiManager.getCurrentGUIName() == "crafting" || guiManager.getCurrentGUIName() == "adv_crafting"){
						guiManager.closeGUI();
						Mouse.setGrabbed(true);
					}else{
						guiManager.showGUI("crafting");
					}
				}
				Vector3f pos = worldManager.entityManager.getPlayer().getPos();
				if(Keyboard.isKeyDown(Keyboard.KEY_F)){
					worldManager.setTileAtPos(pos, Tile.Fire.getId(), true);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_P)){
					/*String id = JOptionPane.showInputDialog("Enter in the id of the tile you wish to place: ");
					int tileid = Integer.parseInt(id);
					worldManager.setTileAtPos(pos, (byte) tileid, true);*/
					/*worldManager.setTileAtPos(pos,Tile.ParticleProjector.getId(),true);*/
					/*worldManager.setTileAtPos(pos, Tile.Log.getId(), false);
					worldManager.setMetaAtPos((int)pos.x, (int)pos.y, (int)pos.z, (byte)4, true);*/
					worldManager.setTileAtPos(pos, Tile.Chest.getId(), true);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_B)){
					worldManager.setTileAtPos(pos, Tile.Bluestone.getId(), true);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_M)){
					System.out.println(worldManager.getMetaAtPos((int)pos.x, (int)pos.y, (int)pos.z));
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_T)){
					worldManager.setTileAtPos(pos, Tile.Tnt.getId(), true);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_SEMICOLON)){
					worldManager.entityManager.add(new EntityTestAnimal(pos, worldManager));
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_APOSTROPHE)){
					worldManager.entityManager.add(new EntityTestHostile(pos, worldManager));
				}
			
			}
		}
	}
	
	@Override
	public void render() {
		DebugTimer.getTimer("total_render").start();
		if(Constants.GAME_STATE == Constants.GAME){
			glLoadIdentity();
			render3D();
			worldManager.render();
			currentBlock = raycast();
			renderClouds();
			renderText();
			renderInventory();
		}
		render2D();
		guiManager.render();
		glDisable(GL_TEXTURE_2D);
		DebugTimer.getTimer("total_render").end();
	}

	private int raycast(){
		return worldManager.getEntityManager().getPlayer().raycast();
	}
	
	private void renderClouds(){
		Spritesheet.clouds.bind();
		Shape.currentSpritesheet = Spritesheet.clouds;
		GL11.glTranslatef(cloudMove, 100f, 1000f);
		GL11.glRotatef(-90f, 1f, 0f, 0f);
		GL11.glBegin(GL_QUADS);
		Shape.createPlane(-4000f, 0, 0, new Color4f(1f,1f,1f,0.5f), new float[]{0f,0f}, 2000f);
		Shape.createPlane(0, 0, 0, new Color4f(1f,1f,1f,0.5f), new float[]{0f,0f}, 2000f);
		Shape.createPlane(-2000f, 0, 0, new Color4f(1f,1f,1f,0.5f), new float[]{0f,0f}, 2000f);
		GL11.glEnd();
		Spritesheet.atlas.bind();
		Shape.currentSpritesheet = Spritesheet.atlas;
	}
	
	private void renderInventory() {
		glLoadIdentity();
		render2D();
		float SIZE = (float)Constants.WIDTH/18f;
		float HEARTSIZE = (float)Constants.WIDTH/36f;
		float SPACING = (float)Constants.WIDTH/36f;
		float HEARTSPACING = (float)Constants.WIDTH/72f;
		EntityPlayer p = worldManager.getEntityManager().getPlayer();
		Spritesheet.atlas.bind();
		if(!p.tileAtEye().isTransparent()){
			glPushMatrix();
			glBegin(GL_QUADS);
			Shape.createCenteredSquare(Constants.WIDTH/2f, Constants.HEIGHT/2f, new Color4f(1f,1f,1f,1f), p.tileAtEye().getIconCoords(), Constants.WIDTH);
			glEnd();
			glPopMatrix();
		}
		for(float i = 0; i < 9f; i++){
			Spritesheet.atlas.bind();
			glBegin(GL_QUADS);
			if(i == p.getSelectedSlot()){
				Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f,Constants.HEIGHT-(SIZE/2f), new Color4f(1,1,1,1), TextureManager.texture("gui.guislot"), (float)Constants.WIDTH/18f);
			}else{
				Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f,Constants.HEIGHT-(SIZE/2f), new Color4f(0,0,0,1), TextureManager.texture("gui.guislot"), (float)Constants.WIDTH/18f);
			}
			glEnd();
			if(!p.getInventory((int)i).isNull()){
				glPushMatrix();
				glTranslatef((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f,(float)Constants.HEIGHT-(SIZE/2f),0f);
				glScalef(0.7f,0.7f,0.7f);
					glBegin(GL_QUADS);
						if(p.getInventory((int)i).isTile()){
							if(p.getInventory((int)i).getTile().hasMetaTextures()){
								Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), p.getInventory((int)i).getTile().getIconCoords(p.getInventory((int)i).getMeta()), (float)Constants.WIDTH/18f);
							}else{
								Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), p.getInventory((int)i).getTile().getIconCoords(), (float)Constants.WIDTH/18f);
							}
						}else{
							Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), p.getInventory((int)i).getItem().getTexCoords(), (float)Constants.WIDTH/18f);
						}
					glEnd();
				glPopMatrix();
				Constants.FONT.drawString((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f, Constants.HEIGHT-(SIZE/2f), Integer.toString(p.getInventory((int)i).count));
				TextureImpl.unbind();
			}
		}
		Spritesheet.atlas.bind();
		glBegin(GL_QUADS);
		for(int i = 0; i < 10; i++){
			Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*HEARTSIZE+i*HEARTSPACING+HEARTSIZE/2f,Constants.HEIGHT-(SIZE/2f)-HEARTSIZE*2f, new Color4f(1,1,1,1), p.getTexCoordsForHealthIndex(i), HEARTSIZE);
		}
		glEnd();
	}

	private void renderText(){
		render2D();
		glColor3f(1f,1f,1f);
		Constants.FONT.drawString(10, 10, "GLCraft Alpha "+GLCraft.version);
		if(currentBlock != -1){
			String toolTip = "Block: "+Tile.getTile((byte)currentBlock).getName();
			Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth(toolTip)/2, 10, toolTip);
		}
		Constants.FONT.drawString(Constants.WIDTH/2, Constants.HEIGHT/2, "+");
		if(renderDebug){
			EntityPlayer p = getEntityManager().getPlayer();
			Constants.FONT.drawString(10,Constants.FONT.getLineHeight()+10, "X:"+(int)p.getX()+" Y:"+(int)p.getY()+" Z:"+(int)p.getZ());
			Constants.FONT.drawString(10,Constants.FONT.getLineHeight()*2+10, "RotX:"+(int)p.getRot().x+" RotY:"+(int)p.getRot().y+" RotZ:"+(int)p.getRot().z);
			Constants.FONT.drawString(10,Constants.FONT.getLineHeight()*3+10, "FPS:"+GameLoop.getFPS());
			Constants.FONT.drawString(10,Constants.FONT.getLineHeight()*4+10, "Entities:"+worldManager.entityManager.totalEntities());
			Iterator<DebugTimer> i = DebugTimer.getTimers().iterator();
			int ind = 5;
			while(i.hasNext()){
				Constants.FONT.drawString(10,Constants.FONT.getLineHeight()*ind+10, i.next().toString());
				ind++;
			}
		}
		
		TextureImpl.unbind();
	}
	
	public void render2D(){
		glCullFace(GL_BACK);
		glClearDepth(1);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glViewport(0,0,Constants.WIDTH,Constants.HEIGHT);
		glOrtho(0,Constants.WIDTH,Constants.HEIGHT,0,-200,200);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
	}
	
	public void createFog(){
		final FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
        {
            fogColours.put(new float[]{0.4f, 0.7f, 1.0f, 0.2f});
            fogColours.flip();
        }
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glFog(GL11.GL_FOG_COLOR, fogColours);
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
        GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_FASTEST);// GL_NICEST
        GL11.glFogf(GL11.GL_FOG_START, 10);
        GL11.glFogf(GL11.GL_FOG_END, 20);
        GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2f);
	}
	
	public void render3D(){
		//setupLighting();
		glCullFace(GL_FRONT);
		glViewport(0,0,Constants.WIDTH,Constants.HEIGHT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,(float)Constants.WIDTH/(float)Constants.HEIGHT,0.1f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
	}
	
	private void setupLighting(){
		glShadeModel(GL_SMOOTH);
		ByteBuffer f = ByteBuffer.allocateDirect(16);
	    glEnable(GL_DEPTH_TEST);
	    glEnable(GL_LIGHTING);
	    glEnable(GL_LIGHT0);
	    glLightModel(GL_LIGHT_MODEL_AMBIENT, (FloatBuffer)f.asFloatBuffer().put(new float[]{0.05f, 0.05f, 0.05f, 1f}).asReadOnlyBuffer().flip());
	    glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer)f.asFloatBuffer().put(new float[]{0,0,0,1}).flip());
	    glEnable(GL_COLOR_MATERIAL);
	    glColorMaterial(GL_FRONT, GL_DIFFUSE);
	}
	
	@Override
	public void update(){
		DebugTimer.getTimer("total_update").start();
		guiManager.update();
		if(Constants.GAME_STATE == Constants.GAME){
			worldManager.update();
		}
		input();
		cloudMove+=Time.getDelta()*2;
		cloudMove = cloudMove % 2000f;
		DebugTimer.getTimer("total_update").end();
	}
	
	public EntityManager getEntityManager(){
		return worldManager.getEntityManager();
	}
	
	public static FloatBuffer floatBuffer(float f, float s, float t, float l){
		FloatBuffer buf  = BufferUtils.createFloatBuffer(4);
		buf.put(new float[]{f,s,t,l});
		buf.flip();
		return buf;
	}
	
	public WorldManager getWorldManager(){
		return worldManager;
	}
	
	public void initSplashText(){
		render2D();
	}
	
	public void renderSplashText(String line1, String line2){
		int CENTER = Constants.WIDTH/2;
		int HCENTER = Constants.HEIGHT/2;
		glClearColor(0,0,0,1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		String ltext = "GLCraft is generating the world...";
		Constants.FONT.drawString(CENTER-Constants.FONT.getWidth(ltext)/2,30, ltext);
		Constants.FONT.drawString(CENTER-Constants.FONT.getWidth(line1)/2,HCENTER-Constants.FONT.getHeight(line1), line1);
		Constants.FONT.drawString(CENTER-Constants.FONT.getWidth(line2)/2,HCENTER+Constants.FONT.getHeight(line2), line2);
		TextureImpl.unbind();
		Display.update();
		glClearColor(0.0f,0.749019608f,1.0f,0.0f);
	}
	
	public void renderSplashText(String line1, String line2, int percent){
		int CENTER = Constants.WIDTH/2;
		int HCENTER = Constants.HEIGHT/2;
		glClearColor(0,0,0,1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		String ltext = "GLCraft is generating the world...";
		Constants.FONT.drawString(CENTER-Constants.FONT.getWidth(ltext)/2,30, ltext);
		Constants.FONT.drawString(CENTER-Constants.FONT.getWidth(line1)/2,HCENTER-Constants.FONT.getHeight(line1), line1);
		Constants.FONT.drawString(CENTER-Constants.FONT.getWidth(line2)/2,HCENTER+Constants.FONT.getHeight(line2), line2);
		TextureImpl.unbind();
		glLoadIdentity();
		render2D();
		glDisable(GL_TEXTURE_2D);
		glColor3f(0.1f,0.2f,0.1f);
		glBegin(GL_QUADS);
		glVertex2f(CENTER-100,HCENTER+100);
		glVertex2f(CENTER-100,HCENTER+110);
		glVertex2f(CENTER+100,HCENTER+110);
		glVertex2f(CENTER+100,HCENTER+100);
		glEnd();
		glColor3f(0.3f,0.5f,0.3f);
		glBegin(GL_QUADS);
		glVertex2f(CENTER-100,HCENTER+100);
		glVertex2f(CENTER-100,HCENTER+110);
		glVertex2f(CENTER+(percent*2-100),HCENTER+110);
		glVertex2f(CENTER+(percent*2-100),HCENTER+100);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		Display.update();
		glClearColor(0.0f,0.749019608f,1.0f,0.0f);
	}
}
