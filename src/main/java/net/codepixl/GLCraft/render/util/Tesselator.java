package net.codepixl.GLCraft.render.util;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.FormattedString;
import net.codepixl.GLCraft.util.FormattedStringSet;
import net.codepixl.GLCraft.util.logging.GLogger;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Tesselator{
	private static TrueTypeFont FONT;
	/**
	 * Uses OpenGL Stenciling to make only the area defined visible.
	 * Once you are done using the stencil, you must call stencilFinish().
	 **/
	public static void stencilArea(float x, float y, float width, float height){
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);
		GL11.glClearStencil(0);
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);  //Always fail the stencil test
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);   //Set the pixels which failed to 1
		GL11.glStencilMask(0xFF);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        
        GL11.glBegin(GL11.GL_QUADS);
        glVertex2f(x,y);
		glVertex2f(x,y+height);
		glVertex2f(x+width,y+height);
		glVertex2f(x+width,y);
        GL11.glEnd();
        
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glStencilMask(0x00);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
	}
	
	public static void drawOutline(int x, int y, int width, int height, float lineWidth){
		Tesselator.stencilArea(x, y, width, height);
		GL11.glLineWidth(lineWidth);
		glBegin(GL_LINE_LOOP);
			glVertex2f(x,y);
			glVertex2f(x,y+height);
			glVertex2f(x+width,y+height);
			glVertex2f(x+width,y);
		glEnd();
		Tesselator.stencilFinish();
	}
	
	/**
	 * Use after stencilArea().
	 **/
	public static void stencilFinish(){
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
	
	public static void drawTextWithShadow(float x, float y, String text, Color color, Color shadowColor){
		drawString(x+2, y+2, text, shadowColor);
		drawString(x, y, text, color);
	}

	public static void drawTextWithShadow(float x, float y, String text) {
		drawTextWithShadow(x,y,text,Color.white,Color.darkGray);
	}

	public static void drawTextWithShadow(int x, int y, FormattedStringSet text, float opacity) {
		float cx = x;
		for(FormattedString s : text.strings){
			Color fg = s.getColor();
			Color bg = s.getBackgroundColor();
			drawString(cx+2, y+2, s.string, bg.multiply(new Color(1f,1f,1f,opacity)));
			drawString(cx, y, s.string, fg.multiply(new Color(1f,1f,1f,opacity)));
			cx+=Tesselator.getFontWidth(s.string);
		}
	}

	public static void drawString(float x, float y, String s){
		FONT.drawString(x,y,s);
	}

	public static void drawString(float x, float y, String s, Color c){
		FONT.drawString(x,y,s,c);
	}

	public static int getFontWidth(String s){
		return FONT.getWidth(s);
	}

	public static int getFontHeight(){
		return FONT.getHeight();
	}

	public static int getFontHeight(String s){
		return FONT.getHeight(s);
	}

	public static void initFont(){
		int size = 16;
		if(Constants.getWidth() > 2000) size = 32;
		FONT = new TrueTypeFont(new Font("GLCraft", Font.PLAIN, size), true);
	}
}
