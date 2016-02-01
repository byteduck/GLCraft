package net.codepixl.GLCraft.GUI;

import java.util.HashMap;

public class GUIManager {

	private GUIScreen currentGUI;
	private boolean GUIOpen = false;
	private HashMap<String,GUIScreen> staticGUIs = new HashMap<String,GUIScreen>();
	private static GUIManager mainManager;
	private String currentGUIName = "nogui";

	public GUIManager(){
		
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
		currentGUIName = "noname";
	}
	
	public boolean showGUI(String guiName){
		if(staticGUIs.containsKey(guiName)){
			currentGUI = staticGUIs.get(guiName);
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

	public void closeGUI() {
		GUIOpen = false;
		currentGUI = null;
		currentGUIName = "nogui";
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

	public boolean mouseShouldBeGrabbed() {
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

	public String getCurrentGUIName() {
		return currentGUIName;
	}
}
