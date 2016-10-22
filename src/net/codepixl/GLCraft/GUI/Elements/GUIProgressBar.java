package net.codepixl.GLCraft.GUI.Elements;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.render.Shape;

public class GUIProgressBar extends GUIElement{
	
	public Color4f COLOR = new Color4f(0.3f, 0.5f, 0.3f, 1f), BGCOLOR = new Color4f(0.1f, 0.2f, 0.1f, 1f);
	
	public static int PB_HEIGHT = 10;
	
	private int progress, length;
	
	private boolean vertical = false;
	
	private Color4f color = COLOR, bgcolor = BGCOLOR;
	
	public GUIProgressBar(int x, int y, int length){
		this.x = x;
		this.y = y;
		this.length = length;
		this.progress = 0;
		this.width = length;
		this.height = PB_HEIGHT;
	}
	
	public GUIProgressBar(int x, int y, int length, boolean vertical, Color4f color, Color4f bgcolor){
		this.x = x;
		this.y = y;
		this.length = length;
		this.progress = 0;
		this.width = length;
		this.height = PB_HEIGHT;
		this.vertical = vertical;
		this.color = color;
		this.bgcolor = bgcolor;
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		if(this.vertical)
			GL11.glRotatef(-90f, 0, 0, 1f);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		Shape.createTexturelessRect2D(0, 0, length, height, bgcolor);
		glEnd();
		glBegin(GL_QUADS);
		Shape.createTexturelessRect2D(0, 0, length*((float)progress/100f), height, color);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	public void setProgress(int progress){
		this.progress = progress;
	}
}
