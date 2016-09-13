package net.codepixl.GLCraft.GUI.Inventory.Elements;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Color4f;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Mouse;
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
		float size = Constants.WIDTH/18f;
		if(!itemstack.isNull()){
			glPushMatrix();
					if(itemstack.isTile()){
						glTranslatef(x-size/2,y-size/2,0);
						glScalef(0.5f,0.5f,0.5f);
						GL11.glRotatef(140f,1.0f,0.0f,0.0f);
						GL11.glRotatef(45f,0.0f,1.0f,0.0f);
						glBegin(GL_QUADS);
						if(itemstack.getTile().hasMetaTextures()){
							Shape.createCube(size/2f,-size*1.5f,0, new Color4f(1f,1f,1f,1f), itemstack.getTile().getTexCoords(itemstack.getMeta()), size);
						}else{
							Shape.createCube(size/2f,-size*1.5f,0, new Color4f(1f,1f,1f,1f), itemstack.getTile().getTexCoords(), size);
						}
					}else{
						glTranslatef(x,y,0);
						glScalef(0.7f, 0.7f, 0.7f);
						glBegin(GL_QUADS);
						Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), itemstack.getItem().getTexCoords(), size);
					}
				glEnd();
			glPopMatrix();
			if(itemstack.count != 1)
				Tesselator.drawTextWithShadow(x, y, Integer.toString(itemstack.count));
			TextureImpl.unbind();
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void input(int xof, int yof) {
		if(testMouse(xof,yof)){
			this.hover = true;
		}else{
			this.hover = false;
		}
	}
	
	@Override
	public boolean testMouse(int xof, int yof){
		int mouseY = Mouse.getY()-yof;
		int mouseX = Mouse.getX()-xof;
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
