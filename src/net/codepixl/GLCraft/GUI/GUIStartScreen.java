package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.plugin.PluginManagerWindow;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;

public class GUIStartScreen extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int SINGLEPLAYERY = (int) (Constants.HEIGHT*0.3);
	private static final int MULTIPLAYERY = (int) (Constants.HEIGHT*0.5);
	private static final int SERVERY = (int) (Constants.HEIGHT*0.7);
	private static final int PLUGINMANAGERY = SERVERY;
	
	private GUIButton startButton, pluginManagerButton;
	
	public GUIStartScreen(){
		startButton = new GUIButton("Singleplayer", MIDDLE, SINGLEPLAYERY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Constants.setState(Constants.GAME);
				Constants.world.getWorldManager().createWorld();
				glDisable(GL_TEXTURE_2D);
				GUIManager.getMainManager().closeGUI();
				return null;
			}
		});
		pluginManagerButton = new GUIButton("Plugin Manager", MIDDLE, SERVERY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				new PluginManagerWindow(GLCraft.getGLCraft().getPluginManager()).setVisible(true);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
		this.addElement(startButton);
		this.addElement(pluginManagerButton);
	}
	
	@Override
	public void drawBG(){
		Spritesheet.tiles.bind();
		glBegin(GL_QUADS);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*2, Spritesheet.tiles.uniformSize());
			glVertex2f(0,0);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*3, Spritesheet.tiles.uniformSize());
			glVertex2f(0,Constants.HEIGHT);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*3, 0);
			glVertex2f(Constants.WIDTH,Constants.HEIGHT);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*2, 0);
			glVertex2f(Constants.WIDTH,0);
		glEnd();
	}
	
}
