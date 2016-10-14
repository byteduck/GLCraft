package net.codepixl.GLCraft.GUI;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.GUI.Elements.GUISlider;
import net.codepixl.GLCraft.render.util.SettingsManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;

public class GUIPauseMenu extends GUIScreen{
	
	private static final int BACKY = (int) (Constants.HEIGHT*0.2);
	private static final int SAVEY = (int) (Constants.HEIGHT*0.4);
	private static final int QUITY = (int) (Constants.HEIGHT*0.6);
	private static final int SETTINGSY = (int) (Constants.HEIGHT*0.8);
	private static final int MIDDLE = Constants.WIDTH/2;
	
	private GUIButton backButton,quitButton,exitButton,settingsButton;
	private GUITexture savingIcon;
	
	private boolean isHost = false;
	
	public GUIPauseMenu(){
		backButton = new GUIButton("Back to game", MIDDLE, BACKY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Mouse.setGrabbed(true);
				GUIManager.getMainManager().closeGUI(true);
				return null;
			}
		});
		
		quitButton = new GUIButton("YOU SHOULD NOT SEE THIS", MIDDLE, QUITY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				if(isHost){
					WorldManager.saveWorldBlocking();
					GLCraft.getGLCraft().getWorldManager(true).closeWorld("Closing", true);
				}else
					GLCraft.getGLCraft().disconnectFromServer(true);
				return null;
			}
		});
		
		exitButton = new GUIButton("YOU SHOULD NOT SEE THIS", MIDDLE, SAVEY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				if(isHost){
					WorldManager.saveWorldBlocking();
					GLCraft.getGLCraft().getWorldManager(true).closeWorld("Closing", false);
				}else
					GLCraft.getGLCraft().disconnectFromServer(false);
				GUIManager.getMainManager().clearGUIStack();
				return null;
			}
		});
		
		settingsButton = new GUIButton("Settings", MIDDLE, SETTINGSY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				GUIManager.getMainManager().showGUI("settings");
				return null;
			}
		});
		
		savingIcon = new GUITexture("misc.floppy", Constants.WIDTH-42, 10, 32);
		savingIcon.visible = false;
		
		
		
		this.addElement(backButton);
		this.addElement(quitButton);
		this.addElement(exitButton);
		this.addElement(savingIcon);
		this.addElement(settingsButton);
	}
	
	public void setHost(boolean host){
		this.isHost = host;
		if(host){
			quitButton.setText("Save and Quit");
			exitButton.setText("Save and Exit");
		}else{
			quitButton.setText("Disconnect and Quit");
			exitButton.setText("Disconnect and Exit");
		}
	}
	
	@Override
	public void drawBG(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.1f,0.1f,0.1f,0.5f);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(0,0);
			GL11.glVertex2f(0,Constants.HEIGHT);
			GL11.glVertex2f(Constants.WIDTH,Constants.HEIGHT);
			GL11.glVertex2f(Constants.WIDTH,0);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public void update(){
		this.savingIcon.visible = GLCraft.getGLCraft().getWorldManager(false).isSaving();
	}
}
