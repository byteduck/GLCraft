package net.codepixl.GLCraft;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.FileSystemException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureImpl;

import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.plugin.PluginManager;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.texturepack.TexturePackManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.Time;
import net.codepixl.GLCraft.util.logging.CrashHandler;
import net.codepixl.GLCraft.util.logging.TeeOutputStream;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.tile.Tile;

public class GLCraft{
	private CentralManager world;
	private PluginManager pluginManager;
	private static GLCraft glcraft;
	public static boolean isDevEnvironment = false;
	public static boolean loadExtPlugins = true;
	public static String version = "0.0.9dev";
	private Plugin devPlugin;
	public long window;
	
	public static GLCraft getGLCraft(){
		return glcraft;
	}
	
	public GLCraft() throws IOException{
		try{
			commonInitializer();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private GLCraft(Plugin p) throws IOException{
		devPlugin = p;
		commonInitializer();
		
	}
	
	private void commonInitializer() throws IOException{
		
		glcraft = this;
		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		
		GLFW.glfwInit();
		
		glfwWindowHint(GLFW_RESIZABLE, GL11.GL_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		window = glfwCreateWindow(1000, 700, "GLCraft", 0, 0);
		if(window == 0) {
		    throw new RuntimeException("Failed to create window.");
		}
		//glfwSetWindowIcon(window, new Buffer(loadIcon(GLCraft.class.getResource("/textures/icons/icon16.png"))));
		glfwMakeContextCurrent(window);
		GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
			@Override
			public void invoke(long window, int width, int height) {
				Constants.WIDTH = width;
				Constants.HEIGHT = height;
			}
		});
		
		initGL();
		init();
		long ltime = Time.getTime();
		double secondCounter = 0;
		while(!glfwWindowShouldClose(window)){
			update();
			render();
			updateDisplay();
			//if(Constants.maxFPS > 1)
			//	Display.sync(Constants.maxFPS);  TODO reimplement
			secondCounter+=Time.getDelta();
			if(secondCounter > 1){
				Constants.FPS = (int) (1d/Time.getDelta());
				secondCounter = 0;
			}
			Time.setDelta((Time.getTime()-ltime)/(double)Time.SECOND);
			ltime = Time.getTime();
		}
		
		glfwDestroyWindow(window);
		
		WorldManager.saveWorld(true);
		
	}
	
	public static void updateDisplay(){
		glfwPollEvents();
		glfwSwapBuffers(getGLCraft().window);
	}
	
	public void init() {
		Constants.gatherSystemInfo();

		TexturePackManager.initTexturePacks();
		
		/**To initialize Tiles and items because they are static*/
		Tile.tileMap.toString();
		Item.itemMap.toString();
		
		initCamera();
		world = new CentralManager();
		String pluginsFolder = Constants.GLCRAFTDIR+"/Plugins";
		new File(pluginsFolder).mkdirs();
		pluginManager = new PluginManager(pluginsFolder);
		if(loadExtPlugins){
			pluginManager.loadPlugins();
		}
		if(isDevEnvironment){
			pluginManager.addDevPlugin(devPlugin);
		}
		TextureManager.generateAtlas(false);
		GUIManager.getMainManager().showGUI("startScreen");
	}
	
	private void initCamera(){
		
	}
	
	public PluginManager getPluginManager(){
		return pluginManager;
	}

	public void initGL() {
		
		System.out.println("Init GL...");
		
		GL.createCapabilities();
		glfwShowWindow(window);
		
		System.out.println("Created OpenGL capabilities...");
		
		System.out.println("OpenGL Version: "+GL11.glGetString(GL11.GL_VERSION));
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/font/GLCraft.ttf")));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Created Font...");
		
		glViewport(0,0,1000,700);
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		GLU.gluPerspective(67f,Constants.WIDTH/Constants.HEIGHT,0.001f, 1000f);
		glMatrixMode(GL_MODELVIEW);
		
		System.out.println("Created Viewport...");

		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		initSplashText();
		renderSplashText("GLCraft is loading...","Initializing OpenGL...");
		
	}
	
	private void initSplashText(){
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
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glEnable(GL_CULL_FACE);
		GL11.glColor3f(1f,1f,1f);
	}
	
	public static void renderSplashText(String line1, String line2){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		String ltext = "GLCraft is loading...";
		Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth(ltext)/2,30, ltext);
		Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth(line1)/2,Constants.HEIGHT/2-Constants.FONT.getHeight(line1), line1);
		Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth(line2)/2,Constants.HEIGHT/2+Constants.FONT.getHeight(line2), line2);
		TextureImpl.unbind();
		updateDisplay();
	}

	public void update() {
		DebugTimer.startTimer("loop_time");
		world.update();
		pluginManager.update();
		// TODO Auto-generated method stub
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.0f,0.749019608f,1.0f,0.0f);
		world.render();
		DebugTimer.endTimer("loop_time");
	}

	public void dispose() {
		// TODO Auto-generated method stub
		world.dispose();
	}
	
	public static void main(String[] args) throws IOException{
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
		
		try{
			Files.deleteIfExists(new File(System.getProperty("user.home")+"/GLCraft/GLCraft.log").toPath());
		}catch(FileSystemException e){
			JOptionPane.showMessageDialog(null, "You can only run one GLCraft instance at a time.", "GLCraft", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		Files.createDirectories(new File(System.getProperty("user.home")+"/GLCraft").toPath());
		FileOutputStream lfos = new FileOutputStream(System.getProperty("user.home")+"/GLCraft/GLCraft.log");
		TeeOutputStream otos = new TeeOutputStream(System.out,lfos);
		TeeOutputStream etos = new TeeOutputStream(System.err,lfos);
		System.setErr(new PrintStream(etos));
		System.setOut(new PrintStream(otos));
		
		glcraft = new GLCraft();
		
	}
	
	public static void devEnvironment(Plugin p, boolean loadExtPlugins){
		try {
			isDevEnvironment = true;
			GLCraft.loadExtPlugins = loadExtPlugins;
			glcraft = new GLCraft(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
