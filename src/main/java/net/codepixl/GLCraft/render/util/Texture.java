package net.codepixl.GLCraft.render.util;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
	int id,width,height;
	private static Texture lastBind, cbind;
	
	private Texture(int id, int width, int height){
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public static Texture loadTexture(String loc){
		BufferedImage image = null;
		try {
			image = ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(loc));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return commonInit(image);
	}

	public static Texture makeTexture(BufferedImage image){
		return commonInit(image);
	}
	
	public static Texture loadExternalTexture(String loc){
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(loc));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return commonInit(image);
	}
	
	public void bind(){
		glEnable(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glBindTexture(GL_TEXTURE_2D, id);
		lastBind = cbind;
		cbind = this;
	}
	
	public static void unbind(){
		glBindTexture(GL_TEXTURE_2D, 0);
		lastBind = cbind;
		cbind = null;
	}

	public static void rebind(){
		if(lastBind != null) lastBind.bind();
	}
	
	public void delete(){
		glDeleteTextures(id);
	}

	public static Texture loadTexture(BufferedImage image) {
		return commonInit(image);
	}

	private static Texture commonInit(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				pixels[x+y*image.getWidth()] = image.getRGB(x,y);
			}
		}
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4);
		for(int y=0; y < image.getHeight(); y++){
			for(int x=0; x < image.getWidth(); x++){
				int pixel = pixels[y*image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) ((pixel) & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		return new Texture(id, image.getWidth(), image.getHeight());
	}

	public int getId(){
		return id;
	}
}
