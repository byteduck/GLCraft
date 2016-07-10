package net.codepixl.GLCraft;

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
import static org.lwjgl.util.glu.GLU.gluPerspective;

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

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
import com.nishu.utils.Window;

import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.plugin.PluginManager;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.logging.CrashHandler;
import net.codepixl.GLCraft.util.logging.TeeOutputStream;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.tile.Tile;

public class GLCraft extends Screen{
	private CentralManager world;
	private GameLoop gameLoop;
	private PluginManager pluginManager;
	private static GLCraft glcraft;
	public static boolean isDevEnvironment = false;
	public static boolean loadExtPlugins = true;
	public static String version = "0.0.7";
	private Plugin devPlugin;
	
	public static GLCraft getGLCraft(){
		return glcraft;
	}
	
	public GLCraft() throws IOException{
		commonInitializer();
	}
	
	private GLCraft(Plugin p) throws IOException{
		devPlugin = p;
		commonInitializer();
		
	}
	
	private void commonInitializer() throws IOException{
		glcraft = this;
		Display.setIcon(new ByteBuffer[] {
		        loadIcon(GLCraft.class.getResource("/textures/icons/icon16.png")),
		        loadIcon(GLCraft.class.getResource("/textures/icons/icon32.png")),
		});
		Window.createWindow(1000, 700, "GLCraft", false);
		Display.setResizable(true);
		
		gameLoop = new GameLoop();
		gameLoop.setScreen(this); //THIS IS WHEN INITGL AND INIT ARE CALLED
		gameLoop.setDebugMode(false);
		gameLoop.start(60);
	}
	
	@Override
	public void init() {
		
		/**To initialize Tiles and items because they are static*/
		
		Tile.tileMap.toString();
		Item.itemMap.toString();
		
		initCamera();
		world = new CentralManager();
		String pluginsFolder = System.getProperty("user.home")+"/GLCraft/Plugins";
		new File(pluginsFolder).mkdirs();
		pluginManager = new PluginManager(pluginsFolder);
		if(loadExtPlugins){
			pluginManager.loadPlugins();
		}
		if(isDevEnvironment){
			pluginManager.addDevPlugin(devPlugin);
		}
		TextureManager.generateAtlas();
	}
	
	private void initCamera(){
		
	}
	
	public PluginManager getPluginManager(){
		return pluginManager;
	}

	@Override
	public void initGL() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/font/GLCraft.ttf")));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		glViewport(0,0,Display.getWidth(),Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,Constants.WIDTH/Constants.HEIGHT,0.001f, 1000f);
		glMatrixMode(GL_MODELVIEW);

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
		Display.update();
	}

	@Override
	public void update() {
		DebugTimer.startTimer("loop_time");
		world.update();
		pluginManager.update();
		// TODO Auto-generated method stub
	}

	@Override
	public void render() {
		if(Display.wasResized()){
			Constants.WIDTH = Display.getWidth();
			Constants.HEIGHT = Display.getHeight();
		}
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.0f,0.749019608f,1.0f,0.0f);
		world.render();
		DebugTimer.endTimer("loop_time");
	}

	@Override
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
