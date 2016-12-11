package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.render.TextureManager;

public class PluginTile extends Tile{
	public Plugin plugin;
	
	@Override
	public void registerTile(){
		//Logger.log("Attempted to register a plugin tile the wrong way!");
	}
	
	public PluginTile(Plugin p){
		this.plugin = p;
		if(this.getClass() != PluginTile.class){
			if(this.hasTexture()){
				if(this.hasMultipleTextures()){
					for(String name : this.getMultiTextureNames()){
						if(!TextureManager.hasTexture("tiles."+name)){
							TextureManager.addPluginTexture("tiles."+name, "textures/tiles/" +name+".png",plugin);
						}
					}
				}else{
					if(!TextureManager.hasTexture("tiles."+this.getTextureName())){
						TextureManager.addPluginTexture("tiles."+this.getTextureName(), "textures/tiles/" +this.getTextureName()+".png",plugin);
					}
				}
				if(!TextureManager.hasTexture("tiles."+this.getIconName())){
					TextureManager.addPluginTexture("tiles."+this.getIconName(), "textures/tiles/" +this.getIconName()+".png",plugin);
				}
			}
		}
	}

	public void setId(byte id){
		this.id = id;
	}
}
