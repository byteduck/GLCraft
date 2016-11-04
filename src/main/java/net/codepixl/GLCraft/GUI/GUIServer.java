package net.codepixl.GLCraft.GUI;

import java.net.InetAddress;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.network.Server.InetAddressAndPort;
import net.codepixl.GLCraft.util.Constants;

public class GUIServer extends GUIScreen{
	
	GUILabel name;
	GUILabel addr;
	GUIMultiplayer pgui;
	InetAddressAndPort iaddr;
	public boolean selected;
	
	public GUIServer(GUIMultiplayer pgui, String msg, InetAddress addr, int port){
		this.width = 100;
		this.height = 100;
		this.pgui = pgui;
		this.name = new GUILabel(10, 0, msg);
		this.addr = new GUILabel(10, (int) (100-(Constants.FONT.getHeight()+10)), addr+":"+port);
		this.addr.size = 1f;
		this.iaddr = new InetAddressAndPort(addr,port);
		
		addElement(this.name);
		addElement(this.addr);
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
							pgui.setSelectedServer(this);
						else
							pgui.connect(this);
					}
				}
			}
		}
	}
}
