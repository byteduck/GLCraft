package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.util.Constants;

public abstract class GUI {
	public static void render() {}
	public static void initGL() {}
	public static void update() {}
	public static void input() {}
	
	public static boolean testClick(int mouseX, int mouseY, int x, int y, int width, int height){
		mouseY = -mouseY+Constants.HEIGHT;
		if(mouseY <= y+height/2 && mouseY >= y-height/2){
			if(mouseX <= x+width/2 && mouseX >= x-width/2){
				return true;
			}
		}
		return false;
	}
	
	public static void createButton(int x, int y, int padding, int height, String text, Color4f color, Color4f stringColor){
		glDisable(GL_TEXTURE_2D);
		int width = Constants.FONT.getWidth(text) + padding*2;
		glBegin(GL_QUADS);
			glColor3f(color.r,color.g,color.b);
			glVertex2f(x-width/2,y-height/2);
			glVertex2f(x-width/2,y+height/2);
			glVertex2f(x+width/2,y+height/2);
			glVertex2f(x+width/2,y-height/2);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		glColor4f(stringColor.r,stringColor.g,stringColor.b,stringColor.a);
		Constants.FONT.drawString(x-Constants.FONT.getWidth(text)/2, y-Constants.FONT.getHeight(text)/2, text);
		TextureImpl.unbind();
	}
}
