package net.codepixl.GLCraft.GUI.Elements;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.glVertex2f;

public class GUIScrollBox extends GUIElement{
	public int spacing;
	private ArrayList<GUIScreen> items = new ArrayList<GUIScreen>();
	private int currentY, scrollY;
	private float barY = 0;
	
	public GUIScrollBox(int spacing){
		this.spacing = spacing;
		this.currentY = spacing;
		this.scrollY = 0;
	}
	
	public void addItem(GUIScreen i){
		items.add(i);
		i.y = currentY;
		currentY+=i.height+spacing;
	}
	
	@Override
	public void addElement(GUIScreen s){
		GLogger.logerr("Use addItem instead!", LogSource.GLCRAFT);
	}
	
	@Override
	public void addElements(GUIScreen... s){
		GLogger.logerr("Use addItem instead!", LogSource.GLCRAFT);
	}
	
	public boolean removeItem(GUIScreen i){
		boolean ret = items.remove(i);
		if(ret)
			currentY-=i.height;
		return ret;
	}
	
	public void clearItems(){
		items.clear();
		currentY = spacing;
		scrollY = 0;
	}
	
	public boolean removeItem(int i){
		GUIScreen ret = items.remove(i);
		if(ret != null)
			currentY-=ret.height;
		return ret != null;
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		
        Tesselator.stencilArea(0, 0, width, height);
        
        GL11.glTranslatef(0, -scrollY, 0);
        
        Iterator<GUIScreen> i = items.iterator();
        while(i.hasNext()){
        	i.next().renderMain();
        }
        
        GL11.glTranslatef(0, scrollY, 0);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        if(currentY > height){
	        GL11.glBegin(GL11.GL_QUADS);
	        	glVertex2f(width-21, barY);
	        	glVertex2f(width-21, barY+40);
	        	glVertex2f(width-1, barY+40);
	        	glVertex2f(width-1, barY);
	        GL11.glEnd();
	        
        }
        
        GL11.glBegin(GL11.GL_LINES);
        glVertex2f(width-21,0);
        glVertex2f(width-21,height);
        GL11.glEnd();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        
        Tesselator.stencilFinish();
        
        GL11.glPopMatrix();
	}
	
	@Override
	public void drawBG(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_LINE_WIDTH);
		GL11.glColor3f(1f, 1f, 1f);
		GL11.glLineWidth(2f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2f(0, 0);
		GL11.glVertex2f(0, height);
		GL11.glVertex2f(width, height);
		GL11.glVertex2f(width, 0);
		GL11.glEnd();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	private boolean grabbed = false;
	
	private void scrollBarUpdate(int xof, int yof){
		int mouseY = Mouse.getY()+yof;
		int mouseX = Mouse.getX()-xof;
		mouseY = -mouseY+Constants.getHeight();
		if(mouseY <= barY+40 && mouseY >= barY){
			if(mouseX <= width && mouseX >= width-20){
				if(Mouse.isButtonDown(0)){
			    	grabbed = true;
				}
			}
		}
		
		if(grabbed){
			float dy = Mouse.getDY();
			barY-=dy;
			if(barY<0)
	    		barY=0;
	    	else if(barY>height-40)
	    		barY=height-40;
			if(!Mouse.isButtonDown(0))
				grabbed = false;
		}
	}
	
	@Override
	public void input(int xof, int yof){
		if(currentY > height)
        	scrollBarUpdate(xof+x, yof+y);
        
		if(testMouse(xof, yof)){
			Iterator<GUIScreen> i = items.iterator();
	        while(i.hasNext()){
	        	i.next().input(xof+x,yof+y-scrollY);
	        }
	        
	        if(Mouse.hasWheel() && currentY > height){
	        	barY-=Mouse.getDWheel();
	        	if(barY<0)
	        		barY=0;
	        	else if(barY>height-40)
	        		barY=height-40;
	        }
		}
	}
	
	@Override
	public void update(){
		super.update();
    	float currentY = this.currentY-height;
    	float height = this.height;
		scrollY = (int)((barY*currentY)/(height-40));
	}
}
