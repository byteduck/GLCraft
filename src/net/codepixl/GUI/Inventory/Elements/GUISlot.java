package net.codepixl.GUI.Inventory.Elements;

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

import net.codepixl.GLCraft.GUI.Elements.GUIElement;
import net.codepixl.GLCraft.item.Item;
import net.codepixl.GLCraft.item.ItemStack;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.tile.Tile;

public class GUISlot implements GUIElement{
	
	public ItemStack itemstack;
	private int x,y;
	public static final float size = (float)Constants.WIDTH/18f;
	private boolean hover = false;
	
	public GUISlot(int x, int y){
		this.itemstack = null;
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
		Spritesheet.tiles.bind();
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCenteredSquare(x,y, color, new float[]{Spritesheet.tiles.uniformSize()*3,Spritesheet.tiles.uniformSize()}, size);
		GL11.glEnd();
		if(itemstack != null){
			glPushMatrix();
			glTranslatef(x,y,0);
			glScalef(0.7f,0.7f,0.7f);
				glBegin(GL_QUADS);
					if(itemstack.isTile()){
						Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), itemstack.getTile().getIconCoords(), (float)Constants.WIDTH/18f);
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

}
