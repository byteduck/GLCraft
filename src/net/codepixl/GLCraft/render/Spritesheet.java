package net.codepixl.GLCraft.render;

import java.awt.image.BufferedImage;

public class Spritesheet {
	private Texture texture;
	private String path;
	private float size;
	private BufferedImage image;
	
	public static Spritesheet atlas;
	public static Spritesheet clouds = new Spritesheet("textures/clouds.png",1);
	
	public Spritesheet(String path, float size){
		this.path = path;
		this.size = 1 / size;
		load();
	}
	
	public Spritesheet(BufferedImage image, float size){
		this.path = null;
		this.size = 1 / size;
		this.image = image;
		loadBuffered();
	}
	
	private void load(){
		texture = Texture.loadTexture(path);
	}
	
	private void loadBuffered(){
		texture = Texture.loadTexture(image);
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
