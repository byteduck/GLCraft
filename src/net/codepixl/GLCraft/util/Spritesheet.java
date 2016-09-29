package net.codepixl.GLCraft.util;

import java.awt.image.BufferedImage;

public class Spritesheet {
	private Texture texture;
	private String path;
	private float size;
	private BufferedImage image;
	public int id;
	
	public static Spritesheet atlas;
	public static Spritesheet clouds = new Spritesheet("textures/clouds.png",1);
	public static Spritesheet stars = new Spritesheet("textures/misc/stars.png",1);
	
	public Spritesheet(String path, float size){
		this.path = path;
		this.size = 1 / size;
		load();
	}
	
	public Spritesheet(BufferedImage image, float size) {
		this.image = image;
		this.size = size;
		loadBuffered();
	}
	
	public Spritesheet(String path, float size, boolean external){
		this.path = path;
		this.size = 1 / size;
		if(external) loadExt(); else load();
	}

	private void loadExt() {
		texture = Texture.loadExternalTexture(path);
		id = texture.id;
	}

	private void load(){
		texture = Texture.loadTexture(path);
		id = texture.id;
	}
	
	private void loadBuffered(){
		texture = Texture.loadTexture(image);
		id = texture.id;
	}
	
	public void bind(){
		texture.bind();
	}
	
	public void unbind(){
		texture.unbind();
	}
	
	public void delete(){
		texture.delete();
	}
	
	public float uniformSize(){
		return size;
	}
}
