package net.codepixl.GLCraft.world;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightModel;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.PipedInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.Callable;

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

import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.GUIServer;
import net.codepixl.GLCraft.GUI.GUIStartScreen;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.sound.SoundManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class CentralManager extends Screen{

	private float cloudMove = 0f;
	
	private boolean renderDebug = false;
	TrueTypeFont font;
	private WorldManager worldManager;
	private int currentBlock;
	private PipedInputStream actionsToDo = new PipedInputStream();
	private GUIManager guiManager;
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
		Spritesheet.tiles.bind();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/font/GLCraft.ttf")));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		worldManager = new WorldManager(this);
		initGUIManager();
		soundManager = new SoundManager();
		SoundManager.setMainManager(soundManager);
	}
	
	private void initGUIManager(){
		guiManager = new GUIManager();
		GUIManager.setMainManager(guiManager);
		guiManager.addGUI(new GUIStartScreen(), "startScreen");
		guiManager.addGUI(new GUIServer(), "server");
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
		if(Mouse.isButtonDown(0) && Constants.GAME_STATE == Constants.GAME){
			Mouse.setGrabbed(true);
		}
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				if(Keyboard.isKeyDown(Keyboard.KEY_F3)){
					renderDebug = !renderDebug;
				}
			}
		}
	}
	
	@Override
	public void render() {
		try {
			while(actionsToDo.available() > 0){
				byte ByteBuf[] = new byte[1500];
				actionsToDo.read(ByteBuf);
				ByteBuf = Constants.trim(ByteBuf);
				String str = new String(ByteBuf);
				String[] strs = str.split(";");
				for(int i = 0; i < strs.length; i++){
					String s = strs[i];
					if(s.contains("GLCRAFT_CHANGEBLOCK")){
						if(s.split("\\|\\|").length >= 3){
							String pos = s.split("\\|\\|")[1];
							byte tile = (byte)Integer.parseInt(s.split("\\|\\|")[2]);
							int x = Integer.parseInt(pos.split(",")[0]);
							int y = Integer.parseInt(pos.split(",")[1]);
							int z = Integer.parseInt(pos.split(",")[2]);
							getWorldManager().setTileAtPos(x, y, z, tile, true);
						}
					}
					if(s.contains("GLCRAFT_MOVE_PLAYER")){
						if(s.split("\\|\\|").length >= 2){
							String pos = s.split("\\|\\|")[1];
								if(pos.split(",").length >= 3){
									float x = Float.parseFloat(pos.split(",")[0]);
									float y = Float.parseFloat(pos.split(",")[1]);
									float z = Float.parseFloat(pos.split(",")[2]);
									worldManager.getEntityManager().getPlayerMP().setPos(new Vector3f(x,y,z));
								}
						}
					}
					if(s.contains("GLCRAFT_ROT_PLAYER")){
						if(s.split("\\|\\|").length >= 2){
							String pos = s.split("\\|\\|")[1];
								if(pos.split(",").length >= 3){
									float yaw = Float.parseFloat(pos.split(",")[0]);
									float pitch = Float.parseFloat(pos.split(",")[1]);
									float roll = Float.parseFloat(pos.split(",")[2]);
									worldManager.getEntityManager().getPlayerMP().setRot(new Vector3f(yaw,pitch,roll));
								}
						}
					}
				}
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
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
	}

	@SuppressWarnings("static-access")
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
		Shape.currentSpritesheet = Spritesheet.tiles;
	}
	
	private void renderInventory() {
		glLoadIdentity();
		render2D();
		float SIZE = (float)Constants.WIDTH/18f;
		float HEARTSIZE = (float)Constants.WIDTH/36f;
		float SPACING = (float)Constants.WIDTH/36f;
		float HEARTSPACING = (float)Constants.WIDTH/72f;
		EntityPlayer p = worldManager.getEntityManager().getPlayer();
		Spritesheet.tiles.bind();
		if(!p.tileAtEye().isTransparent()){
			glPushMatrix();
			glBegin(GL_QUADS);
			Shape.createCenteredSquare(Constants.WIDTH/2f, Constants.HEIGHT/2f, new Color4f(1f,1f,1f,1f), p.tileAtEye().getIconCoords(), Constants.WIDTH);
			glEnd();
			glPopMatrix();
		}
		for(float i = 0; i < 9f; i++){
			Spritesheet.tiles.bind();
			glBegin(GL_QUADS);
			if(i == p.getSelectedSlot()){
				Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f,Constants.HEIGHT-(SIZE/2f), new Color4f(1,1,1,1), new float[]{Spritesheet.tiles.uniformSize()*3,Spritesheet.tiles.uniformSize()}, (float)Constants.WIDTH/18f);
			}else{
				Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f,Constants.HEIGHT-(SIZE/2f), new Color4f(1,1,1,1), new float[]{Spritesheet.tiles.uniformSize()*2,Spritesheet.tiles.uniformSize()}, (float)Constants.WIDTH/18f);
			}
			glEnd();
			if(p.getInventory((int)i) != null){
				glPushMatrix();
				glTranslatef((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f,(float)Constants.HEIGHT-(SIZE/2f),0f);
				glScalef(0.7f,0.7f,0.7f);
					glBegin(GL_QUADS);
						if(p.getInventory((int)i).isTile()){
							Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), p.getInventory((int)i).getTile().getIconCoords(), (float)Constants.WIDTH/18f);
						}else{
							Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), p.getInventory((int)i).getItem().getTexCoords(), (float)Constants.WIDTH/18f);
						}
					glEnd();
				glPopMatrix();
				Constants.FONT.drawString((float)Constants.WIDTH/9f+i*SIZE+i*SPACING+SIZE/2f, Constants.HEIGHT-(SIZE/2f), Integer.toString(p.getInventory((int)i).count));
				TextureImpl.unbind();
			}
		}
		glBegin(GL_QUADS);
		for(int i = 0; i < 10; i++){
			Shape.createCenteredSquare((float)Constants.WIDTH/9f+i*HEARTSIZE+i*HEARTSPACING+HEARTSIZE/2f,Constants.HEIGHT-(SIZE/2f)-HEARTSIZE*2f, new Color4f(1,1,1,1), p.getTexCoordsForHealthIndex(i), HEARTSIZE);
		}
		glEnd();
	}

	private void renderText(){
		render2D();
		glColor3f(1f,1f,1f);
		Constants.FONT.drawString(10, 10, "GLCraft Alpha 0.0.6");
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
	
	public void render3D(){
		//setupLighting();
		glClearColor(0.0f,0.749019608f,1.0f,0.0f);
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
	public void update() {
		guiManager.update();
		if(Constants.GAME_STATE == Constants.GAME){
			worldManager.update();
		}
		input();
		cloudMove+=Time.getDelta()*2;
		cloudMove = cloudMove % 2000f;
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
}
