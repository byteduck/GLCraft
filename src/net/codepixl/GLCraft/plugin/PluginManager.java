package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.world.tile.PluginTile;
import net.codepixl.GLCraft.world.tile.Tile;

public class PluginManager {
	
	ArrayList<LoadedPlugin> plugins = new ArrayList<LoadedPlugin>();
	String path;
	byte currentTile = 0x64;
	
	public PluginManager(String pluginPath){
		path = pluginPath;
	}
	
	public void addTile(PluginTile t){
		t.assignedID = currentTile;
		Tile.tileMap.put(currentTile, t);
		System.out.println("Registered plugin tile "+t.getName()+" with ID "+currentTile);
		currentTile++;
	}
	
	public void update(){
		Iterator<LoadedPlugin> it = plugins.iterator();
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
					this.plugins.add(new LoadedPlugin(plugins[i].getAbsolutePath()));
				} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
