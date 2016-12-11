package net.codepixl.GLCraft.GUI.Elements;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Spritesheet;
import org.lwjgl.opengl.GL11;

public class GUITexture extends GUIElement{
	private String texture;
	private int size;
	public GUITexture(String texture, int x, int y, int size){
		this.texture = texture;
		this.x = x;
		this.y = y;
		setSize(size);
	}
	
	@Override
	public void render(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Spritesheet.atlas.bind();
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createSquare(0, 0, Color4f.WHITE, TextureManager.texture(texture), size);
		GL11.glEnd();
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
		this.size = size;
		this.width = size;
		this.height = size;
	}
}
