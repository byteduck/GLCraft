package net.codepixl.GLCraft.GUI;

import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.GUI.Elements.GUIElement;

public class GUIScreen {
	private ArrayList<GUIElement> elements = new ArrayList<GUIElement>();
	public void render(){
		Iterator<GUIElement> it = elements.iterator();
		while(it.hasNext()){
			it.next().render();
		}
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
	
}
