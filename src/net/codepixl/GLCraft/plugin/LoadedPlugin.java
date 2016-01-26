package net.codepixl.GLCraft.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class LoadedPlugin {
	public String name;
	public String version;
	public String path;
	public String description;
	public LoadedPlugin(String path) throws IOException{
		this.path = path;
		byte[] data = Files.readAllBytes(new File(path+"/plugin.json").toPath());
		String jsonString = new String(data,StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(jsonString);
		this.name = json.getString("pluginName");
		this.version = json.getString("pluginVersion");
		this.description = json.getString("pluginDescription");
		System.out.println("Loaded \""+name+"\" version \""+version+"\" with description \""+description+"\"");
	}
}
