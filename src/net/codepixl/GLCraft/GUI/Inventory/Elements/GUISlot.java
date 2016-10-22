package net.codepixl.GLCraft.GUI.Inventory.Elements;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.Elements.GUIElement;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUISlot extends GUIElement{
	
	public ItemStack itemstack;
	public static float size = 1000f/18f;
	public boolean hover = false;
	public boolean canPickup = true, canPlace = true;
	public EntityPlayer player;
	public boolean justTook = false, justPlaced = false, showLabel = true;
	private Callable<Void> callable;
	
	public GUISlot(int x, int y, EntityPlayer player){
		this.itemstack = new ItemStack();
		this.x = x;
		this.y = y;
		this.player = player;
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
		Shape.createCenteredSquare(0,0, color, TextureManager.texture("gui.guislot"), size);
		GL11.glEnd();
		itemstack.renderIcon(0, 0, size);
	}

	@Override
	public void input(int xof, int yof) {
		this.justTook = false;
		this.justPlaced = false;
		if(testMouse(xof,yof)){
			player.hoverSlot = this;
			this.hover = true;
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						ItemStack pItemStack = player.mouseItem;
						ItemStack itemStack = this.itemstack;
						if(this.player.mouseItem.isNull() && this.canPickup){
							this.itemstack = pItemStack;
							this.player.mouseItem = itemStack;
							this.justTook = true;
						}else{
							if(this.canPickup && this.canPlace){
								if(itemstack.compatible(pItemStack)){
									int added = itemStack.addToStack(pItemStack.count);
									pItemStack.count = added;
									if(pItemStack.count == 0)
										this.player.mouseItem = new ItemStack();
									this.justPlaced = true;
								}else{
									this.itemstack = pItemStack;
									this.player.mouseItem = itemStack;
									this.justTook = true;
									this.justPlaced = true;
								}
							}else if(this.canPlace && !this.canPickup && itemStack.isNull()){
								this.itemstack = pItemStack;
								this.player.mouseItem = itemStack;
								this.justPlaced = true;
							}else if(this.canPickup && itemStack.compatible(pItemStack)){
								int added = pItemStack.addToStack(itemStack.count);
								itemStack.count = added;
								if(itemStack.count == 0)
									this.itemstack = new ItemStack();
								this.justTook = true;
							}
						}
					}else if(Mouse.isButtonDown(1)){
						ItemStack pItemStack = player.mouseItem;
						ItemStack itemStack = this.itemstack;
						if(!pItemStack.isNull() && (itemStack.compatible(pItemStack) || itemStack.isNull())){
							if(itemStack.isNull()){
								this.itemstack = new ItemStack(pItemStack);
								this.itemstack.count = 0;
								itemStack = this.itemstack;
							}
							int added = itemStack.addToStack(1);
							if(added == 0)
								pItemStack.count--;
							if(pItemStack.count == 0)
								this.player.mouseItem = new ItemStack();
							this.justPlaced = true;
						}
					}
				}
			}
		}else{
			this.hover = false;
		}
		if((this.justTook || this.justPlaced) && this.callable != null)
			try {
				callable.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public boolean testMouse(int xof, int yof){
		int mouseY = Mouse.getY()-yof;
		int mouseX = Mouse.getX()-xof;
		mouseY = -mouseY+Constants.getHeight();
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
	
	public void setListener(Callable<Void> callable){
		this.callable = callable;
	}

}
