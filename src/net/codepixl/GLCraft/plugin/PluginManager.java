package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PluginManager {
	
	ArrayList<LoadedPlugin> plugins = new ArrayList<LoadedPlugin>();
	String path;
	
	public PluginManager(String pluginPath){
		path = pluginPath;
	}
	
	public void loadPlugins(){
		File dir = new File(path);
		File[] plugins = dir.listFiles();
		for(int i = 0; i < plugins.length; i++){
			if(plugins[i].isDirectory()){
				try {
					this.plugins.add(new LoadedPlugin(plugins[i].getAbsolutePath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
