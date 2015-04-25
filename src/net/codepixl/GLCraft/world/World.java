package net.codepixl.GLCraft.world;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.PipedInputStream;
import java.nio.FloatBuffer;

import net.codepixl.GLCraft.GUI.GUIServer;
import net.codepixl.GLCraft.GUI.GUIStartScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Ray;
import net.codepixl.GLCraft.util.Raytracer;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.Camera;
import net.codepixl.GLCraft.world.entity.mob.MobManager;
import net.codepixl.GLCraft.world.tile.Tile;

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

public class World extends Screen{

	private boolean renderDebug = false;
	TrueTypeFont font;
	private WorldManager worldManager;
	private int currentBlock;
	private PipedInputStream actionsToDo = new PipedInputStream();
	
	public static final int AIRCHUNK = 0, MIXEDCHUNK = 1;

	public World(){
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
		try {
			actionsToDo.connect(Constants.actionsToDo);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Constants.setWorld(this);
		Spritesheet.tiles.bind();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/font/GLCraft.ttf")));
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		worldManager = new WorldManager(this);
	}

	@Override
	public void initGL() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glEnable(GL_CULL_FACE);
	}

	private void input(){
		if(Mouse.isButtonDown(0) && Constants.GAME_STATE == Constants.GAME){
			Mouse.setGrabbed(true);
		}else if(Constants.GAME_STATE == Constants.START_SCREEN){
			GUIStartScreen.input();
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
							getWorldManager().setTileAtPos(x, y, z, tile);
						}
					}
					if(s.contains("GLCRAFT_MOVE_PLAYER")){
						if(s.split("\\|\\|").length >= 2){
							String pos = s.split("\\|\\|")[1];
								if(pos.split(",").length >= 3){
									float x = Float.parseFloat(pos.split(",")[0]);
									float y = Float.parseFloat(pos.split(",")[1]);
									float z = Float.parseFloat(pos.split(",")[2]);
									worldManager.mobManager.getPlayerMP().setPos(new Vector3f(x,y,z));
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
									worldManager.mobManager.getPlayerMP().setRot(new Vector3f(yaw,pitch,roll));
								}
						}
					}
				}
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(Constants.downloadedWorld && !Constants.transferredWorld){
			int size = Constants.CHUNKSIZE * Constants.viewDistance;
			int total = size*size*size;
			int cPos = 0;
			System.out.println("transferringworld");
			worldManager.worldFromBuf();
			Constants.transferredWorld = true;
		}
		if(Constants.GAME_STATE == Constants.GAME){
			render3D();
			worldManager.render();
			currentBlock = raycast();
			glLoadIdentity();
			renderText();
		}else if(Constants.GAME_STATE == Constants.START_SCREEN){
			render2D();
			GUIStartScreen.render();
			glDisable(GL_TEXTURE_2D);
		}else if(Constants.GAME_STATE == Constants.SERVER){
			render2D();
			GUIServer.render();
			glDisable(GL_TEXTURE_2D);
		}
	}
	
	private int raycast(){
		Ray r = Raytracer.getScreenCenterRay();
		int tile = -1;
		while(r.distance < 10 ){
			if(worldManager.doneGenerating){
				if(worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z) == -1 || worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z) == 0){
					r.next();
				}else{
					//System.out.println(worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z));
					tile = worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z);
					if(tile != Tile.TallGrass.getId()){
						GL11.glBegin(GL11.GL_QUADS);
							Shape.createCube((int)r.pos.x-0.0005f, (int)r.pos.y-0.0005f, (int)r.pos.z-0.0005f, new Color4f(1,1,1,0.1f), new float[]{Spritesheet.tiles.uniformSize()*7,0}, 1.001f);
						GL11.glEnd();
					}else{
						GL11.glBegin(GL11.GL_QUADS);
							Shape.createCross((int)r.pos.x-0.0005f, (int)r.pos.y-0.0005f, (int)r.pos.z-0.0005f, new Color4f(1,1,1,0.1f), new float[]{Spritesheet.tiles.uniformSize()*7,0}, 1.001f);
						GL11.glEnd();
					}
					worldManager.selectedBlock = new Vector3f((int)r.pos.x, (int)r.pos.y, (int)r.pos.z);
					if(Mouse.isButtonDown(0) && worldManager.getMobManager().getPlayer().getBreakCooldown() == 0f){
						worldManager.setTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z, (byte)0);
						try {
							if(Constants.isMultiplayer){
								Constants.packetsToSend.write(new String("GLCRAFT_CHANGEBLOCK||"+(int)r.pos.x+","+(int)r.pos.y+","+(int)r.pos.z+"||0;").getBytes());
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						worldManager.getMobManager().getPlayer().setBreakCooldown(0.2f);
					}
					r.distance = 11;
				}
			}else{
				return -1;
			}
		}
		return tile;
	}

	void renderText(){
		render2D();
		glColor3f(1f,1f,1f);
		Constants.FONT.drawString(10, 10, "GLCraft Alpha 0.0.5");
		if(currentBlock != -1){
			String toolTip = "Block: "+Tile.getTile((byte)currentBlock).getName();
			Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth(toolTip)/2, 10, toolTip);
		}
		Constants.FONT.drawString(Constants.WIDTH/2, Constants.HEIGHT/2, "+");
		if(renderDebug){
			Camera c = getMobManager().getPlayer().getCamera();
			Constants.FONT.drawString(10,Constants.FONT.getLineHeight()+10, "X:"+(int)c.getX()+" Y:"+(int)c.getY()+" Z:"+(int)c.getZ());
			Constants.FONT.drawString(10,Constants.FONT.getLineHeight()*2+10, "RotX:"+(int)c.getPitch()+" RotY:"+(int)c.getYaw()+" RotZ:"+(int)c.getRoll());
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
		glOrtho(0,Constants.WIDTH,Constants.HEIGHT,0,-1,1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
	}
	
	public void render3D(){
		// TODO Auto-generated method stub
		glClearColor(0.0f,0.749019608f,1.0f,0.0f);
		glCullFace(GL_FRONT);
		glViewport(0,0,Constants.WIDTH,Constants.HEIGHT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,(float)Constants.WIDTH/(float)Constants.HEIGHT,0.5f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
	}
	
	@Override
	public void update() {
		if(Constants.GAME_STATE == Constants.GAME){
			worldManager.update();
		}
		input();
	}
	
	public MobManager getMobManager(){
		return worldManager.getMobManager();
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
	
	public void prepareForGame(){
		getWorldManager().mobManager = new MobManager(getWorldManager());
	}
}
