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

public class GUIProgressBar extends GUIScreen{
	
	public static int PB_HEIGHT = 10;
	
	private int progress, length;
	
	private boolean vertical = false;
	
	public GUIProgressBar(int x, int y, int length){
		this.x = x;
		this.y = y;
		this.length = length;
		this.progress = 0;
		this.width = length;
		this.height = PB_HEIGHT;
	}
	
	public GUIProgressBar(int x, int y, int length, boolean vertical){
		this.x = x;
		this.y = y;
		this.length = length;
		this.progress = 0;
		this.width = length;
		this.height = PB_HEIGHT;
		this.vertical = vertical;
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		if(this.vertical)
			GL11.glRotatef(-90f, 0, 0, 1f);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		Shape.createTexturelessRect(0, 0, length, height, new Color4f(0.1f, 0.2f, 0.1f, 1f));
		glEnd();
		glBegin(GL_QUADS);
		Shape.createTexturelessRect(0, 0, length*((float)progress/100f), height, new Color4f(0.3f, 0.5f, 0.3f, 1f));
		glEnd();
		glEnable(GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	public void setProgress(int progress){
		this.progress = progress;
	}
}
