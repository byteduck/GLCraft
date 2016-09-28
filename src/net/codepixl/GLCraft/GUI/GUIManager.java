package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUITextBox;
import net.codepixl.GLCraft.GUI.Inventory.GUIInventoryScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class GUIManager {

	private GUIScreen currentGUI;
	private boolean GUIOpen = false, showGame = false;
	private HashMap<String,GUIScreen> staticGUIs = new HashMap<String,GUIScreen>();
	private static GUIManager mainManager;
	private String currentGUIName = "nogui";
	private GUITextBox focusedTextBox;
	private GUIGame gameGUI;

	public GUIManager(){
		initTextures();
	}
	
	private void initTextures(){
		TextureManager.addTexture("gui.guislot", TextureManager.GUIS+"guislot.png");
		TextureManager.addTexture("gui.heart", TextureManager.GUIS+"heart.png");
		TextureManager.addTexture("gui.heart_half", TextureManager.GUIS+"heart_half.png");
		TextureManager.addTexture("gui.heart_empty", TextureManager.GUIS+"heart_empty.png");
		TextureManager.addTexture("gui.bubble", TextureManager.GUIS+"bubble.png");
	}
	
	public void setShowGame(boolean show){
		showGame = show;
	}

	public void setGameGUI(GUIGame gui){
		this.gameGUI = gui;
	}
	
	public static void setMainManager(GUIManager manager){
		mainManager = manager;
	}
	
	public static GUIManager getMainManager(){
		return mainManager;
	}

	public void showGUI(GUIScreen gui) {
		currentGUI = gui;
		gui.onOpen();
		GUIOpen = true;
		currentGUIName = "othergui";
	}
	
	public boolean showGUI(String guiName){
		if(staticGUIs.containsKey(guiName)){
			currentGUI = staticGUIs.get(guiName);
			staticGUIs.get(guiName).onOpen();
			GUIOpen = true;
			currentGUIName = guiName;
			return true;
		}
		return false;
	}
	
	public boolean isGUIOpen(){
		return GUIOpen;
	}
	
	public GUIScreen getGUI(String guiName){
		return staticGUIs.get(guiName);
	}
	
	public boolean hasGUI(String guiName){
		return staticGUIs.containsKey(guiName);
	}
	
	public void addGUI(GUIScreen gui, String guiName){
		staticGUIs.put(guiName, gui);
	}

	public void closeGUI(boolean onClose){
		GUIScreen tmp = currentGUI;
		GUIOpen = false;
		currentGUI = null;
		currentGUIName = "nogui";
		focusedTextBox = null;
		if(tmp != null && onClose)
			tmp.onClose();
	}
	
	public GUIScreen getCurrentGUI(){
		return currentGUI;
	}

	public void render() {
		if(showGame)
			gameGUI.renderMain();
		if (GUIOpen)
			currentGUI.renderMain();
		renderMouseItem();
		EntityPlayer p = GLCraft.getGLCraft().getEntityManager().getPlayer();
		if(p.mouseItem.isNull() && p.hoverSlot != null && p.hoverSlot.showLabel && p.hoverSlot.hover && !p.hoverSlot.itemstack.isNull()){
			TextureImpl.unbind();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_QUADS);
			Shape.createTexturelessRect2D(Mouse.getX(), -Mouse.getY()+Constants.HEIGHT-Constants.FONT.getHeight(), Constants.FONT.getWidth(p.hoverSlot.itemstack.getName())+4, Constants.FONT.getHeight()+4, new Color4f(0f, 0f, 0f, 0.5f));
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			Tesselator.drawTextWithShadow(Mouse.getX()+2, -Mouse.getY()+Constants.HEIGHT-Constants.FONT.getHeight()+2, p.hoverSlot.itemstack.getName());
		}
	}

	public void update(){
		if(showGame)
			gameGUI.update();
		if (GUIOpen)
			currentGUI.update();
	}

	public void input(){
		EntityPlayer p = GLCraft.getGLCraft().getEntityManager().getPlayer();
		p.hoverSlot = null;
		if (GUIOpen){
			currentGUI.input(0,0);
			if(currentGUI instanceof GUIInventoryScreen)
				gameGUI.input(0,0);
		}else if(showGame)
			gameGUI.input(0, 0);
		if(focusedTextBox != null){
			Keyboard.enableRepeatEvents(true);
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){
					char c = Keyboard.getEventCharacter();
					int k = Keyboard.getEventKey();
					if(k == Keyboard.KEY_ESCAPE || k == Keyboard.KEY_RETURN){
						this.focusedTextBox.setFocused(false);
						this.focusedTextBox = null;
					}else{
						this.focusedTextBox.textInput(k,c);
					}
				}
			}
			Keyboard.enableRepeatEvents(false);
		}
	}
	
	public void renderMouseItem(){
		if(this.currentGUI != null && this.currentGUI.shouldRenderMouseItem()){
			EntityPlayer player = GLCraft.getGLCraft().getEntityManager().getPlayer();
			player.mouseItem.renderIcon(Mouse.getX(), -Mouse.getY()+Constants.HEIGHT, 64);
		}
	}

	public boolean mouseShouldBeGrabbed(){
		if(GUIOpen){
			return currentGUI.mouseGrabbed;
		}else{
			return true;
		}
	}
	
	public boolean sendPlayerInput(){
		if(GUIOpen){
			return currentGUI.playerInput;
		}else{
			return true;
		}
	}

	public String getCurrentGUIName(){
		return currentGUIName;
	}

	public void setFocusedTextBox(GUITextBox textBox){
		this.focusedTextBox = textBox;
		textBox.setFocused(true);
	}
	
	public void unfocusTextBox(){
		if(this.focusedTextBox != null)
			this.focusedTextBox.setFocused(false);
		this.focusedTextBox = null;
	}
}
