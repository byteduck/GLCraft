package net.codepixl.GLCraft.GUI.Elements;

import net.codepixl.GLCraft.GUI.GUITexturepacks;
import net.codepixl.GLCraft.render.texturepack.TexturePackManager;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.lingala.zip4j.exception.ZipException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by aaron on 2/19/2017.
 */
public class GUITexturepack extends GUIElement {
	private GUITexture icon;
	private GUILabel name;
	public boolean selected = false;
	public GUITexturepacks pgui;
	public GUITexturepack(String name, BufferedImage icon, GUITexturepacks pgui){
		this.width = 100;
		this.height = 100* Constants.getGUIScale();
		this.pgui = pgui;

		this.name = new GUILabel(110, 50 - (int)(Tesselator.getFontHeight()*GUILabel.LBLSIZE)/2, name);
		this.icon = new GUITexture(icon, 5, 5, 90);

		addElements(this.name, this.icon);
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
						pgui.setSelectedTP(this);
						try {
							TexturePackManager.setTexturePack(this.name.text);
						} catch (ZipException | FileNotFoundException | UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void deleteTexture(){
		icon.deleteTexture();
	}
}
