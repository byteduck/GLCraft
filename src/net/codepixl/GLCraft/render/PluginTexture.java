package net.codepixl.GLCraft.render;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.plugin.LoadedPlugin;
import net.codepixl.GLCraft.plugin.Plugin;

public class PluginTexture {
	public Plugin plugin;
	public String name;
	public String loc;
	public PluginTexture(String name, String loc, Plugin p){
		this.name = name;
		this.loc = loc;
		this.plugin = p;
	}
}
