package net.codepixl.GLCraft.render;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.Texture;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TextureManager {
	private static HashMap<String,String> textures = new HashMap<String,String>();
	private static HashMap<String,float[]> atlasCoords = new HashMap<String,float[]>();
	public static final String TILES = "textures/tiles/";
	public static final String ITEMS = "textures/items/";
	public static final String GUIS = "textures/gui/";
	public static final String MISC = "textures/misc/";
	private static String currentBoundTexture = "";
	private static boolean madeAtlas = false;
	private static BufferedImage noimg;
	public static String currentTexturepack = "none";
	public static boolean setAtlas;
	public static final int maxWidth = 16;
	
	public static void addTexture(String name, String path){
		textures.put(name.toLowerCase(),path.toLowerCase());
	}
	public static void addExternalTexture(String name, String path){
		textures.put(name.toLowerCase(), "[EXTERNAL]"+path);
	}
	public static void initTextures(){
		try {
			noimg = ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(MISC+"no_img.png"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		addTexture("misc.highlight",MISC+"highlight.png");
		for(int i = 0; i <= 7; i++){
			addTexture("misc.break_"+i,MISC+"break_"+i+".png");
		}
		addTexture("misc.break_8",MISC+"break_7.png");
		addTexture("misc.nothing",MISC+"nothing.png");
		addTexture("misc.floppy",MISC+"floppy.png");
	}
	public static void regenerateAtlas(){
		atlasCoords = new HashMap<String,float[]>();
		generateAtlas(true);
	}
	public static void generateAtlas(boolean regen){
		if(!madeAtlas || regen){
			System.out.println("Generating atlas with texture pack: "+currentTexturepack);
			madeAtlas = true;
			int total = 0;
			for (String list : textures.values()) {
			    total++;
			}
			Iterator<Map.Entry<String, String>> it = textures.entrySet().iterator();
			int height = /*(int) Math.ceil((float)total/(float)maxWidth);*/ maxWidth;
			BufferedImage combined = new BufferedImage(maxWidth*16, height*16, BufferedImage.TYPE_INT_ARGB);
			Graphics g = combined.getGraphics();
			int x = 0;
			int y = 0;
			while(it.hasNext()){
				Map.Entry<String, String> next = it.next();
				BufferedImage image;
				try {
					if(!regen) GLCraft.renderSplashText("Generating Texture Atlas...", "Loading "+next.getKey());
					if(next.getValue().startsWith("[EXTERNAL]")){
						image = ImageIO.read(new File(next.getValue().substring(next.getValue().indexOf(']')+1)));
					}else{
						if(currentTexturepack.equals("none")){
							image = ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(next.getValue()));
						}else{
							File tp = new File(Constants.GLCRAFTDIR + "Texturepacks/tmp/"+next.getValue());
							if(tp.exists()){
								image = ImageIO.read(tp.getAbsoluteFile());
							}else{
								image = ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(next.getValue()));
							}
						}
					}
					g.drawImage(image, x*16, y*16, null);
					atlasCoords.put(next.getKey(), new float[]{(float)x*(1f/(float)maxWidth),(float)y*(1f/(float)maxWidth)});
					//System.out.println("Added "+next.getKey()+" at "+x+","+y+" to texture atlas");
				} catch (Exception e) {
					System.err.println("Error adding "+next.getKey()+" to texture atlas: Could not find file "+next.getValue()+". Replacing with \"NO IMG\"");
					e.printStackTrace();
					g.drawImage(noimg, x*16, y*16, null);
					atlasCoords.put(next.getKey(), new float[]{(float)x*(1f/(float)maxWidth),(float)y*(1f/(float)maxWidth)});
				}
				x++;
				if(x >= maxWidth){
					x = 0;
					y++;
				}
			}
			try {
				File outputfile = new File(Constants.GLCRAFTDIR,"/temp/atlas.png");
				outputfile.getParentFile().mkdirs();
				outputfile.createNewFile();
				ImageIO.write(combined, "png", outputfile);
				
				if(!regen) Spritesheet.atlas = new Spritesheet(outputfile.getAbsolutePath(),maxWidth,true); else setAtlas = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static float[] tile(Tile t){
		if(t.hasMultipleTextures()){
			float[] coords = new float[12];
			for(int i = 0; i < 6; i++){
				int index = i*2;
				coords[index] = atlasCoords.get("tiles."+t.getMultiTextureNames()[i].toLowerCase())[0];
				coords[index+1] = atlasCoords.get("tiles."+t.getMultiTextureNames()[i].toLowerCase())[1];
			}
			return coords;
		}else{
			return atlasCoords.get("tiles."+t.getTextureName().toLowerCase());
		}
	}
	public static float[] tile(Tile t, byte meta) {
		if(t.hasMultipleTextures()){
			float[] coords = new float[12];
			for(int i = 0; i < 6; i++){
				int index = i*2;
				coords[index] = atlasCoords.get("tiles."+t.getMultiTextureNames(meta)[i].toLowerCase())[0];
				coords[index+1] = atlasCoords.get("tiles."+t.getMultiTextureNames(meta)[i].toLowerCase())[1];
			}
			return coords;
		}else{
			return atlasCoords.get("tiles."+t.getTextureName(meta).toLowerCase());
		}
	}
	public static float[] texture(String s){
		return atlasCoords.get(s.toLowerCase());
	}
	public static float[] itemStackIcon(ItemStack i) {
		if(!i.isNull()){
			if(i.isItem()) return item(i.getItem());
			if(i.isTile()) return tileIcon(i.getTile());
		}
		return new float[]{0,0};
	}
	public static float[] tileIcon(Tile t) {
		return atlasCoords.get("tiles."+t.getIconName().toLowerCase());
	}
	public static float[] tileIcon(Tile t, byte meta) {
		return atlasCoords.get("tiles."+t.getIconName(meta).toLowerCase());
	}
	public static float[] item(Item i) {
		return atlasCoords.get("items."+i.getTextureName().toLowerCase());
	}
	public static float[] itemStack(ItemStack i) {
		if(!i.isNull()){
			if(i.isItem()) return item(i.getItem());
			if(i.isTile()) return tile(i.getTile());
		}
		return new float[]{0,0};
	}
	public static boolean hasTexture(String name){
		return textures.containsKey(name.toLowerCase());
	}
}
