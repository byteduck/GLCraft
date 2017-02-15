/*
    GLCraft - A simple voxel game made with LWJGL
    Copyright (C) 2016 Aaron Sonin

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.codepixl.GLCraft;

import com.nishu.utils.Color4f;
import com.nishu.utils.Screen;
import com.nishu.utils.Time;
import com.nishu.utils.Window;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIStartScreen;
import net.codepixl.GLCraft.network.Client;
import net.codepixl.GLCraft.network.Server;
import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.plugin.PluginManager;
import net.codepixl.GLCraft.render.*;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.texturepack.TexturePackManager;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.SettingsManager;
import net.codepixl.GLCraft.util.logging.CrashHandlerWindow;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.util.logging.TeeOutputStream;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureImpl;

import java.awt.*;
import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.FileSystemException;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class GLCraft extends Screen{
	private CentralManager clientCentralManager, serverCentralManager;
	private WorldManager clientWorldManager, serverWorldManager;
	private PluginManager pluginManager;
	private static GLCraft glcraft;
	public static boolean isDevEnvironment = false;
	public static boolean loadExtPlugins = true;
	public static final String version = "0.1";
	public static final String versionTag = "";
	public static final String fullVersion = version + " " + versionTag; 
	private Plugin devPlugin;
	public boolean spendRemainingTime = true;
	private Server server;
	private Client client;
	private boolean isServer = false;
	private static int xSize, ySize;
	private static float time;
	
	public static GLCraft getGLCraft(){
		return glcraft;
	}
	
	public GLCraft(boolean dedicatedServer) throws IOException, LWJGLException{
		commonInitializer(dedicatedServer);
	}
	
	private GLCraft(Plugin p) throws IOException, LWJGLException{
		devPlugin = p;
		commonInitializer(false);
		
	}
	
	/**
	 * Main code for the display.
	 */
	private void commonInitializer(boolean dedicatedServer) throws IOException, LWJGLException{
		if(new File("libs/natives").exists())
			System.setProperty("org.lwjgl.librarypath", new File("libs/natives").getAbsolutePath());
		else
			System.setProperty("org.lwjgl.librarypath", new File(".").getAbsolutePath());
		UncaughtExceptionHandler ueh = new UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread t, Throwable thr){
				System.err.println("Uncaught exception thrown in thread "+t.getName());
				thr.printStackTrace();
				new CrashHandlerWindow(t, thr);
			}
		};
		Thread.currentThread().setUncaughtExceptionHandler(ueh);
		Thread.setDefaultUncaughtExceptionHandler(ueh);
		this.isServer = dedicatedServer;
		if(!this.isServer){
			try{
				Files.deleteIfExists(new File(System.getProperty("user.home"),"GLCraft"+File.separator+"GLCraft.log").toPath());
			}catch(FileSystemException e){
				e.printStackTrace();
				//JOptionPane.showMessageDialog(null, "You can only run one GLCraft instance at a time.", "GLCraft", JOptionPane.ERROR_MESSAGE);
				//System.exit(0);
			}
			Files.createDirectories(new File(System.getProperty("user.home")+"/GLCraft").toPath());
			FileOutputStream lfos = new FileOutputStream(System.getProperty("user.home")+File.separator+"GLCraft"+File.separator+"GLCraft.log");
			TeeOutputStream otos = new TeeOutputStream(System.out,lfos);
			TeeOutputStream etos = new TeeOutputStream(System.err,lfos);
			System.setErr(new PrintStream(etos));
			System.setOut(new PrintStream(otos));
			
			GLogger.init(lfos);
			GLogger.rout.setSuppressWarnings(true);
			
			glcraft = this;
			
			Display.setFullscreen(false);
			Display.setDisplayMode(new DisplayMode(1000, 700));
			Display.setTitle("GLCraft "+fullVersion);
			Display.setIcon(new ByteBuffer[] {
			        loadIcon(GLCraft.class.getResource("/textures/icons/icon16.png")),
			        loadIcon(GLCraft.class.getResource("/textures/icons/icon32.png")),
			});
			Display.create(new PixelFormat(8,8,8));
			
			initGL();
			
			init();
			
			GLogger.rout.setSuppressWarnings(false);
			long ltime = Time.getTime();
			double secondCounter = 0;
			while(!Display.isCloseRequested()){
				ltime = Time.getTime();
				time+=Time.getDelta();
				update();
				render();
				Window.update();
				if(Constants.maxFPS > 1)
					Display.sync(Constants.maxFPS);
				secondCounter+=Time.getDelta();
				if(secondCounter > 1){
					Constants.FPS = (int) (1d/Time.getDelta());
					secondCounter = 0;
				}
				Constants.QFPS = (int) (1d/((Time.getTime()-ltime)/(double)Time.SECOND));
				//So basically what this piece of code does is it will spend (1000000000/(FPS))-TimeSpentUpdatingAndRendering nanoseconds rebuilding chunks every frame.
				if(Constants.QFPS > 0 && spendRemainingTime){
					long nsBuildingChunks = 1000000000/Constants.QFPS;
					long targTime = ltime+nsBuildingChunks;
					doRemainingTime(targTime);
				}
				Time.setDelta((Time.getTime()-ltime)/(double)Time.SECOND);
			}
			
			if(this.serverCentralManager != null && this.serverCentralManager.isOpen()){
				WorldManager.saveWorldBlocking();
				this.serverWorldManager.closeWorld("Closing", true);
			}
			
			System.exit(0);
		}else{
			try{
				Files.deleteIfExists(new File("GLCraft.log").toPath());
			}catch(FileSystemException e){
				e.printStackTrace();
				//JOptionPane.showMessageDialog(null, "You can only run one GLCraft instance at a time.", "GLCraft", JOptionPane.ERROR_MESSAGE);
				//System.exit(0);
			}
			
			FileOutputStream lfos = new FileOutputStream("GLCraft.log");
			TeeOutputStream otos = new TeeOutputStream(System.out,lfos);
			TeeOutputStream etos = new TeeOutputStream(System.err,lfos);
			System.setErr(new PrintStream(etos));
			System.setOut(new PrintStream(otos));
			
			GLogger.init(lfos);
			GLogger.rout.setSuppressWarnings(true);
			
			glcraft = this;
			
			initServer();
			
			long ltime = Time.getTime();
			while(true){
				ltime = Time.getTime();
				time+=Time.getDelta();
				update();
				Time.setDelta((Time.getTime()-ltime)/(double)Time.SECOND);
			}
		}
		
	}
	
	private void doRemainingTime(long targTime) {
	    //worldManager.rebuildNextChunk();
		while(System.nanoTime() < targTime){
		    clientWorldManager.rebuildNextChunk();
		}
	}

	public boolean isLocalServerRunning(){
		return this.serverCentralManager != null && this.serverCentralManager.isOpen();
	}

	@Override
	public void init() {
		
		SettingsManager.init();
		
		Constants.init(false);
		
		Constants.gatherSystemInfo();

		TexturePackManager.initTexturePacks();
		
		/**To initialize Tiles and items because they are static*/
		Tile.tileMap.toString();
		GLogger.log("---------------", LogSource.NONE);
		Item.itemMap.toString();
		
		initCamera();
		clientCentralManager = new CentralManager(false);
		//serverCentralManager = new CentralManager(true);
		clientWorldManager = clientCentralManager.getWorldManager();
		//serverWorldManager = serverCentralManager.getWorldManager();

		
		String pluginsFolder = Constants.GLCRAFTDIR+"/plugins";
		new File(pluginsFolder).mkdirs();
		pluginManager = new PluginManager(pluginsFolder);
		if(loadExtPlugins){
			pluginManager.loadPlugins();
		}
		if(isDevEnvironment){
			pluginManager.addDevPlugin(devPlugin);
		}
		TextureManager.generateAtlas(false);
		GUIManager.getMainManager().showGUI(new GUIStartScreen());
	}
	
	public void initServer(){

		SettingsManager.init();

		Constants.init(true);
		
		Constants.gatherSystemInfo();
		
		/**To initialize Tiles and items because they are static*/
		Tile.tileMap.toString();
		GLogger.log("---------------", LogSource.NONE);
		Item.itemMap.toString();
		
		serverCentralManager = new CentralManager(true);
		serverWorldManager = serverCentralManager.getWorldManager();
		
		String pluginsFolder = "plugins";
		new File(pluginsFolder).mkdirs();
		pluginManager = new PluginManager(pluginsFolder);
		if(loadExtPlugins){
			pluginManager.loadPlugins();
		}
		if(isDevEnvironment){
			pluginManager.addDevPlugin(devPlugin);
		}
		
		Constants.GAME_STATE = Constants.GAME;
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
		Constants.initGL();
		
		glViewport(0,0,Display.getWidth(),Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,Constants.getWidth()/Constants.getHeight(),0.001f, 1000f);
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
		glViewport(0,0,Constants.getWidth(),Constants.getHeight());
		glOrtho(0,Constants.getWidth(),Constants.getHeight(),0,-200,200);
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
		if(!getGLCraft().isServer){
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			Tesselator.drawString(Constants.getWidth()/2-Tesselator.getFontWidth(line1)/2,Constants.getHeight()/2-Tesselator.getFontHeight(line1)/2, line1);
			Tesselator.drawString(Constants.getWidth()/2-Tesselator.getFontWidth(line2)/2,Constants.getHeight()/2+Tesselator.getFontHeight(line2)/2+10, line2);
			Spritesheet.logo.bind();
			Shape.currentSpritesheet = Spritesheet.logo;
			glBegin(GL_QUADS);
			Shape.createCenteredRect2D(Constants.getWidth()/2,Constants.getHeight()/2-Tesselator.getFontHeight()*2-30, Color4f.WHITE, new float[]{0,0}, 200f, 100f);
			glEnd();
			TextureImpl.unbind();
			Display.update();
		}
	}

	@Override
	public void update() {
		DebugTimer.startTimer("loop_time");
		if(!this.isServer()) {
			if (!Display.isFullscreen()) {
				xSize = Display.getWidth();
				ySize = Display.getHeight();
			}
		}
		if(clientCentralManager != null)
			clientCentralManager.update();
		if(serverCentralManager != null)
			serverCentralManager.update();
		if(pluginManager != null)
			pluginManager.update();
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		clientCentralManager.render();
		DebugTimer.endTimer("loop_time");
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		clientCentralManager.dispose();
	}
	
	
	public static void main(String[] args) throws IOException, LWJGLException{
		boolean dedicatedServer = false;
		for(String arg : args){
			if(arg.equals("dedicatedServer"))
				dedicatedServer = true;
		}
		glcraft = new GLCraft(dedicatedServer);
	}
	
	public static void devEnvironment(Plugin p, boolean loadExtPlugins) throws LWJGLException{
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

	public WorldManager getWorldManager(boolean server) {
		if(server)
			return serverWorldManager;
		else
			return clientWorldManager;
	}

	public EntityManager getEntityManager(boolean server) {
		if(server)
			return serverWorldManager.getEntityManager();
		else
			return clientWorldManager.getEntityManager();
	}
	
	public CentralManager getCentralManager(boolean server){
		if(server)
			return serverCentralManager;
		else
			return clientCentralManager;
	}
	
	public Server getServer(){
		return this.server;
	}
	
	public Client getClient(){
		return this.client;
	}

	/**
	 * @return Returns if this GLCraft instance is a DEDICATED server.
	 */
	public boolean isServer() {
		return isServer;
	}
	
	public void setIsServer(boolean serv){
		this.isServer = serv;
	}

	public void setServer(Server server){
		this.server = server;
	}
	
	public void setClient(Client client){
		this.client = client;
	}

	public void prepareLocalServer(){
		if(this.serverCentralManager == null){
			this.serverCentralManager = new CentralManager(true);
			this.serverWorldManager = this.serverCentralManager.getWorldManager();
		}else{
			this.serverCentralManager.getServer().reinit();
		}
	}

	public void closeLocalServer(String reason) {
		this.serverWorldManager.closeWorld(reason, false);
	}

	public void disconnectFromServer(boolean quit) {
		clientWorldManager.closeWorld("", quit);
	}

	public void closeLocalServerNow(String reason) {
		this.serverWorldManager.closeWorldNow(reason);
	}

	public void toggleFullScreen(){
		try{
			if(Display.isFullscreen()){
				Display.setDisplayMode(new DisplayMode(xSize, ySize));
				Display.setFullscreen(false);
			}else{
				Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			}
			GUIManager.getMainManager().resize();
		}catch(LWJGLException e){}
	}

	/**
	 * @return The runtime, in seconds.
	 */
	public static float getTime(){
		return time;
	}

}
