package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;

import org.newdawn.slick.opengl.TextureImpl;

public class GUIServer extends GUI{
	public static void render() {
		// TODO Auto-generated method stub
		glEnable(GL_TEXTURE_2D);
		drawBG();
		//glClearColor(0.3f,0.1f,0.3f,1.0f);
		Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth("Running Server...")/2, Constants.HEIGHT/2-Constants.FONT.getHeight("Running Server...")/2, "Running Server...");
		String ip = "Connected IP: "+Constants.ConnIP;
		Constants.FONT.drawString(Constants.WIDTH/2-Constants.FONT.getWidth(ip)/2, (Constants.HEIGHT/2-Constants.FONT.getHeight(ip)/2) + 50, ip);
		TextureImpl.unbind();
	}
	
	public static void drawBG(){
		Spritesheet.tiles.bind();
		glBegin(GL_QUADS);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*2, Spritesheet.tiles.uniformSize());
			glVertex2f(0,0);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*3, Spritesheet.tiles.uniformSize());
			glVertex2f(0,Constants.HEIGHT);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*3, 0);
			glVertex2f(Constants.WIDTH,Constants.HEIGHT);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*2, 0);
			glVertex2f(Constants.WIDTH,0);
		glEnd();
	}
}
