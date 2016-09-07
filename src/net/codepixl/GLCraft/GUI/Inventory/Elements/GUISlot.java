package net.codepixl.GLCraft.GUI.Inventory.Elements;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUISlot extends GUIScreen{
	
	public ItemStack itemstack;
	private int x,y;
	public static final float size = (float)Constants.WIDTH/18f;
	public boolean hover = false;
	
	public GUISlot(int x, int y){
		this.itemstack = new ItemStack();
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void render() {
		Color4f color;
		if(hover){
			color = new Color4f(1,1,1,1);
		}else{
			color = new Color4f(0.7f,0.7f,0.7f,1);
		}
		Spritesheet.atlas.bind();
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCenteredSquare(x,y, color, TextureManager.texture("gui.guislot"), size);
		GL11.glEnd();
		if(!itemstack.isNull()){
			glPushMatrix();
			glTranslatef(x,y,0);
			glScalef(0.7f,0.7f,0.7f);
				glBegin(GL_QUADS);
					if(itemstack.isTile()){
						if(itemstack.getTile().hasMetaTextures()){
							Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), itemstack.getTile().getIconCoords(itemstack.getMeta()), (float)Constants.WIDTH/18f);
						}else{
							Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), itemstack.getTile().getIconCoords(), (float)Constants.WIDTH/18f);
						}
					}else{
						Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), itemstack.getItem().getTexCoords(), (float)Constants.WIDTH/18f);
					}
				glEnd();
			glPopMatrix();
			Constants.FONT.drawString(x, y, Integer.toString(itemstack.count));
			TextureImpl.unbind();
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void input() {
		if(testMouse()){
			this.hover = true;
		}else{
			this.hover = false;
		}
	}
	
	public boolean testMouse(){
		int mouseY = Mouse.getY();
		int mouseX = Mouse.getX();
		mouseY = -mouseY+Constants.HEIGHT;
		if(mouseY <= y+size/2 && mouseY >= y-size/2){
			if(mouseX <= x+size/2 && mouseX >= x-size/2){
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty(){
		return this.itemstack.count == 0 || this.itemstack == null || this.itemstack.equals(new ItemStack());
	}

}
