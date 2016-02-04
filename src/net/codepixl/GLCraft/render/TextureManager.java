package net.codepixl.GLCraft.render;

import java.util.HashMap;

import net.codepixl.GLCraft.item.Item;
import net.codepixl.GLCraft.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TextureManager {
	private static HashMap<String,Texture> textures = new HashMap<String,Texture>();
	public static final String TILES = "textures/tiles/";
	public static final String ITEMS = "textures/items/";
	public static final String GUIS = "textures/gui/";
	public static final String MISC = "textures/misc/";
	public static boolean addTexture(String name, String path){
		if(!textures.containsKey(name)){
			textures.put(name, Texture.loadTexture(path));
			return true;
		}
		return false;
	}
	public static void initTextures(){
		addTexture("misc.highlight",TILES+"glass.png");
		for(int i = 0; i <= 7; i++){
			addTexture("misc.break_"+i,MISC+"break_"+i+".png");
		}
		addTexture("misc.break_8",MISC+"break_7.png");
	}
	public static Texture getTexture(String name) {
		return textures.get(name);
	}
	public static void unbind() {
		Texture.unbind();
	}
	public static void bindTile(Tile t){
		getTexture("tiles."+t.getTextureName()).bind();
	}
	public static void bindItem(Item i){
		getTexture("tiles."+i.getTextureName()).bind();
	}
	public static void bindItemStack(ItemStack s){
		if(!s.isNull()){
			if(s.isTile()){
				bindTile(s.getTile());
			}else{
				bindItem(s.getItem());
			}
		}
	}
	public static void bindTexture(String string){
		getTexture(string).bind();
	}
	public static void bindTileIcon(Tile t){
		getTexture("tiles."+t.getIconName()).bind();
	}
	public static void bindItemStackIcon(ItemStack s) {
		if(!s.isNull()){
			if(s.isTile()){
				bindTileIcon(s.getTile());
			}else{
				bindItem(s.getItem());
			}
		}
	}
}
