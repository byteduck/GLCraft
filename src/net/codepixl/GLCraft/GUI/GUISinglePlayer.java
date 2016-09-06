package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.data.saves.SaveLoadWindow;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUISinglePlayer extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int NEWWORLDY = (int) (Constants.HEIGHT*0.4);
	private static final int LOADWORLDY = (int) (Constants.HEIGHT*0.6);
	
	GUIButton newWorld, loadWorld;
	
	public GUISinglePlayer(){
		newWorld = new GUIButton("New World", MIDDLE, NEWWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String name = JOptionPane.showInputDialog("World Name:");
				if(name == null || name.trim().equals("")){}else{
					Constants.setState(Constants.GAME);
					Constants.world.getWorldManager().createWorld(name);
					glDisable(GL_TEXTURE_2D);
					GUIManager.getMainManager().closeGUI();
				}
				return null;
			}
		});
		
		loadWorld = new GUIButton("Load World", MIDDLE, LOADWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Constants.setState(Constants.GAME);
				String name = SaveLoadWindow.loadWorld(Constants.world.getWorldManager());
				Constants.world.getWorldManager().loadWorld(name);
				glDisable(GL_TEXTURE_2D);
				GUIManager.getMainManager().closeGUI();
				return null;
			}
		});
		
		addElement(newWorld);
		addElement(loadWorld);
	}
	
	@Override
	public void drawBG() {
		try{
			Spritesheet.atlas.bind();
			int howManyWide = (Constants.WIDTH/64)+1;
			int howManyTall = (Constants.HEIGHT/64)+1;
			float[] texCoords = TextureManager.tile(Tile.Stone);
			for(int x = 0; x < howManyWide*64; x+=64){
				for(int y = 0; y < howManyTall*64; y+=64){
					glBegin(GL_QUADS);
					glTexCoord2f(texCoords[0], texCoords[1]);
					glVertex2f(x, y);
					glTexCoord2f(texCoords[0] + Spritesheet.atlas.uniformSize(), texCoords[1]);
					glVertex2f(x, y+64);
					glTexCoord2f(texCoords[0] + Spritesheet.atlas.uniformSize(), texCoords[1] + Spritesheet.atlas.uniformSize());
					glVertex2f(x+64, y+64);
					glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.atlas.uniformSize());
					glVertex2f(x+64, y);
					glEnd();
				}
			}
		}catch(NullPointerException e){
			//This happens if we're in the middle of changing texturepacks
		}
	}
	
	@Override
	public void input(){
		super.input();
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			GUIManager.getMainManager().showGUI("startScreen");
	}
	
}
