package net.codepixl.GLCraft.GUI;

import java.util.HashMap;

public class GUIManager {

	private GUIScreen currentGUI;
	private boolean GUIOpen = false;
	private HashMap<String,GUIScreen> staticGUIs = new HashMap<String,GUIScreen>();
	private static GUIManager mainManager;

	public GUIManager() {
	}
	
	public static void setMainManager(GUIManager manager){
		mainManager = manager;
	}
	
	public static GUIManager getMainManager(){
		return mainManager;
	}

	public void showGUI(GUIScreen gui) {
		currentGUI = gui;
		GUIOpen = true;
	}
	
	public boolean showGUI(String guiName){
		if(staticGUIs.containsKey(guiName)){
			currentGUI = staticGUIs.get(guiName);
			GUIOpen = true;
			return true;
		}
		return false;
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

	public void closeGUI() {
		GUIOpen = false;
		currentGUI = null;
	}

	public void render() {
		if (GUIOpen)
			currentGUI.render();
	}

	public void update() {
		if (GUIOpen)
			currentGUI.update();
	}

	public void input() {
		if (GUIOpen)
			currentGUI.input();
	}
}
