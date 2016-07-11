package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.world.tile.PluginTile;
import net.codepixl.GLCraft.world.tile.Tile;

public class PluginManager {
	
	ArrayList<LoadedPlugin> loadedPlugins = new ArrayList<LoadedPlugin>();
	HashMap<Plugin,LoadedPlugin> plugins = new HashMap<Plugin,LoadedPlugin>();
	public static String path;
	byte currentTile = 0x64;
	public LoadedPlugin currentlyLoadingPlugin = null;
	
	public PluginManager(String pluginPath){
		path = pluginPath;
	}
	
	public void addDevPlugin(Plugin p){
		GLCraft.renderSplashText("Loading Plugins...", "Dev Plugin");
		LoadedPlugin temp = new LoadedPlugin(p);
		loadedPlugins.add(temp);
		plugins.put(p, temp);
	}
	
	public void addTile(PluginTile t){
		t.assignedID = currentTile;
		Tile.tileMap.put(currentTile, t);
		System.out.println("Registered plugin tile "+t.getName()+" with ID "+currentTile);
		currentTile++;
	}
	
	public void update(){
		Iterator<LoadedPlugin> it = loadedPlugins.iterator();
		while(it.hasNext()){
			it.next().update();
		}
	}
	
	public void loadPlugins(){
		File dir = new File(path);
		File[] plugins = dir.listFiles();
		for(int i = 0; i < plugins.length; i++){
			if(plugins[i].isDirectory()){
				try {
					GLCraft.renderSplashText("Loading Plugins...", plugins[i].getName());
					this.loadedPlugins.add(new LoadedPlugin(plugins[i].getAbsolutePath()));
				} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		currentlyLoadingPlugin = null;
	}

	public String getResourceLocation(Plugin p) {
		if(plugins.get(p).mainClass.equals("dev")){
			return plugins.get(p).path+"/res/";
		}else{
			System.out.println(plugins.get(p).path);
			return plugins.get(p).path;
		}
	}
	
	public String getResourceLocation(){
		if(currentlyLoadingPlugin.mainClass.equals("dev")){
			return "res/";
		}else{
			System.out.println(currentlyLoadingPlugin.path);
			return currentlyLoadingPlugin.path;
		}
	}
}
