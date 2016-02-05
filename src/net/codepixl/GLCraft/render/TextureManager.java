package net.codepixl.GLCraft.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TextureManager {
	private static HashMap<String,String> textures = new HashMap<String,String>();
	private static int texCount = 0;
	public static final String TILES = "textures/tiles/";
	public static final String ITEMS = "textures/items/";
	public static final String GUIS = "textures/gui/";
	public static final String MISC = "textures/misc/";
	private static String currentBoundTexture = "";
	private static boolean madeAtlas = false;
	public static void addTexture(String name, String path){
		textures.put(name,path);
		texCount++;
	}
	public static void initTextures(){
		addTexture("misc.highlight",TILES+"glass.png");
		for(int i = 0; i <= 7; i++){
			addTexture("misc.break_"+i,MISC+"break_"+i+".png");
		}
		addTexture("misc.break_8",MISC+"break_7.png");
	}
	public static void generateAtlas(){
		if(!madeAtlas){
			madeAtlas = true;
			final int maxWidth = 10;
			int total = 0;
			for (String list : textures.values()) {
			    total++;
			}
			Iterator<Map.Entry<String, String>> it = textures.entrySet().iterator();
			int height = (int) Math.ceil((float)total/(float)maxWidth);
			while(it.hasNext()){
				Map.Entry<String, String> next = it.next();
				
			}
		}
	}
}
