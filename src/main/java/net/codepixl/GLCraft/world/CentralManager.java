package net.codepixl.GLCraft.world;

import com.google.common.io.Files;
import com.nishu.utils.Color4f;
import com.nishu.utils.Screen;
import com.nishu.utils.Time;
import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.*;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.GUI.Inventory.GUICrafting;
import net.codepixl.GLCraft.GUI.Inventory.GUICraftingAdvanced;
import net.codepixl.GLCraft.network.Client;
import net.codepixl.GLCraft.network.Server;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketMultiPacket;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.sound.SoundManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.util.command.CommandManager;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveManager;
import net.codepixl.GLCraft.util.logging.CrashHandler;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.Recipe.InvalidRecipeException;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.mob.AI.pathfinding.Pathfinder;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class CentralManager extends Screen{
	
	public boolean renderDebug = false;
	TrueTypeFont font;
	private WorldManager worldManager;
	private int currentBlock;
	private PipedInputStream actionsToDo = new PipedInputStream();
	public GUIManager guiManager;
	private SoundManager soundManager;
	private double messageTime = 0;
	private String message = "";
	public boolean isServer;
	private Server server;
	private Client client;
	public CommandManager commandManager;
	private Spritesheet currentWeatherSpritesheet;
	private float currentRainSpeed;
	
	private Pathfinder pathfinder;
	private Vector3i pathfindPos = new Vector3i(0,0,0);

	private boolean renderingSplashText;
	private String splashLine1;
	private int splashPercent;
	private String splashLine2;

	private boolean renderSplashPercent;
	
	private ArrayList<Packet> toSend = new ArrayList<Packet>();
	
	public static final int AIRCHUNK = 0, MIXEDCHUNK = 1;

	public CentralManager(boolean server){
		isServer = server;
		if(!server)
			initGL();
		init();
	}
	
	@Override
	public void dispose(){
		Display.destroy();
		System.exit(0);
	}

	@Override
	public void init() {
		if(!isServer){
			initGUIManager();
			Constants.generateStars();
			TextureManager.initTextures();
		}else{
			this.commandManager = new CommandManager(this);
		}
		
		try {
			CraftingManager.initRecipes();
		} catch (InvalidRecipeException e) {
			e.printStackTrace();
		}
		
		worldManager = new WorldManager(this, isServer);
		
		if(!isServer){
			guiManager.setGameGUI(new GUIGame(worldManager));
			GLCraft.renderSplashText("Starting Central Manager...", "Starting Sound System");
			soundManager = new SoundManager();
			SoundManager.setMainManager(soundManager);
		}
		
		try{
			if(isServer)
				if(GLCraft.getGLCraft().isServer())
					try{
						server = new Server(worldManager, Server.DEFAULT_SERVER_PORT);
					}catch(IOException e){
						e.printStackTrace();
						System.exit(0);
					}
				else
					server = new Server(worldManager);
			else{
				client = new Client(worldManager);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		if(GLCraft.getGLCraft().isServer()){ //If is dedicated
			try {
				Save s = SaveManager.getDedicatedSave();
				if(!s.version.equals("?")){
					worldManager.loadWorld(s);
				}else{
					worldManager.createWorld("world", true);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
	}

	@Override
	public void initGL() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glEnable(GL_CULL_FACE);
	}

	private void input(){
		if(!isServer){
			guiManager.input();
			if(!Mouse.isGrabbed() && Mouse.isButtonDown(0) && guiManager.mouseShouldBeGrabbed()){
				Mouse.setGrabbed(true);
			}else if(!guiManager.mouseShouldBeGrabbed()){
				Mouse.setGrabbed(false);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_C)){
				CrashHandler.invokeCrash();
			}
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){
					if(Keyboard.isKeyDown(Keyboard.KEY_F11))
						GLCraft.getGLCraft().toggleFullScreen();
					if(Keyboard.isKeyDown(Keyboard.KEY_F3)){
						renderDebug = !renderDebug;
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
						GUIScreen g = guiManager.getCurrentGUI();
						if(guiManager.isGUIOpen() && g.canBeExited()){
							guiManager.closeGUI(true);
							Mouse.setGrabbed(true);
						}else if(!guiManager.isGUIOpen()){
							guiManager.showGUI(new GUIPauseMenu());
						}
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_E)){
						if(guiManager.getCurrentGUI() instanceof GUICrafting || guiManager.getCurrentGUI() instanceof GUICraftingAdvanced){
							guiManager.closeGUI(true);
							Mouse.setGrabbed(true);
						}else{
							guiManager.showGUI(new GUICrafting(worldManager.getPlayer()));
						}
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_T)){
						if(!guiManager.isGUIOpen())
							guiManager.showGUI(new GUIChat(worldManager));
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_SLASH)){
						if(!guiManager.isGUIOpen()){
							GUIChat g = new GUIChat(worldManager);
							g.initialText = "/";
							guiManager.showGUI(g);
						}
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_F2)){
						takeScreenshot();
					}
					Vector3f pos = worldManager.entityManager.getPlayer().getPos();
					if(Keyboard.isKeyDown(Keyboard.KEY_O)){
						pathfindPos = new Vector3i(pos);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_P)){
						pathfinder = new Pathfinder(new Vector3i(pos), pathfindPos, worldManager);
						pathfinder.pathfind(1000);
					}
					/*if(Keyboard.isKeyDown(Keyboard.KEY_SEMICOLON)){
						worldManager.entityManager.add(new EntityTestAnimal(pos, worldManager));
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_APOSTROPHE)){
						worldManager.entityManager.add(new EntityTestHostile(pos, worldManager));
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_L)){
						Chunk c = worldManager.getChunk(pos);
						GLogger.log(worldManager.getLight((int)pos.x, (int)pos.y, (int)pos.z), LogSource.CLIENT);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_G)){
						try {
							Server s = new Server(worldManager, Server.DEFAULT_SERVER_PORT);
							worldManager.isServer = false;
							Client c = new Client(worldManager, Client.DEFAULT_CLIENT_PORT);
							Client.ServerConnectionState cs = c.connectToServer(InetAddress.getLocalHost(), s.getPort());
							Logger.log("Connection was a success: "+cs.success);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}*/
					/*Vector3f pos = worldManager.entityManager.getPlayer().getPos();
					if(Keyboard.isKeyDown(Keyboard.KEY_F)){
						worldManager.setTileAtPos(pos, Tile.Furnace.getId(), true);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_P)){
						pathfinder = new Pathfinder(new Vector3i(pos), pathfindPos, worldManager);
						pathfinder.pathfind(1000);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_O)){
						pathfindPos = new Vector3i(pos);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_B)){
						worldManager.setTileAtPos(pos, Tile.BluestoneOre.getId(), true);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_M)){
						Logger.log(worldManager.getMetaAtPos((int)pos.x, (int)pos.y, (int)pos.z));
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
					if(Keyboard.isKeyDown(Keyboard.KEY_G)){
						worldManager.entityManager.getPlayer().addToInventory(new ItemStack(Tile.Grass, 64));
					}*/
				}
			}
		}
	}
	
	public void showMessage(double duration, String message){
		this.messageTime = duration;
		this.message = message;
	}
	
	@Override
	public void render() {
		if(!isServer){
			DebugTimer.getTimer("total_render").start();
			if(Constants.GAME_STATE == Constants.GAME && !renderingSplashText){
				glLoadIdentity();
				render3D();
				worldManager.render();
				currentBlock = raycast();
				renderSky();
				if(this.renderDebug && this.pathfinder != null && this.pathfinder.path.size() > 0)
					this.pathfinder.renderPath();
				renderEtc();
				//renderInventory();
			}
			render2D();
			DebugTimer.startTimer("entity_render");
			if(Constants.GAME_STATE == Constants.GAME)
				worldManager.entityManager.getPlayer().renderHeldItem();
			DebugTimer.endTimer("entity_render");
			guiManager.render();
			if(Constants.GAME_STATE == Constants.GAME)
				renderText();
			if(renderingSplashText){
				if(renderSplashPercent)
					this.renderSplashText(splashLine1, splashLine2, splashPercent);
				else
					this.renderSplashText(splashLine1, splashLine2);
			}
			glDisable(GL_TEXTURE_2D);
			DebugTimer.getTimer("total_render").end();
		}
	}

	private void renderEtc() {
		render2D();
		if(worldManager.isSaving()){
			Spritesheet.atlas.bind();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_QUADS);
			Shape.createSquare(Constants.getWidth()-42, 10, Color4f.WHITE, TextureManager.texture("misc.floppy"), 32);
			GL11.glEnd();
		}
		drawCrosshair();
	}

	private int raycast(){
		return worldManager.getEntityManager().getPlayer().raycast();
	}
	
	private void renderSky(){
		
		Spritesheet.atlas.bind();
		Shape.currentSpritesheet = Spritesheet.atlas;
		
		GL11.glPushMatrix();
		GL11.glTranslatef(worldManager.getEntityManager().getPlayer().getX(), worldManager.getEntityManager().getPlayer().getY(), worldManager.getEntityManager().getPlayer().getZ());
		GL11.glRotatef((worldManager.getWorldTime() - (Constants.dayLengthMS/24*5f) % Constants.dayLengthMS)/(float)Constants.dayLengthMS*360f,0.0f,0.0f,1.0f);
	    GL11.glTranslatef(500f, -37.5f, -37.5f);
	    GL11.glBegin(GL11.GL_QUADS);
	    Shape.createCube(0, 0, 0, Color4f.WHITE, TextureManager.texture("misc.sun"), 75f);
	    GL11.glEnd();
	    GL11.glPopMatrix();
	    
	    GL11.glPushMatrix();
		GL11.glTranslatef(worldManager.getEntityManager().getPlayer().getX(), worldManager.getEntityManager().getPlayer().getY(), worldManager.getEntityManager().getPlayer().getZ());
		GL11.glRotatef((worldManager.getWorldTime() - (Constants.dayLengthMS/24*18f) % Constants.dayLengthMS)/(float)Constants.dayLengthMS*360f,0.0f,0.0f,1.0f);
	    GL11.glTranslatef(500f, -37.5f, -37.5f);
	    GL11.glBegin(GL11.GL_QUADS);
	    Shape.createCube(0, 0, 0, Color4f.WHITE, TextureManager.texture("misc.moon"), 75f);
	    GL11.glEnd();
	    GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPointSize(1f);
		GL11.glPushMatrix();
		GL11.glTranslatef(worldManager.getEntityManager().getPlayer().getX(), worldManager.getEntityManager().getPlayer().getY(), worldManager.getEntityManager().getPlayer().getZ());
		GL11.glRotatef((worldManager.getWorldTime() - (Constants.dayLengthMS/24*5f) % Constants.dayLengthMS)/(float)Constants.dayLengthMS*360f, 0, 0, 1);
		GL11.glBegin(GL11.GL_POINTS);
		float intensity = (-worldManager.getSkyLightIntensity(true)+0.5f)*2f;
		GL11.glColor4f(1f, 1f, 1f, intensity);
		for(int i = 0; i < Constants.stars.length; i++){
	    	GL11.glVertex3f(Constants.stars[i].x, Constants.stars[i].y, Constants.stars[i].z);
	    }
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		//Spritesheet.clouds.bind();
		//Shape.currentSpritesheet = Spritesheet.clouds;
		GL11.glPushMatrix();
		//GL11.glRotatef(-90f, 1f, 0f, 0f);
		worldManager.cloudShader.use();
		GL20.glUniform1f(GL20.glGetUniformLocation(worldManager.cloudShader.getProgram(), "time"), GLCraft.getTime());
		GL20.glUniform1f(GL20.glGetUniformLocation(worldManager.cloudShader.getProgram(), "cover"), worldManager.getCloudDensity());
		GL20.glUniform1f(GL20.glGetUniformLocation(worldManager.cloudShader.getProgram(), "sharpness"), 0.01f);
		GL20.glUniform1f(GL20.glGetUniformLocation(worldManager.cloudShader.getProgram(), "speed"), 0.005f);
		GL11.glBegin(GL_QUADS);
		float lightIntensity = worldManager.getSkyLightIntensity()*worldManager.getCloudDarkness();
		Shape.createTexturelessFlat(-1000f, 127f, -1000f, new Color4f(lightIntensity,lightIntensity,lightIntensity,0.5f), 2000f);
		GL11.glEnd();
		worldManager.cloudShader.release();
		GL11.glPopMatrix();

		if(worldManager.getPlayer().canBreathe() && worldManager.getPrecipitationOpacity() > 0) renderWeather();
		
		//Spritesheet.atlas.bind();
		//Shape.currentSpritesheet = Spritesheet.atlas;
	}

	private void renderWeather(){
		EntityPlayer p = worldManager.getPlayer();
		if(worldManager.currentWeather.type.precipitationSpritesheet != null)
			currentWeatherSpritesheet = worldManager.currentWeather.type.precipitationSpritesheet;
		if(currentWeatherSpritesheet != null)
			currentWeatherSpritesheet.bind();
		glPushMatrix();
		glEnable (GL_BLEND);
		glCullFace(GL_BACK);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Cylinder c = new Cylinder();
		float light = worldManager.getSkyLightIntensity()*2;
		GL11.glTranslatef((int)p.getX()+0.5f, 0, (int)p.getZ()+0.55f);
		c.setDrawStyle(GLU_FILL);
		c.setTextureFlag(true);
		c.setNormals(GLU_SMOOTH);
		GL11.glColor4f(light, light, light, worldManager.getPrecipitationOpacity());
		for(int x = 10; x >= 0; x--){
			for(int z = 10; z >= 0; z--){
				if(!(x ==0 && z == 0)) {
					drawPrecipitationColumn(x, z, p, c);
					drawPrecipitationColumn(-x, z, p, c);
					drawPrecipitationColumn(-x, -z, p, c);
					drawPrecipitationColumn(x, -z, p, c);
				}else{
					glCullFace(GL_FRONT);
					drawPrecipitationColumn(x, z-0.05f, p, c);
					glCullFace(GL_BACK);
				}
			}
		}
		glPopMatrix();
		Spritesheet.atlas.bind();
	}

	private void drawPrecipitationColumn(float x, float z, EntityPlayer p, Cylinder c){
		int y = 0;
		while(!worldManager.openToSky(new Vector3f(p.getX()+x, y, p.getZ()+z))) y++;
		glMatrixMode(GL_TEXTURE);
		if(worldManager.currentWeather.type.rainSpeed != 0)
			currentRainSpeed = worldManager.currentWeather.type.rainSpeed;
		glTranslatef(0, ((GLCraft.getTime()  * currentRainSpeed) % 1 + z / 7f), 0);
		glScalef(4, 13, 1);
		glMatrixMode(GL_MODELVIEW);
		glTranslatef(x,y,z);
		GL11.glRotatef(-90, 1, 0, 0);
		glRotatef(-45, 0, 0, 1);
		if(!((int)x == 0 && (int)z == 0)) c.draw(0.5f, 0.6f, 127f, 4, 1);
		else c.draw(0.6f, 0.6f, 127f, 50, 1);
		glRotatef(45, 0, 0, 1);
		GL11.glRotatef(90, 1, 0, 0);
		glTranslatef(-x,-y,-z);
		glMatrixMode(GL_TEXTURE);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
	}

	@Deprecated
	private void renderInventory() {
		glLoadIdentity();
		render2D();
		float SIZE = (float)Constants.getWidth()/18f;
		float HEARTSIZE = (float)Constants.getWidth()/36f;
		float SPACING = (float)Constants.getWidth()/36f;
		float HEARTSPACING = (float)Constants.getWidth()/72f;
		float BUBBLESIZE = HEARTSIZE;
		float BUBBLESPACING = HEARTSPACING;
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
			Shape.createCenteredSquare(Constants.getWidth()/2f, Constants.getHeight()/2f, new Color4f(1f,1f,1f,1f), texCoords, Constants.getWidth());
			glEnd();
			glPopMatrix();
		}else if(!p.canBreathe()){
			glPushMatrix();
			glBegin(GL_QUADS);
			Shape.createCenteredSquare(Constants.getWidth()/2f, Constants.getHeight()/2f, new Color4f(1f,1f,1f,1f), Tile.Water.getIconCoords(), Constants.getWidth());
			glEnd();
			glPopMatrix();
		}
		for(float i = 0; i < 9f; i++){
			GUISlot s = new GUISlot((int)(Constants.getWidth()/9f+i*SIZE+i*SPACING+SIZE/2f),(int)(Constants.getHeight()-(SIZE/2f)),p);
			s.itemstack = p.getInventory((int)i);
			if(p.getSelectedSlot() == i)
				s.hover = true;
			s.render();
		}
		
		Spritesheet.atlas.bind();
		
		if(p.airLevel < 10f){
			glBegin(GL_QUADS);
			for(int i = 0; i < 10; i++){
				Shape.createCenteredSquare((float)Constants.getWidth()/9f+i*BUBBLESIZE+i*BUBBLESPACING+BUBBLESIZE/2f,Constants.getHeight()-(SIZE/2f)-BUBBLESIZE*3.5f, new Color4f(1,1,1,1), p.getTexCoordsForAirIndex(i), BUBBLESIZE);
			}
			glEnd();
		}
		
		glBegin(GL_QUADS);
		for(int i = 0; i < 10; i++){
			Shape.createCenteredSquare((float)Constants.getWidth()/9f+i*HEARTSIZE+i*HEARTSPACING+HEARTSIZE/2f,Constants.getHeight()-(SIZE/2f)-HEARTSIZE*2f, new Color4f(1,1,1,1), p.getTexCoordsForHealthIndex(i), HEARTSIZE);
		}
		glEnd();
	}
	
	public void takeScreenshot(){
		showMessage(2, "Took screenshot");
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = Display.getDisplayMode().getWidth();
		int height= Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
		String filename = new SimpleDateFormat("yyyy'-'MM'-'dd'_'hh'-'mm'-'ss'.png'").format(new Date());
		File file = new File(Constants.GLCRAFTDIR+"screenshots/"+filename); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		   
		for(int x = 0; x < width; x++) 
		{
		    for(int y = 0; y < height; y++)
		    {
		        int i = (x + (width * y)) * bpp;
		        int r = buffer.get(i) & 0xFF;
		        int g = buffer.get(i + 1) & 0xFF;
		        int b = buffer.get(i + 2) & 0xFF;
		        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
		    }
		}
		   
		try {
			Files.createParentDirs(file);
		    ImageIO.write(image, format, file);
		} catch (IOException e) { e.printStackTrace(); }
	}

	private void renderText(){
		render2D();
		glColor3f(1f,1f,1f);
		Tesselator.drawString(10, 10, "GLCraft Beta "+GLCraft.version+" "+GLCraft.versionTag);
		if(messageTime > 0){
			messageTime -= Time.getDelta();
			if(messageTime < 0)
				messageTime = 0;
			Tesselator.drawString(10, Tesselator.getFontHeight()+20, this.message);
		}
		if(!guiManager.isGUIOpen() && currentBlock != -1){
			String toolTip = "Block: "+Tile.getTile((byte)currentBlock).getName();
			Tesselator.drawString(Constants.getWidth()/2-Tesselator.getFontWidth(toolTip)/2, 10, toolTip);
		}
		if(renderDebug){
			EntityPlayer p = getEntityManager().getPlayer();
			Tesselator.drawString(10,Tesselator.getFontHeight()+10, "X: "+(int)p.getX()+" Y: "+(int)p.getY()+" Z: "+(int)p.getZ());
			Tesselator.drawString(10,Tesselator.getFontHeight()*2+10, "RotX: "+(int)p.getRot().x+" RotY: "+(int)p.getRot().y+" RotZ: "+(int)p.getRot().z);
			Tesselator.drawString(10,Tesselator.getFontHeight()*3+10, "Facing: "+p.getEnumFacing().removeUpDown());
			Tesselator.drawString(10,Tesselator.getFontHeight()*4+10, "FPS: "+Constants.FPS);
			Tesselator.drawString(10,Tesselator.getFontHeight()*5+10, "Entities: "+worldManager.entityManager.totalEntities());
			Tesselator.drawString(10,Tesselator.getFontHeight()*6+10, "Time: "+worldManager.getTime());
			Tesselator.drawString(10,Tesselator.getFontHeight()*7+10, "Weather: "+worldManager.currentWeather);
			Iterator<DebugTimer> i = DebugTimer.getTimers().iterator();
			int ind = 0;
			while(i.hasNext()){
				String next = i.next().toString();
				Tesselator.drawString(Constants.getWidth()-10-Tesselator.getFontWidth(next),Tesselator.getFontHeight()*ind+10, next);
				ind++;
			}
		}
		
		TextureImpl.unbind();
	}
	
	private void drawCrosshair() {
		GL11.glLineWidth(2f);
		GL11.glLogicOp(GL11.GL_INVERT);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(Constants.getWidth()/2-5, Constants.getHeight()/2);
		GL11.glVertex2d(Constants.getWidth()/2+5, Constants.getHeight()/2);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(Constants.getWidth()/2, Constants.getHeight()/2-5);
		GL11.glVertex2d(Constants.getWidth()/2, Constants.getHeight()/2-2);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2d(Constants.getWidth()/2, Constants.getHeight()/2+5);
		GL11.glVertex2d(Constants.getWidth()/2, Constants.getHeight()/2+2);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}

	public void render2D() {
		glCullFace(GL_BACK);
		glClearDepth(1);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glViewport(0, 0, Constants.getWidth(), Constants.getHeight());
		glOrtho(0, Constants.getWidth(), Constants.getHeight(), 0, -400, 400);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void render3D(){
		//setupLighting();
		glCullFace(GL_FRONT);
		glViewport(0,0,Constants.getWidth(),Constants.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(67f,(float)Constants.getWidth()/(float)Constants.getHeight(),0.1f, 1000f);
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
		if(!isServer){
			try {
				getClient().update();
			} catch (IOException e) {
				e.printStackTrace();
			}
			guiManager.update();
			guiManager.setShowGame(Constants.GAME_STATE == Constants.GAME);
			input();
		}else{
			commandManager.update();
		}
		
		try{
			if(toSend.size() > 1){
				PacketMultiPacket p = new PacketMultiPacket(toSend);
				if(isServer)
					server.sendToAllClients(p);
				else
					client.sendToServer(p);
			}else if(toSend.size() > 0)
				if(isServer)
					server.sendToAllClients(toSend.get(0));
				else
					client.sendToServer(toSend.get(0));
			toSend.clear();
		}catch(IOException e){e.printStackTrace();}
		
		if(Constants.GAME_STATE == Constants.GAME){
			worldManager.update();
		}
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
	
	public void finishSplashText(){
		this.renderingSplashText = false;
	}
	
	private void renderSplashText(String line1, String line2){
		if(!isServer){
			int CENTER = Constants.getWidth()/2;
			int HCENTER = Constants.getHeight()/2;
			glClearColor(0,0,0,1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			String ltext = "GLCraft is generating the world...";
			Tesselator.drawString(CENTER-Tesselator.getFontWidth(ltext)/2,30, ltext);
			Tesselator.drawString(CENTER-Tesselator.getFontWidth(line1)/2,HCENTER-Tesselator.getFontHeight(line1), line1);
			Tesselator.drawString(CENTER-Tesselator.getFontWidth(line2)/2,HCENTER+Tesselator.getFontHeight(line2), line2);
			TextureImpl.unbind();
		}
	}
	
	public void setSplashText(String line1, String line2, int percent){
		this.splashLine1 = line1;
		this.splashLine2 = line2;
		this.splashPercent = percent;
		this.renderSplashPercent = true;
		this.renderingSplashText = true;
	}
	
	public void setSplashText(String line1, String line2){
		this.splashLine1 = line1;
		this.splashLine2 = line2;
		this.renderSplashPercent = false;
		this.renderingSplashText = true;
	}
	
	private void renderSplashText(String line1, String line2, int percent){
		if(!isServer){
			int CENTER = Constants.getWidth()/2;
			int HCENTER = Constants.getHeight()/2;
			glClearColor(0,0,0,1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			String ltext = "GLCraft is loading...";
			Tesselator.drawString(CENTER-Tesselator.getFontWidth(ltext)/2,30, ltext);
			Tesselator.drawString(CENTER-Tesselator.getFontWidth(line1)/2,HCENTER-Tesselator.getFontHeight(line1), line1);
			Tesselator.drawString(CENTER-Tesselator.getFontWidth(line2)/2,HCENTER+Tesselator.getFontHeight(line2), line2);
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
		}
	}

	public Client.ServerConnectionState connectToLocalServer() throws UnknownHostException, IOException{
		return this.client.connectToServer(InetAddress.getLocalHost(), GLCraft.getGLCraft().getServer().getPort());
	}
	
	public Server getServer(){
		return server;
	}
	
	public Client getClient(){
		return client;
	}
	
	public void sendPacket(Packet p){
		toSend.add(p);
	}

	public void sendPacket(Packet p, EntityPlayerMP mp){
		try{
			if(isServer)
				server.sendToClient(p,mp);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public Client.ServerConnectionState connectToServer(InetAddress addr, int port) throws UnknownHostException, IOException{
		return client.connectToServer(addr, port);
	}

	public boolean isOpen() {
		return this.getServer().isOpen();
	}
}
