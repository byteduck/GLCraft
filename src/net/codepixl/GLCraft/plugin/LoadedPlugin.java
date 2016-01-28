package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class LoadedPlugin {
	public String name;
	public String version;
	public String path;
	public String description;
	public String mainClass;
	ClassLoader loader;
	Plugin plugin;
	public LoadedPlugin(String path) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		this.path = path;
		byte[] data = Files.readAllBytes(new File(path+"/plugin.json").toPath());
		String jsonString = new String(data,StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(jsonString);
		this.name = json.getString("pluginName");
		this.version = json.getString("pluginVersion");
		this.description = json.getString("pluginDescription");
		this.mainClass = json.getString("mainClass");
		loader = new URLClassLoader(new URL[]{new File(path).toURL()});
		plugin = (Plugin) loader.loadClass(mainClass).newInstance();
		plugin.init();
		System.out.println("Loaded \""+name+"\" version \""+version+"\" with description \""+description+"\"");
	}
	
	public void update(){
		plugin.update();
	}
}
