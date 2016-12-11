package net.codepixl.GLCraft.GUI;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.*;
import net.codepixl.GLCraft.GUI.Elements.GUILabel.Alignment;
import net.codepixl.GLCraft.network.Client;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;

public class GUISinglePlayer extends GUIScreen{
	
	private GUIButton newWorld, loadWorld, deleteWorld;
	private GUILabel title;
	private GUISave selectedSave;
	private GUIScrollBox scrollBox;
	private GUITextBox textBox;
	private int LINEY = (int) (Constants.getHeight()*0.85);
	
	public void makeElements(){
		final int MIDDLE = Constants.getWidth()/2;
		final int NEWWORLDX = (int) (Constants.getWidth()*0.7);
		final int NEWWORLDY = (int) (Constants.getHeight()*0.925);
		final int LOADWORLDX = (int) (Constants.getWidth()*0.3);
		final int LOADWORLDY = (int) (Constants.getHeight()*0.8);
		final int DELETEWORLDY = LOADWORLDY;
		final int DELETEWORLDX = (int) (Constants.getWidth()*0.7);
		final int TEXTBOXY = (int) (Constants.getHeight()*0.925);
		final int TEXTBOXX = (int) (Constants.getHeight()*0.3);
		LINEY = (int) (Constants.getHeight()*0.85);
		setDrawStoneBackground(true);
		
		newWorld = new GUIButton("New World", NEWWORLDX, NEWWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String name = textBox.getText();
				if(name == null || name.trim().equals("")){}else{
					Constants.setState(Constants.GAME);
					GLCraft.getGLCraft().getWorldManager(false).createBlankWorld();
					GLCraft.getGLCraft().prepareLocalServer();
					GLCraft.getGLCraft().getWorldManager(true).createWorld(name);
					Client.ServerConnectionState cs = GLCraft.getGLCraft().getCentralManager(false).connectToLocalServer();
					if(!cs.success){
						GLCraft.getGLCraft().closeLocalServer("Error opening world");
						GUIManager.getMainManager().showGUI(new GUIServerError("Error connecting to server:\n",cs.message));
						return null;
					}
					glDisable(GL_TEXTURE_2D);
					GUIManager.getMainManager().clearGUIStack();
					GUIManager.getMainManager().closeGUI(false);
				}
				return null;
			}
		});
		
		loadWorld = new GUIButton("Load World", LOADWORLDX, LOADWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				GLCraft.getGLCraft().getWorldManager(false).createBlankWorld();
				GLCraft.getGLCraft().prepareLocalServer();
				if(!GLCraft.getGLCraft().getWorldManager(true).loadWorld(selectedSave.save)){
					return null;
				}
				Client.ServerConnectionState cs = GLCraft.getGLCraft().getCentralManager(false).connectToLocalServer();
				if(!cs.success){
					GLCraft.getGLCraft().closeLocalServer("Error opening world");
					GUIManager.getMainManager().showGUI(new GUIServerError("Error connecting to server:\n",cs.message));
					return null;
				}
				Constants.setState(Constants.GAME);
				glDisable(GL_TEXTURE_2D);
				GUIManager.getMainManager().clearGUIStack();
				GUIManager.getMainManager().closeGUI(false);
				return null;
			}
		});
		loadWorld.setEnabled(false);
		
		deleteWorld = new GUIButton("Delete World", DELETEWORLDX, DELETEWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if(selectedSave != null)
					GUIManager.getMainManager().showGUI(new GUIDeleteWorld(selectedSave.save));
				return null;
			}
		});
		deleteWorld.setEnabled(false);
		
		title = new GUILabel(MIDDLE, 10, "Singleplayer");
		title.size = 2.0f;
		title.alignment = Alignment.CENTER;
		
		scrollBox = new GUIScrollBox(10);
		scrollBox.x = 100;
		scrollBox.y = Tesselator.getFontHeight()*2+20;
		scrollBox.width = Constants.getWidth()-200;
		scrollBox.height = (int) (Constants.getHeight()*0.75f-scrollBox.y);
		
		final String tbp = "   Enter New World Name   ";
		int tbtwidth = Tesselator.getFontWidth(tbp);
		textBox = new GUITextBox(TEXTBOXX, TEXTBOXY, tbtwidth, tbp);
		textBox.y-=textBox.height/2;
		
		addElement(newWorld);
		addElement(loadWorld);
		addElement(title);
		addElement(scrollBox);
		addElement(textBox);
		addElement(deleteWorld);
		
	}
	
	@Override
	public void drawBG(){
		super.drawBG();
		GL11.glLineWidth(2f);
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(0, LINEY);
		GL11.glVertex2f(width, LINEY);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public void onOpen(){
		super.onOpen();
		this.selectedSave = null;
		this.loadWorld.setEnabled(false);
		this.deleteWorld.setEnabled(false);
		scrollBox.clearItems();
		try {
			Save[] saves = SaveManager.getSaves();
			int i = 0;
			for(Save save : saves){
				GUISave s = new GUISave(save, this);
				s.x = 10;
				s.width = Constants.getWidth()-240;
				scrollBox.addItem(s);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSelectedSave(GUISave s){
		if(selectedSave !=null){
			selectedSave.selected = false;
		}else{
			loadWorld.setEnabled(true);
			deleteWorld.setEnabled(true);
		}
		selectedSave = s;
		selectedSave.selected = true;
	}

	public void loadSave(GUISave guiSave) {
		loadWorld.invokeClick();
	}
	
}
