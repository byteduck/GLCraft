package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.plugin.PluginManager;
import net.codepixl.GLCraft.plugin.PluginManagerWindow;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.tile.Tile;
import net.lingala.zip4j.core.ZipFile;

public class GUIStartScreen extends GUIScreen {

	private static final int MIDDLE = Constants.WIDTH / 2;
	private static final int SINGLEPLAYERY = (int) (Constants.HEIGHT * 0.3);
	private static final int MULTIPLAYERY = (int) (Constants.HEIGHT * 0.5);
	private static final int SERVERY = (int) (Constants.HEIGHT * 0.7);
	private static final int TEXTUREY = (int) (Constants.HEIGHT * 0.9);
	private static final int PLUGINMANAGERY = SERVERY;
	private static final int SOUNDY = MULTIPLAYERY;

	private GUIButton startButton, pluginManagerButton, soundButton, texturepackButton, quitButton;

	public GUIStartScreen() {
		startButton = new GUIButton("Singleplayer", MIDDLE, SINGLEPLAYERY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Constants.setState(Constants.GAME);
				Constants.world.getWorldManager().createWorld();
				glDisable(GL_TEXTURE_2D);
				GUIManager.getMainManager().closeGUI();
				return null;
			}
		});
		soundButton = new GUIButton("Play a sound", MIDDLE, SOUNDY, new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				return null;
			}

		});
		pluginManagerButton = new GUIButton("Plugin Manager", MIDDLE, SERVERY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				new PluginManagerWindow(GLCraft.getGLCraft().getPluginManager()).setVisible(true);
				return null;
			}
		});
		texturepackButton = new GUIButton("Texturepacks", MIDDLE, TEXTUREY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				JFileChooser jf = new JFileChooser();
				jf.setCurrentDirectory(new File(System.getProperty("user.home") + "/GLCraft/Texturepacks"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Compressed GLCraft Texturepack", "zip");
				jf.setFileFilter(filter);
				int returnVal = jf.showOpenDialog(null);
				File TP = null;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					TP = jf.getSelectedFile();
					File tempFolder = new File(System.getProperty("user.home") + "/GLCraft/Texturepacks/tmp/textures");
					tempFolder.delete();
					tempFolder.mkdirs();
					ZipFile zip = new ZipFile(TP);
					zip.extractAll(tempFolder.getAbsolutePath());
				}

				File texturepackInfo = new File(System.getProperty("user.home") + "GLCraft/Texturepacks/currentTP.txt");
				try {
					texturepackInfo.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(texturepackInfo.getAbsolutePath(), "UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				writer.println(TP.getName().substring(0, TP.getName().length()-4));
				writer.close();
				
				JOptionPane.showMessageDialog(null, "You must restart GLCraft for changes to take effect.");

				
				return null;
			}
		});
		quitButton = new GUIButton("Quit", (Constants.FONT.getWidth("quit") + 40) / 2, Constants.BTNHEIGHT / 2 + 10, new Callable<Void>() {
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
		this.addElement(texturepackButton);
	}

	@Override
	public void drawBG() {
		Spritesheet.atlas.bind();
		float[] texCoords = TextureManager.tile(Tile.Stone);
		glBegin(GL_QUADS);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex2f(0, 0);
		glTexCoord2f(texCoords[0] + Spritesheet.atlas.uniformSize(), texCoords[1]);
		glVertex2f(0, Constants.HEIGHT);
		glTexCoord2f(texCoords[0] + Spritesheet.atlas.uniformSize(), texCoords[1] + Spritesheet.atlas.uniformSize());
		glVertex2f(Constants.WIDTH, Constants.HEIGHT);
		glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.atlas.uniformSize());
		glVertex2f(Constants.WIDTH, 0);
		glEnd();
	}
}
