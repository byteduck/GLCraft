package net.codepixl.GLCraft.world.item;

import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.render.TextureManager;

public class PluginItem extends Item{
	public Plugin plugin;

	@Override
	public void registerItem(){
		//Logger.log("Attempted to register a plugin item the wrong way!");
	}
	
	public PluginItem(Plugin p){
		this.plugin = p;
		if(this.getClass() != PluginItem.class){
			TextureManager.addPluginTexture("items."+getTextureName(), "textures/items/" +getTextureName()+".png", p);
		}
	}

	public void setId(byte id){
		this.id = id;
	}
}
