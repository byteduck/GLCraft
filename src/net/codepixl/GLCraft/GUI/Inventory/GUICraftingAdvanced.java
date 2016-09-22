package net.codepixl.GLCraft.GUI.Inventory;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.Recipe;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUICraftingAdvanced extends GUIInventoryScreen{
	private GUISlot slot1,slot2,slot3,slot4,slot5,slot6,slot7,slot8,slot9,result;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = (int) (Constants.HEIGHT/2-GUISlot.size*2);
	private static final int HSIZE = (int) (GUISlot.size/2f);
	public GUICraftingAdvanced(EntityPlayer p){
		super(p);
		slot1 = new GUISlot(HMIDDLE-HSIZE*2,VMIDDLE-HSIZE*2,p);
		slot2 = new GUISlot(HMIDDLE,VMIDDLE-HSIZE*2,p);
		slot3 = new GUISlot(HMIDDLE+HSIZE*2,VMIDDLE-HSIZE*2,p);
		slot4 = new GUISlot(HMIDDLE-HSIZE*2,VMIDDLE,p);
		slot5 = new GUISlot(HMIDDLE,VMIDDLE,p);
		slot6 = new GUISlot(HMIDDLE+HSIZE*2,VMIDDLE,p);
		slot7 = new GUISlot(HMIDDLE-HSIZE*2,VMIDDLE+HSIZE*2,p);
		slot8 = new GUISlot(HMIDDLE,VMIDDLE+HSIZE*2,p);
		slot9 = new GUISlot(HMIDDLE+HSIZE*2,VMIDDLE+HSIZE*2,p);
		result = new GUISlot(HMIDDLE, VMIDDLE+HSIZE*4,p);
		result.canPlace = false;
		this.addElement(slot1);
		this.addElement(slot2);
		this.addElement(slot3);
		this.addElement(slot4);
		this.addElement(slot5);
		this.addElement(slot6);
		this.addElement(slot7);
		this.addElement(slot8);
		this.addElement(slot9);
		this.addElement(result);
	}
	
	@Override
	public void update(){
		super.update();
		ItemStack result = new ItemStack();
		if(slot3.isEmpty() && slot6.isEmpty() && slot7.isEmpty() && slot8.isEmpty() && slot9.isEmpty())
			result = CraftingManager.checkRecipe(new Recipe(slot1.itemstack,slot2.itemstack,slot4.itemstack,slot5.itemstack,null));
		else
			result = CraftingManager.checkRecipe(new Recipe(slot1.itemstack,slot2.itemstack,slot3.itemstack,slot4.itemstack,slot5.itemstack,slot6.itemstack,slot7.itemstack,slot8.itemstack,slot9.itemstack,null));
		if(result != null){
			this.result.itemstack = new ItemStack(result);
		}else{
			this.result.itemstack = new ItemStack();
		}
		if(slot1.itemstack.count == 0)
			slot1.itemstack = new ItemStack();
		if(slot2.itemstack.count == 0)
			slot2.itemstack = new ItemStack();
		if(slot3.itemstack.count == 0)
			slot3.itemstack = new ItemStack();
		if(slot4.itemstack.count == 0)
			slot4.itemstack = new ItemStack();
		if(slot5.itemstack.count == 0)
			slot5.itemstack = new ItemStack();
		if(slot6.itemstack.count == 0)
			slot6.itemstack = new ItemStack();
		if(slot7.itemstack.count == 0)
			slot7.itemstack = new ItemStack();
		if(slot8.itemstack.count == 0)
			slot8.itemstack = new ItemStack();
		if(slot9.itemstack.count == 0)
			slot9.itemstack = new ItemStack();
	}
	
	@Override
	public void input(int xof, int yof){
		super.input(xof,yof);
		if(result.justTook){
			if(!slot1.itemstack.isNull())
				slot1.itemstack.count-=1;
			if(!slot2.itemstack.isNull())
				slot2.itemstack.count-=1;
			if(!slot3.itemstack.isNull())
				slot3.itemstack.count-=1;
			if(!slot4.itemstack.isNull())
				slot4.itemstack.count-=1;
			if(!slot5.itemstack.isNull())
				slot5.itemstack.count-=1;
			if(!slot6.itemstack.isNull())
				slot6.itemstack.count-=1;
			if(!slot7.itemstack.isNull())
				slot7.itemstack.count-=1;
			if(!slot8.itemstack.isNull())
				slot8.itemstack.count-=1;
			if(!slot9.itemstack.isNull())
				slot9.itemstack.count-=1;
		}
	}
	
	private GUISlot getHoveredSlot(){
		if(slot1.hover) return slot1;
		if(slot2.hover) return slot2;
		if(slot3.hover) return slot3;
		if(slot4.hover) return slot4;
		if(slot5.hover) return slot5;
		if(slot6.hover) return slot6;
		if(slot7.hover) return slot7;
		if(slot8.hover) return slot8;
		if(slot9.hover) return slot9;
		return null;
	}
}
