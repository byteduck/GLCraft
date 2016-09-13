package net.codepixl.GLCraft.GUI.Elements;

import static org.lwjgl.opengl.GL11.glColor4f;

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
	
	public GUILabel(int x, int y, String text){
		this.x = x;
		this.y = y;
		this.text = text;
	}
	
	public GUILabel(int x, int y, String text, Color4f textColor, Alignment alignment, float size){
		this.x = x;
		this.y = y;
		this.text = text;
		this.textColor = textColor;
		this.size = size;
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
