package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.render.TextureManager;

public class PluginTile extends Tile{
	public Plugin plugin;
	public static byte assignedID = -1;
	@Override
	public byte getId(){
		return assignedID;
	}
	
	
	@Override
	public void registerTile(){
		//System.out.println("Attempted to register a plugin tile the wrong way!");
	}
	
	public PluginTile(Plugin p){
		this.plugin = p;
		if(this.getClass() != PluginTile.class){
			if(this.hasTexture()){
				if(this.hasMultipleTextures()){
					for(String name : this.getMultiTextureNames()){
						if(!TextureManager.hasTexture("tiles."+name)){
							TextureManager.addExternalTexture("tiles."+name, GLCraft.getGLCraft().getPluginManager().getResourceLocation(p)+"textures/tiles/"+name+".png");
						}
					}
				}else{
					TextureManager.addTexture("tiles."+this.getTextureName(), TextureManager.TILES+this.getTextureName()+".png");
				}
				if(!TextureManager.hasTexture(this.getIconName())){
					TextureManager.addTexture("tiles."+this.getIconName(), TextureManager.TILES+this.getIconName()+".png");
				}
			}
		}
	}
}
