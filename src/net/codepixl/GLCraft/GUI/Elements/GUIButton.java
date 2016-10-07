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

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.sound.SoundManager;
import net.codepixl.GLCraft.util.Constants;

public class GUIButton extends GUIScreen{

	public static final int BTNHEIGHT = 30;
	public static final int BTNPADDING = 10;
	public static final Color4f BTNCOLOR = new Color4f(0f, 0f, 0f, 1f);
	public static final Color4f BTNHOVERCOLOR = new Color4f(0.25f, 0.25f, 0.25f, 1f);
	public static final Color4f BTNPRESSEDCOLOR = new Color4f(0.35f, 0.35f, 0.35f, 1f);
	public static final Color4f BTNTEXTCOLOR = new Color4f(1f, 1f, 1f, 1f);
	public static final Color4f BTNDISABLEDCOLOR = new Color4f(0.5f,0.5f,0.5f,1f);
	
	private String text;
	private int x,y,padding;
	private Color4f color,hoverColor,pressedColor,textColor,disabledColor;
	private boolean hovered,pressed;
	private Callable<Void> action;
	
	public GUIButton(String text, int x, int y, Callable<Void> action){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = BTNHEIGHT;
		this.padding = BTNPADDING;
		this.color = BTNCOLOR;
		this.hoverColor = BTNHOVERCOLOR;
		this.pressedColor = BTNPRESSEDCOLOR;
		this.textColor = BTNTEXTCOLOR;
		this.disabledColor = BTNDISABLEDCOLOR;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
		this.enabled = true;
	}
	
	public GUIButton(String text, int x, int y, Callable<Void> action, int height, int padding){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = height;
		this.padding = padding;
		this.color = BTNCOLOR;
		this.hoverColor = BTNHOVERCOLOR;
		this.pressedColor = BTNPRESSEDCOLOR;
		this.textColor = BTNTEXTCOLOR;
		this.disabledColor = BTNDISABLEDCOLOR;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
		this.enabled = true;
	}
	
	public GUIButton(String text, int x, int y, Callable<Void> action, Color4f color, Color4f hoverColor, Color4f pressedColor, Color4f textColor, Color4f disabledColor){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = BTNHEIGHT;
		this.padding = BTNPADDING;
		this.color = color;
		this.hoverColor = hoverColor;
		this.pressedColor = pressedColor;
		this.textColor = textColor;
		this.disabledColor = disabledColor;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
		this.enabled = true;
	}
	
	public GUIButton(String text, int x, int y, Callable<Void> action, int height, int padding, Color4f color, Color4f hoverColor, Color4f pressedColor, Color4f textColor){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = height;
		this.padding = padding;
		this.color = color;
		this.hoverColor = hoverColor;
		this.pressedColor = pressedColor;
		this.textColor = textColor;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
		this.enabled = true;
	}
	
	@Override
	public void render() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		glBegin(GL_QUADS);
			if(hovered && !pressed && enabled){
				glColor3f(hoverColor.r,hoverColor.g,hoverColor.b);
			}else if(pressed && enabled){
				glColor3f(pressedColor.r,pressedColor.g,pressedColor.b);
			}else if(!enabled){
				glColor3f(disabledColor.r, disabledColor.g, disabledColor.b);
			}else{
				glColor3f(color.r,color.g,color.b);
			}
			glVertex2f(x-width/2,y-height/2);
			glVertex2f(x-width/2,y+height/2);
			glVertex2f(x+width/2,y+height/2);
			glVertex2f(x+width/2,y-height/2);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		glColor4f(textColor.r,textColor.g,textColor.b,textColor.a);
		Constants.FONT.drawString(x-Constants.FONT.getWidth(text)/2, y-Constants.FONT.getHeight(text)/2, text);
		TextureImpl.unbind();
	}

	@Override
	public void update() {
		
	}

	@Override
	public void input(int xof, int yof) {
		if(testMouse(xof, yof)){
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						if(!pressed && enabled){
							SoundManager.getMainManager().quickPlay("click");
							try {
								this.action.call();
							} catch (Exception e) {
								System.err.println("A button with text "+this.text+" tried to call a callable but failed. Stacktrace:");
								e.printStackTrace();
							}
						}
						this.pressed = true;
					}else{
						this.pressed = false;
					}
				}
			}
			this.hovered = true;
		}else{
			this.hovered = false;
			this.pressed = false;
		}
	}
	
	public boolean testMouse(int xof, int yof){
		int mouseY = Mouse.getY()+xof;
		int mouseX = Mouse.getX()+yof;
		mouseY = -mouseY+Constants.HEIGHT;
		if(mouseY <= y+height/2 && mouseY >= y-height/2){
			if(mouseX <= x+width/2 && mouseX >= x-width/2){
				return true;
			}
		}
		return false;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public boolean getEnabled(){
		return this.enabled;
	}
	
	public void invokeClick(){
		try {
			this.action.call();
		} catch (Exception e) {
			System.err.println("A button with text "+this.text+" tried to call a callable but failed. Stacktrace:");
			e.printStackTrace();
		}
	}
	
	public void setText(String text){
		this.text = text;
		this.width = Constants.FONT.getWidth(text)+padding*2;
	}
	
}
