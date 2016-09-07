package net.codepixl.GLCraft.GUI;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

public class GUIScreen{
	public int x = 0;
	public int y = 0;
	public boolean enabled = true;
	private ArrayList<GUIScreen> elements = new ArrayList<GUIScreen>();
	public boolean mouseGrabbed = false;
	public boolean playerInput = false;
	
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		drawBG();
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().render();
		}
		GL11.glPopMatrix();
	}
	public void initGL(){
		
	}
	public void update(){
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().update();
		}
	}
	public void input(){
		if(!enabled)
			return;
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().input();
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
