package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveLoadWindow;
import net.codepixl.GLCraft.util.data.saves.SaveManager;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUISinglePlayer extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int NEWWORLDX = (int) (Constants.WIDTH*0.3);
	private static final int NEWWORLDY = (int) (Constants.HEIGHT*0.8);
	private static final int LOADWORLDX = (int) (Constants.WIDTH*0.7);
	private static final int LOADWORLDY = (int) (Constants.HEIGHT*0.8);
	
	GUIButton newWorld, loadWorld;
	
	public GUISinglePlayer(){
		newWorld = new GUIButton("New World", NEWWORLDX, NEWWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String name = JOptionPane.showInputDialog("World Name:", "GLCraft");
				if(name == null || name.trim().equals("")){}else{
					Constants.setState(Constants.GAME);
					Constants.world.getWorldManager().createWorld(name);
					glDisable(GL_TEXTURE_2D);
					GUIManager.getMainManager().closeGUI();
				}
				return null;
			}
		});
		
		loadWorld = new GUIButton("Load World", LOADWORLDX, LOADWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String name = SaveLoadWindow.loadWorld(Constants.world.getWorldManager());
				if(!Constants.world.getWorldManager().loadWorld(name)){
					return null;
				}
				Constants.setState(Constants.GAME);
				glDisable(GL_TEXTURE_2D);
				GUIManager.getMainManager().closeGUI();
				return null;
			}
		});
		
		addElement(newWorld);
		addElement(loadWorld);
		
		/*try {
			Save[] saves = SaveManager.getSaves();
			int i = 0;
			for(Save save : saves){
				GUILabel lbl = new GUILabel(save.dispName);
				lbl.x = 0;
				lbl.y = i*100;
				addElement(lbl);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
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
	public void onClose(){
		GUIManager.getMainManager().showGUI("startScreen");
	}
	
}
