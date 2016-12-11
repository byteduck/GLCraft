package net.codepixl.GLCraft.GUI.Elements;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.render.util.Tesselator;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import static org.lwjgl.opengl.GL11.glColor4f;

public class GUILabel extends GUIElement{
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
		glColor4f(textColor.r,textColor.g,textColor.b,textColor.a);
		int y = -Tesselator.getFontHeight();
		switch(alignment){
		case LEFT:
			for (String line : text.split("\n"))
		        Tesselator.drawString(0, y += Tesselator.getFontHeight(), line);
			break;
		case CENTER:
			for (String line : text.split("\n"))
		        Tesselator.drawString(-Tesselator.getFontWidth(line)/2, y += Tesselator.getFontHeight(), line);
			break;
		case RIGHT:
			for (String line : text.split("\n"))
		        Tesselator.drawString(-Tesselator.getFontWidth(line), y += Tesselator.getFontHeight(), line);
			break;
		}
		TextureImpl.unbind();
		GL11.glPopMatrix();
	}
	
}
