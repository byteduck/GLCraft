package net.codepixl.GLCraft.GUI.Elements;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.util.Constants;

public class GUILabel extends GUIScreen{
	public static Color4f LBLTEXTCOLOR = new Color4f(1f,1f,1f,1f);
	public static Alignment LBLALIGNMENT = Alignment.LEFT;
	public static float LBLSIZE = 1.5f;
	
	public Color4f textColor = LBLTEXTCOLOR;
	public String text;
	public Alignment alignment = LBLALIGNMENT;
	public float size = LBLSIZE;
	
	public GUILabel(String text){
		this.text = text;
	}
	
	public GUILabel(String text, Color4f textColor, Alignment alignment, float size){
		this.text = text;
		this.textColor = textColor;
		this.LBLSIZE = size;
	}
	
	public enum Alignment{
		LEFT,CENTER,RIGHT;
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glScalef(size, size, 0);
		int width = Constants.FONT.getWidth(text);
		int height = Constants.FONT.getHeight(text);
		glColor4f(textColor.r,textColor.g,textColor.b,textColor.a);
		switch(alignment){
		case LEFT:
			Constants.FONT.drawString(0, 0, text);
			break;
		case CENTER:
			Constants.FONT.drawString(-width/2, 0, text);
			break;
		case RIGHT:
			Constants.FONT.drawString(-width, 0, text);
			break;
		}
		TextureImpl.unbind();
		GL11.glPopMatrix();
	}
	
}
