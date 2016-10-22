package net.codepixl.GLCraft.GUI;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.GUI.Elements.GUISlider;
import net.codepixl.GLCraft.GUI.Elements.GUITexture;
import net.codepixl.GLCraft.render.util.SettingsManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;

public class GUIPauseMenu extends GUIScreen{
	
	private GUIButton backButton,quitButton,exitButton,settingsButton;
	private GUITexture savingIcon;
	
	public static boolean isHost = false;
	
	@Override
	public void makeElements(){
		int BACKY = (int) (Constants.getHeight()*0.2);
		int SAVEY = (int) (Constants.getHeight()*0.4);
		int QUITY = (int) (Constants.getHeight()*0.6);
		int SETTINGSY = (int) (Constants.getHeight()*0.8);
		int MIDDLE = Constants.getWidth()/2;
		backButton = new GUIButton("Back to game", MIDDLE, BACKY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Mouse.setGrabbed(true);
				GUIManager.getMainManager().closeGUI(true);
				return null;
			}
		});
		
		quitButton = new GUIButton(isHost ? "Save and Quit" : "Disconnect and Quit", MIDDLE, QUITY, new Callable<Void>(){
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
		
		exitButton = new GUIButton(isHost ? "Save and Exit" : "Disconnect", MIDDLE, SAVEY, new Callable<Void>(){
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
				GUIManager.getMainManager().showGUI(new GUISettings());
				return null;
			}
		});
		
		savingIcon = new GUITexture("misc.floppy", Constants.getWidth()-42, 10, 32);
		savingIcon.visible = false;
		
		
		
		this.addElement(backButton);
		this.addElement(quitButton);
		this.addElement(exitButton);
		this.addElement(savingIcon);
		this.addElement(settingsButton);
	}
	
	@Override
	public void drawBG(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.1f,0.1f,0.1f,0.5f);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(0,0);
			GL11.glVertex2f(0,Constants.getHeight());
			GL11.glVertex2f(Constants.getWidth(),Constants.getHeight());
			GL11.glVertex2f(Constants.getWidth(),0);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	@Override
	public void update(){
		super.update();
		this.savingIcon.visible = GLCraft.getGLCraft().getWorldManager(false).isSaving();
	}
}
