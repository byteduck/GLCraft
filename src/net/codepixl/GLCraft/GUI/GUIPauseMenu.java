package net.codepixl.GLCraft.GUI;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;

public class GUIPauseMenu extends GUIScreen{
	
	private static final int BACKY = (int) (Constants.HEIGHT*0.3);
	private static final int SLOTY = (int) (Constants.HEIGHT*0.5);
	private static final int QUITY = (int) (Constants.HEIGHT*0.7);
	private static final int MIDDLE = Constants.WIDTH/2;
	
	private GUIButton backButton,quitButton;
	
	public GUIPauseMenu(){
		backButton = new GUIButton("Back to game", MIDDLE, BACKY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				Mouse.setGrabbed(true);
				GUIManager.getMainManager().closeGUI();
				return null;
			}
		});
		
		quitButton = new GUIButton("Quit", MIDDLE, QUITY, new Callable<Void>(){

			@Override
			public Void call() throws Exception {
				System.exit(0);
				return null;
			}
		});
		
		this.addElement(backButton);
		this.addElement(quitButton);
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
}
