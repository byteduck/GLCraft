package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.PluginItem;
import net.codepixl.GLCraft.world.tile.PluginTile;
import net.codepixl.GLCraft.world.tile.Tile;

public class PluginManager {
	
	ArrayList<LoadedPlugin> loadedPlugins = new ArrayList<LoadedPlugin>();
	HashMap<Plugin,LoadedPlugin> plugins = new HashMap<Plugin,LoadedPlugin>();
	public final HashMap<String,LoadedPlugin> pluginIDs = new HashMap<String,LoadedPlugin>();
	public static String path;
	byte currentTile = 0x64;
	byte currentItem = 0x64;
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
		GLogger.log("Registered plugin tile "+t.getName()+" with ID "+currentTile, LogSource.GLCRAFT);
		currentTile++;
	}
	
	public void addItem(PluginItem i){
		i.assignedID = currentItem;
		Item.itemMap.put(currentItem, i);
		GLogger.log("Registered item "+i.getName()+" with ID "+currentItem, LogSource.GLCRAFT);
		currentItem++;
	}
	
	public void update(){
		Iterator<LoadedPlugin> it = loadedPlugins.iterator();
		while(it.hasNext()){
			it.next().update();
		}
	}
	
	public void loadPlugins(){
		File dir = new File(path);
		File[] plugins = dir.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
			
		});
		for(int i = 0; i < plugins.length; i++){
			try {
				GLCraft.renderSplashText("Loading Plugins...", plugins[i].getName().replaceAll(".jar", ""));
				LoadedPlugin p = new LoadedPlugin(plugins[i].getAbsolutePath());
				this.loadedPlugins.add(p);
				this.plugins.put(p.plugin, p);
				this.pluginIDs.put(p.mainClass+":"+p.version, p);
			} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentlyLoadingPlugin = null;
	}
	
	public LoadedPlugin getLoadedPlugin(Plugin p){
		return this.plugins.get(p);
	}
	
	public HashMap<Plugin, LoadedPlugin> getPlugins(){
		return plugins;
	}
}
