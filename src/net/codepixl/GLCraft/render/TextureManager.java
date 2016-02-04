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
	private static String currentBoundTexture = "";
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
		currentBoundTexture = "";
	}
	public static void bindTile(Tile t){
		if(!currentBoundTexture.equals("tiles."+t.getTextureName())){
			getTexture("tiles."+t.getTextureName()).bind();
			currentBoundTexture = "tiles."+t.getTextureName();
		}
	}
	public static void bindItem(Item i){
		if(!currentBoundTexture.equals("items."+i.getTextureName())){
			getTexture("tiles."+i.getTextureName()).bind();
			currentBoundTexture = "items."+i.getTextureName();
		}
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
	public static void bindTexture(String textureName){
		if(!currentBoundTexture.equals(textureName)){
			getTexture(textureName).bind();
			currentBoundTexture = textureName;
		}
	}
	public static void bindTileIcon(Tile t){
		if(!currentBoundTexture.equals("tiles."+t.getIconName())){
			getTexture("tiles."+t.getIconName()).bind();
			currentBoundTexture = "tiles."+t.getIconName();
		}
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
