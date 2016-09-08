package net.codepixl.GLCraft.GUI;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.codepixl.GLCraft.util.Constants;

public class GUIScreen{
	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public boolean enabled = true;
	private ArrayList<GUIScreen> elements = new ArrayList<GUIScreen>();
	public boolean mouseGrabbed = false;
	public boolean playerInput = false;
	
	public void renderMain(){
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		drawBG();
		this.render();
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().renderMain();
		}
		GL11.glPopMatrix();
	}
	
	/**
	 * IMPORTANT: Once this method is called, glTranslate has already been called with this GUIScreen's x and y position.
	 */
	public void render(){
		
	}
	
	public void initGL(){
		
	}
	
	public boolean testMouse(int xof, int yof){
		int mouseY = Mouse.getY()+yof;
		int mouseX = Mouse.getX()-xof;
		mouseY = -mouseY+Constants.HEIGHT;
		if(mouseY <= y+height && mouseY >= y){
			if(mouseX <= x+width && mouseX >= x){
				return true;
			}
		}
		return false;
	}
	
	public boolean testClick(int xof, int yof, int button){
		if(testMouse(xof,yof)){
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(button)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void update(){
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().update();
		}
	}
	public void input(int xOffset, int yOffset){
		if(!enabled)
			return;
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().input(xOffset+x, yOffset+y);
		}
	}
	
	public GUIScreen(){
		
	}
	
	public void addElement(GUIScreen element){
		this.elements.add(element);
	}
	
	public void drawBG() {
		
	}
	
	public void onClose() {
		
	}
	
	public boolean canBeExited() {
		return true;
	}
	
}
