package net.codepixl.GLCraft.render.util;

import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import net.codepixl.GLCraft.util.Constants;

public class Tesselator{
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
	
	/**
	 * Use after stencilArea().
	 **/
	public static void stencilFinish(){
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
	
	public static void drawTextWithShadow(float x, float y, String text, Color color, Color shadowColor){
		Constants.FONT.drawString(x+2, y+2, text, shadowColor);
		Constants.FONT.drawString(x, y, text, color);
	}

	public static void drawTextWithShadow(float x, float y, String text) {
		drawTextWithShadow(x,y,text,Color.white,Color.darkGray);
	}
}
