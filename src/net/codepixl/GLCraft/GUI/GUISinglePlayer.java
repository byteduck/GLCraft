package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.lwjgl.opengl.GL11;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.GUI.Elements.GUILabel.Alignment;
import net.codepixl.GLCraft.GUI.Elements.GUIScrollBox;
import net.codepixl.GLCraft.GUI.Elements.GUITextBox;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveManager;

public class GUISinglePlayer extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int NEWWORLDX = (int) (Constants.WIDTH*0.7);
	private static final int NEWWORLDY = (int) (Constants.HEIGHT*0.925);
	private static final int LOADWORLDX = (int) (Constants.WIDTH*0.3);
	private static final int LOADWORLDY = (int) (Constants.HEIGHT*0.8);
	private static final int DELETEWORLDY = LOADWORLDY;
	private static final int DELETEWORLDX = (int) (Constants.WIDTH*0.7);
	private static final int TEXTBOXY = (int) (Constants.HEIGHT*0.925);
	private static final int TEXTBOXX = (int) (Constants.HEIGHT*0.3);
	private static final int LINEY = (int) (Constants.HEIGHT*0.85);
	
	private GUIButton newWorld, loadWorld, deleteWorld;
	private GUILabel title;
	private GUISave selectedSave;
	private GUIScrollBox scrollBox;
	private GUITextBox textBox;
	
	public GUISinglePlayer(){
		setDrawStoneBackground(true);
		
		newWorld = new GUIButton("New World", NEWWORLDX, NEWWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				String name = textBox.getText();
				if(name == null || name.trim().equals("")){}else{
					Constants.setState(Constants.GAME);
					GLCraft.getGLCraft().getWorldManager().createWorld(name);
					glDisable(GL_TEXTURE_2D);
					GUIManager.getMainManager().closeGUI(false);
				}
				return null;
			}
		});
		
		loadWorld = new GUIButton("Load World", LOADWORLDX, LOADWORLDY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if(!GLCraft.getGLCraft().getWorldManager().loadWorld(selectedSave.save)){
					return null;
				}
				Constants.setState(Constants.GAME);
				glDisable(GL_TEXTURE_2D);
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
		scrollBox.y = Constants.FONT.getHeight()*2+20;
		scrollBox.width = Constants.WIDTH-200;
		scrollBox.height = (int) (Constants.HEIGHT*0.75f-scrollBox.y);
		
		final String tbp = "   Enter New World Name   ";
		int tbtwidth = Constants.FONT.getWidth(tbp);
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
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(0, LINEY);
		GL11.glVertex2f(width, LINEY);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public void onClose(){
		GUIManager.getMainManager().showGUI("startScreen");
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
				s.width = Constants.WIDTH-240;
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
