package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.plugin.LoadedPlugin;

import java.util.Collection;

public class PacketPlayerLogin extends Packet{
	
	public byte protocolVersion = 0x0;
	public String name;
	public String[] plugins;
	
	public PacketPlayerLogin(String name){
		this.name = name;
		Collection<LoadedPlugin> ps = GLCraft.getGLCraft().getPluginManager().getPlugins().values();
		plugins = new String[ps.size()];
		int i = 0;
		for(LoadedPlugin p : ps){
			plugins[i] = p.mainClass+":"+p.version;
			i++;
		}
	}
	
}
