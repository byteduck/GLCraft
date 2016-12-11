package net.codepixl.GLCraft.GUI.Elements;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.concurrent.Callable;

public class GUISlider extends GUIElement{
	
	public static int HEIGHT = 20;
	
	private float val;
	private int max;
	private int min;
	private boolean grabbed = false;
	private Callable<Void> callback;
	public String maxlbl = "";
	public String lbl;
	
	public GUISlider(String lbl, int x, int y, int length, int min, int max, Callable<Void> callback){
		this.x = x;
		this.y = y;
		this.height = HEIGHT;
		this.width = length;
		this.min = min;
		this.max = max;
		this.val = min;
		this.callback = callback;
		this.lbl = lbl;
	}
	
	@Override
	public void render(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createTexturelessRect2D(0, 0, width, height, Color4f.BLACK);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		Shape.createTexturelessRect2D(0, 0, width, height, Color4f.GRAY);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createTexturelessRect2D(((float)(val-min)/(float)(max-min))*(width-10), 0, 10f, height, Color4f.WHITE);
		GL11.glEnd();
		
		GL11.glColor3f(0.75f, 0.75f, 0.75f);
		Tesselator.drawOutline(0, 0, width, height, 1.5f);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		String lblend = (!maxlbl.equals("") && val == max) ? maxlbl : Integer.toString((int) val);
		String fulllbl = lbl+":"+lblend;
		Tesselator.drawString(width/2-Tesselator.getFontWidth(fulllbl)/2, height/2-Tesselator.getFontHeight()/2, fulllbl, Color.gray);
		TextureImpl.unbind();
	}
	
	public int getVal(){
		return (int) val;
	}
	
	public void setVal(int val){
		this.val = val;
	}
	
	private void sliderUpdate(int xof, int yof){
		int mouseY = Mouse.getY()+yof;
		int mouseX = Mouse.getX()-xof;
		mouseY = -mouseY+Constants.getHeight();
		float barX = (((float)val-min)/((float)max-min))*(width-10);
		if(mouseY <= height && mouseY >= 0){
			if(mouseX <= barX+10 && mouseX >= barX){
				if(Mouse.isButtonDown(0)){
			    	grabbed = true;
				}
			}
		}
		
		if(grabbed){
			float dx = Mouse.getDX();
			val+=dx/(float)(width-height)*max;
			if(val<min)
	    		val=min;
	    	else if(val>max)
	    		val=max;
			if(dx != 0){
				try {
					callback.call();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!Mouse.isButtonDown(0))
				grabbed = false;
		}
	}
	
	@Override
	public void input(int xof, int yof){
		sliderUpdate(xof+x, yof+y);
	}
	
	public float getMax() {
		return max;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public float getMin() {
		return min;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
}
