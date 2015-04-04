package net.codepixl.GLCraft;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.PerlinNoise;
import net.codepixl.GLCraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
import com.nishu.utils.Window;

public class GLCraft extends Screen{
	private World world;
	private GameLoop gameLoop;
	
	public GLCraft(){
		gameLoop = new GameLoop();
		gameLoop.setScreen(this);
		gameLoop.setDebugMode(false);
		gameLoop.start(60);
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		initCamera();
		
		world = new World();
	}
	
	private void initCamera(){
		
	}

	@Override
	public void initGL() {
		// TODO Auto-generated method stub
		glViewport(0,0,Display.getWidth(),Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,Constants.WIDTH/Constants.HEIGHT,0.001f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		
		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
	}

	@Override
	public void update() {
		world.update();
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			dispose();
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0,0,0.75f,1);
		world.render();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		world.dispose();
	}
	
	public static void main(String[] args) throws IOException{
		Window.createWindow(Constants.WIDTH, Constants.HEIGHT, "GLCraft", false);
		new GLCraft();
	}

}
