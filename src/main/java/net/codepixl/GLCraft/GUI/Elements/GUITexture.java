package net.codepixl.GLCraft.GUI.Elements;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.render.util.Texture;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.logging.GLogger;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import java.awt.image.BufferedImage;

public class GUITexture extends GUIElement{
	private String texture;
	private Texture textureImage;
	private int size;
	public GUITexture(String texture, int x, int y, int size){
		this.texture = texture;
		this.x = x;
		this.y = y;
		setSize(size);
	}

	public GUITexture(BufferedImage texture, int x, int y, int size){
		this.textureImage = Texture.makeTexture(texture);
		this.x = x;
		this.y = y;
		setSize(size);
	}
	
	@Override
	public void render(){
		if(texture != null)
			Spritesheet.atlas.bind();
		else {
			textureImage.bind();
			textureImage.bind(); //idk why but this second statement makes it work
		}
		GL11.glBegin(GL11.GL_QUADS);
		if(texture != null)
			Shape.createSquare(0, 0, Color4f.WHITE, TextureManager.texture(texture), size);
		else
			Shape.createSquare(0, 0, Color4f.WHITE, size);
		GL11.glEnd();
		TextureImpl.unbind();
	}
	
	public String getTexture() {
		return texture;
	}
	
	public void setTexture(String texture) {
		this.texture = texture;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size * Constants.getGUIScale();
		this.width = size * Constants.getGUIScale();
		this.height = size * Constants.getGUIScale();
	}

	public void deleteTexture(){
		if(textureImage != null) textureImage.delete();
	}
}
