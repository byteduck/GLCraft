package net.codepixl.GLCraft.GUI.Inventory;

import org.lwjgl.input.Keyboard;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.item.ItemStack;
import net.codepixl.GLCraft.item.crafting.CraftingManager;
import net.codepixl.GLCraft.item.crafting.Recipe;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class GUICrafting extends GUIScreen{
	private GUISlot slot1,slot2,slot3,slot4,result;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = Constants.HEIGHT/2;
	private static final int HSIZE = (int) (GUISlot.size/2f);
	private EntityPlayer player;
	public GUICrafting(EntityPlayer p){
		player = p;
		slot1 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE-HSIZE);
		slot2 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE+HSIZE);
		slot3 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE-HSIZE);
		slot4 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE+HSIZE);
		result = new GUISlot(HMIDDLE, VMIDDLE+HSIZE*4);
		this.addElement(slot1);
		this.addElement(slot2);
		this.addElement(slot3);
		this.addElement(slot4);
		this.addElement(result);
	}
	
	@Override
	public void update(){
		super.update();
		ItemStack result = CraftingManager.checkRecipe(new Recipe(slot1.itemstack,slot2.itemstack,slot3.itemstack,slot4.itemstack,null));
		if(result != null){
			this.result.itemstack = result;
		}else{
			this.result.itemstack = null;
		}
	}
	
	@Override
	public void input(){
		super.input();
		GUISlot hoveredSlot = getHoveredSlot();
		if(hoveredSlot != null){
			if(Keyboard.isKeyDown(Keyboard.KEY_1)){
				hoveredSlot.itemstack = player.getInventory(0);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_2)){
				hoveredSlot.itemstack = player.getInventory(1);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_3)){
				hoveredSlot.itemstack = player.getInventory(2);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_4)){
				hoveredSlot.itemstack = player.getInventory(3);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_5)){
				hoveredSlot.itemstack = player.getInventory(4);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_6)){
				hoveredSlot.itemstack = player.getInventory(5);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_7)){
				hoveredSlot.itemstack = player.getInventory(6);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_8)){
				hoveredSlot.itemstack = player.getInventory(7);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_9)){
				hoveredSlot.itemstack = player.getInventory(8);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_0)){
				hoveredSlot.itemstack = null;
			}
		}
	}
	
	private GUISlot getHoveredSlot(){
		if(slot1.hover) return slot1;
		if(slot2.hover) return slot2;
		if(slot3.hover) return slot3;
		if(slot4.hover) return slot4;
		return null;
	}
}
