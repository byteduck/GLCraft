package net.codepixl.GLCraft.render;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.plugin.LoadedPlugin;
import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.render.util.Texture;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TextureManager {
	private static HashMap<String,String> textures = new HashMap<String,String>();
	private static HashMap<String,float[]> atlasCoords = new HashMap<String,float[]>();
	private static ArrayList<PluginTexture> pluginTextures = new ArrayList<PluginTexture>();
	public static final String TILES = "textures/tiles/";
	public static final String ITEMS = "textures/items/";
	public static final String GUIS = "textures/gui/";
	public static final String MISC = "textures/misc/";
	protected static String currentBoundTexture = "";
	private static boolean madeAtlas = false;
	private static BufferedImage noimg;
	public static String currentTexturepack = "[[none]]";
	public static boolean setAtlas;
	public static int maxWidth = 256;
	
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
		addTexture("misc.sun",MISC+"sun.png");
		addTexture("misc.moon",MISC+"moon.png");
		addTexture("misc.no_img",MISC+"no_img.png");
	}
	public static void regenerateAtlas(){
		atlasCoords = new HashMap<String,float[]>();
		generateAtlas(true);
	}
	/*public static void generateAtlas(boolean regen){
		if(!madeAtlas || regen){
			GLogger.log("Generating atlas with texture pack: "+currentTexturepack, LogSource.GLCRAFT);
			madeAtlas = true;
			int total = 0;
			for (String list : textures.values()) {
			    total++;
			}
			Iterator<Map.Entry<String, String>> it = textures.entrySet().iterator();
			int height = /*(int) Math.ceil((float)total/(float)maxWidth); maxWidth;
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
						if(currentTexturepack.equals("[[none]]")){
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
					//Logger.log("Added "+next.getKey()+" at "+x+","+y+" to texture atlas");
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
			Iterator<PluginTexture> it2 = pluginTextures.iterator();
			while(it2.hasNext()){
				PluginTexture next = it2.next();
				BufferedImage image;
				LoadedPlugin p = GLCraft.getGLCraft().getPluginManager().getLoadedPlugin(next.plugin);
				try {
					if(!GLCraft.getGLCraft().isDevEnvironment){
						image = ImageIO.read(p.loader.getResourceAsStream(next.loc));
					}else{
						image = ImageIO.read(new File("res",next.loc));
					}
					g.drawImage(image, x*16, y*16, null);
					atlasCoords.put(next.name, new float[]{(float)x*(1f/(float)maxWidth),(float)y*(1f/(float)maxWidth)});
				} catch (IOException | IllegalArgumentException e) {
					e.printStackTrace();
					System.err.println("Error adding "+next.name+" to texture atlas: Could not find file "+next.loc+" in plugin "+p.name+". Replacing with \"NO IMG\"");
					e.printStackTrace();
					g.drawImage(noimg, x*16, y*16, null);
					atlasCoords.put(next.name, new float[]{(float)x*(1f/(float)maxWidth),(float)y*(1f/(float)maxWidth)});
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
				if(regen){
					Spritesheet.atlas.delete();
					setAtlas = true;
				}
				Spritesheet.atlas = new Spritesheet(outputfile.getAbsolutePath(),maxWidth,true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Shape.currentSpritesheet = Spritesheet.atlas;

	}*/

	private static int imageIndex = 0;
	private static ArrayList<Boolean[]> lines;
	
	private static class AtlasTexture{
		private final BufferedImage image;
		private final String name;
		private float[] location;

		public AtlasTexture(BufferedImage image, String name){
			this.image = image;
			this.name = name;
		}
	}

	public static void generateAtlas(boolean regen){
		if(!madeAtlas || regen){
			GLogger.log("Generating atlas with texture pack: "+currentTexturepack, LogSource.GLCRAFT);
			madeAtlas = true;

			//init images array
			AtlasTexture[] images = new AtlasTexture[textures.size()+pluginTextures.size()];

			imageIndex = 0;
			maxWidth = 128;
			lines = new ArrayList<Boolean[]>();

			//load images into images[] and sort by width
			loadTextures(images, regen);
			loadPluginTextures(images, regen);
			Arrays.sort(images, (a, b) -> b.image.getWidth() - a.image.getWidth());

			//pack
			for(int j = 0; j < images.length; j++) {
				BufferedImage i = images[j].image;
				int line = 0, xPos = 0;
				while (!roomForImage(i, line, xPos)) {
					xPos++;
					if (xPos + i.getWidth() > maxWidth) {
						xPos = 0;
						line++;
					}
				}
				for (int y = line; y < line + i.getHeight(); y++) {
					Boolean[] l = lines.get(y);
					for (int x = xPos; x < xPos + i.getWidth(); x++)
						l[x] = true;
				}
				images[j].location = new float[]{xPos, line, 0, 0};
			}

			BufferedImage out = createImage(BufferedImage.TYPE_INT_ARGB, images);
			try{
				File outputfile = new File(Constants.GLCRAFTDIR,"/temp/atlas.png");
				outputfile.getParentFile().mkdirs();
				outputfile.createNewFile();
				ImageIO.write(out, "png", outputfile);
				if(regen){
					Spritesheet.atlas.delete();
					setAtlas = true;
				}
				Spritesheet.atlas = new Spritesheet(outputfile.getAbsolutePath(),true);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	private static BufferedImage createImage(int type, AtlasTexture[] images){
		BufferedImage img = new BufferedImage(maxWidth, lines.size(), type);
		Graphics2D g = img.createGraphics();
		for(int i = 0; i < images.length; i++) {
			AtlasTexture tex = images[i];
			g.drawImage(tex.image, (int) tex.location[0], (int) tex.location[1], null);
			tex.location[0] /= (float)maxWidth;
			tex.location[1] /= (float)img.getHeight();
			tex.location[2] = tex.location[0]+((float)tex.image.getWidth()/(float)maxWidth);
			tex.location[3] = tex.location[1]+((float)tex.image.getHeight()/(float)img.getHeight());
			atlasCoords.put(tex.name, tex.location);
		}
		return img;
	}

	private static boolean roomForImage(BufferedImage i, int line, int xPos){
		if(xPos+i.getWidth() > maxWidth) return false;
		for(int y = line; y < line + i.getHeight(); y++) {
			Boolean[] l = getLine(y);
			for (int x = xPos; x < xPos + i.getWidth(); x++)
				if(l[x]) return false;
		}
		return true;
	}

	private static Boolean[] getLine(int line){
		while(lines.size() < line+1){
			Boolean[] lineB = new Boolean[maxWidth];
			for(int i = 0; i < maxWidth; i++)
				lineB[i] = false;
			lines.add(lineB);
		}
		return lines.get(line);
	}

	private static void loadTextures(AtlasTexture[] images, boolean regen){
		Iterator<Map.Entry<String, String>> it = textures.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> next = it.next();
			BufferedImage image;
			try {
				if(!regen) GLCraft.renderSplashText("Generating Texture Atlas...", "Loading "+next.getKey());
				if(next.getValue().startsWith("[EXTERNAL]")){
					image = ImageIO.read(new File(next.getValue().substring(next.getValue().indexOf(']')+1)));
				}else{
					if(currentTexturepack.equals("[[none]]")){
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
				images[imageIndex++] = new AtlasTexture(image, next.getKey());
				if(image.getWidth() > maxWidth) maxWidth = image.getWidth();
				//Logger.log("Added "+next.getKey()+" at "+x+","+y+" to texture atlas");
			} catch (Exception e) {
				System.err.println("Error adding "+next.getKey()+" to texture atlas: Could not find file "+next.getValue()+". Replacing with \"NO IMG\"");
				e.printStackTrace();
				images[imageIndex++] = new AtlasTexture(noimg, next.getKey());;
			}
		}
	}

	private static void loadPluginTextures(AtlasTexture[] images, boolean regen){
		Iterator<PluginTexture> it2 = pluginTextures.iterator();
		while(it2.hasNext()){
			PluginTexture next = it2.next();
			BufferedImage image;
			LoadedPlugin p = GLCraft.getGLCraft().getPluginManager().getLoadedPlugin(next.plugin);
			try {
				if(!GLCraft.isDevEnvironment){
					image = ImageIO.read(p.loader.getResourceAsStream(next.loc));
				}else{
					image = ImageIO.read(new File("res",next.loc));
				}
				images[imageIndex++] = new AtlasTexture(image, next.name);
				if(image.getWidth() > maxWidth) maxWidth = image.getWidth();
			} catch (IOException | IllegalArgumentException e) {
				e.printStackTrace();
				System.err.println("Error adding "+next.name+" to texture atlas: Could not find file "+next.loc+" in plugin "+p.name+". Replacing with \"NO IMG\"");
				e.printStackTrace();
				images[imageIndex++] = new AtlasTexture(noimg, next.name);
			}
		}
	}

	public static float[] tile(Tile t){
		if(t.hasMultipleTextures()){
			float[] coords = new float[24];
			for(int i = 0; i < 6; i++){
				int index = i*4;
				coords[index] = atlasCoords.get("tiles."+t.getMultiTextureNames()[i].toLowerCase())[0];
				coords[index+1] = atlasCoords.get("tiles."+t.getMultiTextureNames()[i].toLowerCase())[1];
				coords[index+2] = atlasCoords.get("tiles."+t.getMultiTextureNames()[i].toLowerCase())[2];
				coords[index+3] = atlasCoords.get("tiles."+t.getMultiTextureNames()[i].toLowerCase())[3];
			}
			return coords;
		}else{
			return atlasCoords.get("tiles."+t.getTextureName().toLowerCase());
		}
	}
	public static float[] tile(Tile t, byte meta) {
		if(t.hasMultipleTextures()){
			float[] coords = new float[24];
			for(int i = 0; i < 6; i++){
				int index = i*4;
				coords[index] = atlasCoords.get("tiles."+t.getMultiTextureNames(meta)[i].toLowerCase())[0];
				coords[index+1] = atlasCoords.get("tiles."+t.getMultiTextureNames(meta)[i].toLowerCase())[1];
				coords[index+2] = atlasCoords.get("tiles."+t.getMultiTextureNames(meta)[i].toLowerCase())[2];
				coords[index+3] = atlasCoords.get("tiles."+t.getMultiTextureNames(meta)[i].toLowerCase())[3];
			}
			return coords;
		}else{
			return atlasCoords.get("tiles."+t.getTextureName(meta).toLowerCase());
		}
	}
	public static float[] texture(String s){
		if(atlasCoords.containsKey(s))
			return atlasCoords.get(s.toLowerCase());
		else
			return atlasCoords.get("misc.no_img");
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
	public static void addPluginTexture(String name, String loc, Plugin plugin) {
		pluginTextures.add(new PluginTexture(name,loc,plugin));
	}
}
