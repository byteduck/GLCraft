package net.codepixl.GLCraft.GUI.Inventory;

import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.Recipe;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUICraftingAdvanced extends GUIInventoryScreen{
	private GUISlot slot1,slot2,slot3,slot4,slot5,slot6,slot7,slot8,slot9,result;
	public GUICraftingAdvanced(EntityPlayer p){
		super(p);
	}
	
	@Override
	public void makeElements(){
		super.makeElements();
		final int HMIDDLE = Constants.getWidth()/2;
		final int VMIDDLE = (int) (Constants.getHeight()/2-GUISlot.size*2);
		final int HSIZE = (int) (GUISlot.size/2f);
		slot1 = new GUISlot(HMIDDLE-HSIZE*2,VMIDDLE-HSIZE*2,player);
		slot2 = new GUISlot(HMIDDLE,VMIDDLE-HSIZE*2,player);
		slot3 = new GUISlot(HMIDDLE+HSIZE*2,VMIDDLE-HSIZE*2,player);
		slot4 = new GUISlot(HMIDDLE-HSIZE*2,VMIDDLE,player);
		slot5 = new GUISlot(HMIDDLE,VMIDDLE,player);
		slot6 = new GUISlot(HMIDDLE+HSIZE*2,VMIDDLE,player);
		slot7 = new GUISlot(HMIDDLE-HSIZE*2,VMIDDLE+HSIZE*2,player);
		slot8 = new GUISlot(HMIDDLE,VMIDDLE+HSIZE*2,player);
		slot9 = new GUISlot(HMIDDLE+HSIZE*2,VMIDDLE+HSIZE*2,player);
		result = new GUISlot(HMIDDLE, VMIDDLE+HSIZE*4,player);
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
		try{
			if(slot3.isEmpty() && slot6.isEmpty() && slot7.isEmpty() && slot8.isEmpty() && slot9.isEmpty())
				result = CraftingManager.checkRecipe(new Recipe(null,"12","34",'1',slot1.itemstack,'2',slot2.itemstack,'3',slot4.itemstack,'4',slot5.itemstack));
			else if(slot1.isEmpty() && slot4.isEmpty() && slot7.isEmpty() && slot8.isEmpty() && slot9.isEmpty())
				result = CraftingManager.checkRecipe(new Recipe(null,"12","34",'1',slot2.itemstack,'2',slot3.itemstack,'3',slot5.itemstack,'4',slot6.itemstack));
			else if(slot1.isEmpty() && slot2.isEmpty() && slot3.isEmpty() && slot6.isEmpty() && slot9.isEmpty())
				result = CraftingManager.checkRecipe(new Recipe(null,"12","34",'1',slot4.itemstack,'2',slot5.itemstack,'3',slot7.itemstack,'4',slot8.itemstack));
			else if(slot1.isEmpty() && slot2.isEmpty() && slot3.isEmpty() && slot4.isEmpty() && slot7.isEmpty())
				result = CraftingManager.checkRecipe(new Recipe(null,"12","34",'1',slot5.itemstack,'2',slot6.itemstack,'3',slot8.itemstack,'4',slot9.itemstack));
			else
				result = CraftingManager.checkRecipe(new Recipe(null,"123","456","789",'1',slot1.itemstack,'2',slot2.itemstack,'3',slot3.itemstack,'4',slot4.itemstack,'5',slot5.itemstack,'6',slot6.itemstack,'7',slot7.itemstack,'8',slot8.itemstack,'9',slot9.itemstack));
		}catch(Recipe.InvalidRecipeException e){
			e.printStackTrace();
		}
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
	
	@Override
	public void onClose(){
		super.onClose();
		player.dropItem(slot1.itemstack);
		player.dropItem(slot2.itemstack);
		player.dropItem(slot3.itemstack);
		player.dropItem(slot4.itemstack);
		player.dropItem(slot5.itemstack);
		player.dropItem(slot6.itemstack);
		player.dropItem(slot7.itemstack);
		player.dropItem(slot8.itemstack);
		player.dropItem(slot9.itemstack);
		slot1.itemstack = new ItemStack();
		slot2.itemstack = new ItemStack();
		slot3.itemstack = new ItemStack();
		slot4.itemstack = new ItemStack();
		slot5.itemstack = new ItemStack();
		slot6.itemstack = new ItemStack();
		slot7.itemstack = new ItemStack();
		slot8.itemstack = new ItemStack();
		slot9.itemstack = new ItemStack();
	}
	
	protected GUISlot getHoveredSlot(){
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
