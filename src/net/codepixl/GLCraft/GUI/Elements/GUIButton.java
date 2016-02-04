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
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.sound.SoundManager;
import net.codepixl.GLCraft.util.Constants;

public class GUIButton implements GUIElement{
	
	private String text;
	private int x,y,height,padding;
	private Color4f color,hoverColor,pressedColor,textColor;
	private boolean hovered,pressed;
	private int width;
	private Callable<Void> action;
	
	public GUIButton(String text, int x, int y, Callable<Void> action){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = Constants.BTNHEIGHT;
		this.padding = Constants.BTNPADDING;
		this.color = Constants.BTNCOLOR;
		this.hoverColor = Constants.BTNHOVERCOLOR;
		this.pressedColor = Constants.BTNPRESSEDCOLOR;
		this.textColor = Constants.BTNTEXTCOLOR;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
	}
	
	public GUIButton(String text, int x, int y, Callable<Void> action, int height, int padding){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = height;
		this.padding = padding;
		this.color = Constants.BTNCOLOR;
		this.hoverColor = Constants.BTNHOVERCOLOR;
		this.pressedColor = Constants.BTNPRESSEDCOLOR;
		this.textColor = Constants.BTNTEXTCOLOR;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
	}
	
	public GUIButton(String text, int x, int y, Callable<Void> action, Color4f color, Color4f hoverColor, Color4f pressedColor, Color4f textColor){
		this.text = text;
		this.x = x;
		this.y = y;
		this.height = Constants.BTNHEIGHT;
		this.padding = Constants.BTNPADDING;
		this.color = color;
		this.hoverColor = hoverColor;
		this.pressedColor = pressedColor;
		this.textColor = textColor;
		this.width = Constants.FONT.getWidth(text) + padding*2;
		this.action = action;
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
	}
	
	@Override
	public void render() {
		glDisable(GL_TEXTURE_2D);
		int width = Constants.FONT.getWidth(text) + padding*2;
		glBegin(GL_QUADS);
			if(hovered && !pressed){
				glColor3f(hoverColor.r,hoverColor.g,hoverColor.b);
			}else if(pressed){
				glColor3f(pressedColor.r,pressedColor.g,pressedColor.b);
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
	public void input() {
		if(testMouse()){
			if(Mouse.isButtonDown(0)){
				if(!pressed){
					SoundManager.getMainManager().quickPlay("click");
					try {
						this.action.call();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("A button with text "+this.text+" tried to call a callable but failed.");
					}
				}
				this.pressed = true;
			}else{
				this.pressed = false;
			}
			this.hovered = true;
		}else{
			this.hovered = false;
			this.pressed = false;
		}
	}
	
	public boolean testMouse(){
		int mouseY = Mouse.getY();
		int mouseX = Mouse.getX();
		mouseY = -mouseY+Constants.HEIGHT;
		if(mouseY <= y+height/2 && mouseY >= y-height/2){
			if(mouseX <= x+width/2 && mouseX >= x-width/2){
				return true;
			}
		}
		return false;
	}
	
}
