package net.codepixl.GLCraft.GUI;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.codepixl.GLCraft.GUI.Elements.GUIElement;

public class GUIScreen extends GUIElement{
	private ArrayList<GUIElement> elements = new ArrayList<GUIElement>();
	public boolean mouseGrabbed = false;
	public boolean playerInput = false;
	
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		drawBG();
		Iterator<GUIElement> it = elements.iterator();
		while(it.hasNext()){
			it.next().render();
		}
		GL11.glPopMatrix();
	}
	public void initGL(){
		
	}
	public void update(){
		Iterator<GUIElement> it = elements.iterator();
		while(it.hasNext()){
			it.next().update();
		}
	}
	public void input(){
		if(!enabled)
			return;
		Iterator<GUIElement> it = elements.iterator();
		while(it.hasNext()){
			it.next().input();
		}
	}
	
	public GUIScreen(){
		
	}
	
	public void addElement(GUIElement element){
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
