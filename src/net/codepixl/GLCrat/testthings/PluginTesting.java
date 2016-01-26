package net.codepixl.GLCrat.testthings;

import net.codepixl.GLCraft.plugin.PluginManager;

public class PluginTesting {
	public static void main(String[] args){
		PluginManager p = new PluginManager("/Users/Codepixl/Desktop/GLCraft Plugins");
		p.loadPlugins();
	}
}
