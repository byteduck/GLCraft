package net.codepixl.GLCraft.GUI;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.data.saves.Save;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.sound.SoundManager;

public class GUISave extends GUIScreen{
	
	public Save save;
	public boolean selected = false;
	GUILabel dispName;
	GUILabel name;
	GUISinglePlayer pgui;
	
	public GUISave(Save s, GUISinglePlayer pgui){
		this.pgui = pgui;
		this.save = s;
		
		this.dispName = new GUILabel(s.dispName);
		this.dispName.x = 10;
		
		this.name = new GUILabel("(Saved in '"+s.name+"')");
		this.name.size = 1f;
		this.name.y = (int) (100-(Constants.FONT.getHeight()*this.name.size+10));
		this.name.x = 10;
		
		this.width = 100;
		this.height = 100;
		
		addElement(name);
		addElement(dispName);
	}
	
	@Override
	public void drawBG(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_LINE_WIDTH);
		if(selected)
			GL11.glColor3f(0f, 0f, 0f);
		else
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
	
	@Override
	public void input(int xof, int yof){
		super.input(xof, yof);
		if(testMouse(xof,yof)){
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						if(!selected)
							pgui.setSelectedSave(this);
						else
							pgui.loadSave(this);
					}
				}
			}
		}
	}
}
