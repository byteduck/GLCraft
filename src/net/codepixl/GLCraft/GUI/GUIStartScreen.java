package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.concurrent.Callable;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.plugin.PluginManagerWindow;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUIStartScreen extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int SINGLEPLAYERY = (int) (Constants.HEIGHT*0.3);
	private static final int MULTIPLAYERY = (int) (Constants.HEIGHT*0.5);
	private static final int SERVERY = (int) (Constants.HEIGHT*0.7);
	private static final int PLUGINMANAGERY = SERVERY;
	private static final int SOUNDY = MULTIPLAYERY;
	
	private GUIButton startButton, pluginManagerButton, soundButton, quitButton;
	
	public GUIStartScreen(){
		startButton = new GUIButton("Singleplayer", MIDDLE, SINGLEPLAYERY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Constants.setState(Constants.GAME);
				if(GLCraft.IS_SERVER) Constants.world.getWorldManager().createWorld();
				else Constants.world.getWorldManager().fetchLocalWorld();
				glDisable(GL_TEXTURE_2D);
				GUIManager.getMainManager().closeGUI();
				return null;
			}
		});
		soundButton = new GUIButton("Play a sound", MIDDLE, SOUNDY, new Callable<Void>(){

			@Override
			public Void call() throws Exception {
				return null;
			}
			
		});
		pluginManagerButton = new GUIButton("Plugin Manager", MIDDLE, SERVERY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				new PluginManagerWindow(GLCraft.getGLCraft().getPluginManager()).setVisible(true);
				return null;
			}
		});
		quitButton = new GUIButton("Quit", (Constants.FONT.getWidth("quit")+40)/2, Constants.BTNHEIGHT/2+10, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				System.exit(0);
				return null;
			}
		});
		this.addElement(quitButton);
		this.addElement(startButton);
		this.addElement(soundButton);
		this.addElement(pluginManagerButton);
	}
	
	@Override
	public void drawBG(){
		Spritesheet.atlas.bind();
		float[] texCoords = TextureManager.tile(Tile.Stone);
		glBegin(GL_QUADS);
			glTexCoord2f(texCoords[0],texCoords[1]);
			glVertex2f(0,0);
			glTexCoord2f(texCoords[0]+Spritesheet.atlas.uniformSize(),texCoords[1]);
			glVertex2f(0,Constants.HEIGHT);
			glTexCoord2f(texCoords[0]+Spritesheet.atlas.uniformSize(),texCoords[1]+Spritesheet.atlas.uniformSize());
			glVertex2f(Constants.WIDTH,Constants.HEIGHT);
			glTexCoord2f(texCoords[0],texCoords[1]+Spritesheet.atlas.uniformSize());
			glVertex2f(Constants.WIDTH,0);
		glEnd();
	}
	
}
