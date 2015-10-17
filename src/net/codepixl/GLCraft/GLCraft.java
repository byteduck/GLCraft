package net.codepixl.GLCraft;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.PNGDecoder;

import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
import com.nishu.utils.Window;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.CentralManager;

public class GLCraft extends Screen{
	private CentralManager world;
	private GameLoop gameLoop;
	
	public GLCraft(){
		gameLoop = new GameLoop();
		gameLoop.setScreen(this);
		gameLoop.setDebugMode(false);
		gameLoop.start(120);
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		initCamera();
		
		world = new CentralManager();
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
		final FloatBuffer position = BufferUtils.createFloatBuffer(4);
		position.put(0.5f).put(0.5f).put(5.0f).put(0.0f).flip();
		glLight(GL_LIGHT0, GL_POSITION, position);
		
		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
	}

	@Override
	public void update() {
		world.update();
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			Mouse.setGrabbed(false);
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
		Display.setIcon(new ByteBuffer[] {
	        loadIcon(GLCraft.class.getResource("/textures/icons/icon16.png")),
	        loadIcon(GLCraft.class.getResource("/textures/icons/icon32.png")),
		});
		Window.createWindow(Constants.WIDTH, Constants.HEIGHT, "GLCraft", false);
		new GLCraft();
	}
	
	private static ByteBuffer loadIcon(URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            PNGDecoder decoder = new PNGDecoder(is);
            ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bb, decoder.getWidth()*4, PNGDecoder.RGBA);
            bb.flip();
            return bb;
        } finally {
            is.close();
        }
    }

}
