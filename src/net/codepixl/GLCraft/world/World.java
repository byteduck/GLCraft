package net.codepixl.GLCraft.world;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.awt.Font;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Ray;
import net.codepixl.GLCraft.util.Raytracer;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.Camera;
import net.codepixl.GLCraft.world.entity.mob.MobManager;
import net.codepixl.GLCraft.world.tile.Tile;

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
	private TrueTypeFont font;
	private WorldManager worldManager;
	private int currentBlock;
	
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
		Spritesheet.tiles.bind();
		font = new TrueTypeFont(new Font("Minecraftia",Font.PLAIN,16), true);
		worldManager = new WorldManager();
	}

	@Override
	public void initGL() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glEnable(GL_CULL_FACE);
	}

	private void input(){
		if(Mouse.isButtonDown(0)){
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
		render3D();
		worldManager.render();
		currentBlock = raycast();
		glLoadIdentity();
		renderText();
	}
	
	private int raycast(){
		Ray r = Raytracer.getScreenCenterRay();
		int tile = -1;
		while(r.distance < 10 ){
			if(worldManager.doneGenerating){
				if(worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z) == -1 || worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z) == 0){
					//System.out.println(worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z));
					r.next();
				}else{
					tile = worldManager.getTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z);
					GL11.glBegin(GL11.GL_QUADS);
						Shape.createCube((int)r.pos.x-0.05f, (int)r.pos.y+0.05f, (int)r.pos.z-0.05f, new Color4f(1,1,1,0.1f), new float[]{Spritesheet.tiles.uniformSize()*7,0}, 1.1f);
					GL11.glEnd();
					worldManager.selectedBlock = new Vector3f((int)r.pos.x, (int)r.pos.y, (int)r.pos.z);
					if(Mouse.isButtonDown(1)){
						System.out.println("break");
						worldManager.setTileAtPos((int)r.pos.x, (int)r.pos.y, (int)r.pos.z, (byte)0);
					}
					r.distance = 11;
				}
			}
		}
		return tile;
	}

	private void renderText(){
		render2D();
		glColor3f(1f,1f,1f);
		font.drawString(10, 10, "GLCraft Alpha 0.0.2");
		if(currentBlock != -1){
			String toolTip = "Block: "+Tile.getTile((byte)currentBlock).getName();
			font.drawString(Constants.WIDTH/2-font.getWidth(toolTip)/2, 10, toolTip);
		}
		font.drawString(Constants.WIDTH/2, Constants.HEIGHT/2, "+");
		if(renderDebug){
			Camera c = getMobManager().getPlayer().getCamera();
			font.drawString(10,font.getLineHeight()+10, "X:"+(int)c.getX()+" Y:"+(int)c.getY()+" Z:"+(int)c.getZ());
			font.drawString(10,font.getLineHeight()*2+10, "RotX:"+(int)c.getPitch()+" RotY:"+(int)c.getYaw()+" RotZ:"+(int)c.getRoll());
			font.drawString(10,font.getLineHeight()*3+10, "FPS:"+GameLoop.getFPS());
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
	}
	
	public void render3D(){
		// TODO Auto-generated method stub
		glCullFace(GL_FRONT);
		glViewport(0,0,Display.getWidth(),Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,Constants.WIDTH/Constants.HEIGHT,0.0001f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
	}
	
	@Override
	public void update() {
		worldManager.update();
		input();
	}
	
	public MobManager getMobManager(){
		return worldManager.getMobManager();
	}
}
