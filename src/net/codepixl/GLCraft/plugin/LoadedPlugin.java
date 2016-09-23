package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.world.crafting.Recipe.InvalidRecipeException;

public class LoadedPlugin {
	public String name;
	public String version;
	public String path;
	public String description;
	public String mainClass;
	public String glVersion;
	ClassLoader loader;
	Plugin plugin;
	public LoadedPlugin(String path) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		try{
			GLCraft.getGLCraft().getPluginManager().currentlyLoadingPlugin = this;
			this.path = path;
			byte[] data = Files.readAllBytes(new File(path+"/plugin.json").toPath());
			String jsonString = new String(data,StandardCharsets.UTF_8);
			JSONObject json = new JSONObject(jsonString);
			this.name = json.getString("pluginName");
			this.version = json.getString("pluginVersion");
			this.description = json.getString("pluginDescription");
			this.mainClass = json.getString("mainClass");
			this.glVersion = json.getString("GLCraftVersion");
			if(this.glVersion != GLCraft.version){
				JOptionPane.showMessageDialog(null, "The Plugin "+this.name+" is for a newer/older version of GLCraft. It will be deleted.",  "Error", JOptionPane.ERROR_MESSAGE);
				deleteDirectory(new File(path));
			}else{
				loader = new URLClassLoader(new URL[]{new File(path).toURL()});
				plugin = (Plugin) loader.loadClass(mainClass).newInstance();
				plugin.init();
			}
			System.out.println("Loaded \""+name+"\" version \""+version+"\" with description \""+description+"\"");
		}catch(JSONException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The Plugin "+this.name+" could not be loaded because it is missing 1 or more JSON properties.", "Error", JOptionPane.ERROR_MESSAGE);
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The Plugin "+this.name+" could not be loaded because of aliens/an unknown error.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public LoadedPlugin(Plugin p){
		GLCraft.getGLCraft().getPluginManager().currentlyLoadingPlugin = this;
		this.plugin = p;
		this.name = "dev plugin";
		this.version = "dev";
		this.description = "A plugin loaded from the GLCraft dev environment.";
		this.mainClass = "dev";
		try {
			p.init();
		} catch (InvalidRecipeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("---Loaded dev plugin---");
	}
	
	public void update(){
		try{
			if(plugin != null)
				plugin.update();
		}catch(AbstractMethodError e){
			
		}
	}
	
	private static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
}
