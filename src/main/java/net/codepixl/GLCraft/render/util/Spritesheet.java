package net.codepixl.GLCraft.render.util;

import java.awt.image.BufferedImage;

public class Spritesheet{
	private Texture texture;
	private String path;
	private BufferedImage image;
	public int id;
	
	public static Spritesheet atlas;
	public static Spritesheet rain = new Spritesheet("textures/environment/rain.png");
	public static Spritesheet logo = new Spritesheet("textures/icons/logos.png");
	
	public Spritesheet(String path){
		this.path = path;
		load();
	}
	
	public Spritesheet(BufferedImage image) {
		this.image = image;
		loadBuffered();
	}
	
	public Spritesheet(String path, boolean external){
		this.path = path;
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
}
