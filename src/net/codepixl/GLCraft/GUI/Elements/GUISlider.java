package net.codepixl.GLCraft.GUI.Elements;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;

public class GUISlider extends GUIScreen{
	
	private int LBLWIDTH;
	private float val;
	private int max;
	private int min;
	private boolean grabbed = false;
	
	public GUISlider(int x, int y, int length, int min, int max){
		this.x = x;
		this.y = y;
		this.height = 20;
		this.width = length;
		this.setMin(min);
		this.setMax(max);
		this.val = min;
	}
	
	@Override
	public void render(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2f(0, 0);
		GL11.glVertex2f(0, height);
		GL11.glVertex2f(width-LBLWIDTH-5, height);
		GL11.glVertex2f(width-LBLWIDTH-5, 0);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createTexturelessSquare(((float)val/(float)max)*(width-height-LBLWIDTH-5), 0, Color4f.WHITE, height);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Constants.FONT.drawString(width-LBLWIDTH, 0, Integer.toString((int) val));
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
		mouseY = -mouseY+Constants.HEIGHT;
		float barX = ((float)val/(float)max)*(width-height-LBLWIDTH);
		if(mouseY <= height && mouseY >= 0){
			if(mouseX <= barX+20 && mouseX >= barX){
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
			if(!Mouse.isButtonDown(0))
				grabbed = false;
		}
	}
	
	@Override
	public void input(int xof, int yof){
		sliderUpdate(xof, yof);
	}
	
	public float getMax() {
		return max;
	}
	
	public void setMax(int max) {
		this.max = max;
		String tmp = "";
		for(int i = 0; i < String.valueOf(max).length(); i++){
			tmp+="0";
		}
		this.LBLWIDTH = Constants.FONT.getWidth(tmp);
	}
	
	public float getMin() {
		return min;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
}
