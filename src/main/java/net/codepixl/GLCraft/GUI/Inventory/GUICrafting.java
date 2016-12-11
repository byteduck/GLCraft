package net.codepixl.GLCraft.GUI.Inventory;

import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.Recipe;
import net.codepixl.GLCraft.world.crafting.Recipe.InvalidRecipeException;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUICrafting extends GUIInventoryScreen{
	private GUISlot slot1,slot2,slot3,slot4,result;
	public GUICrafting(EntityPlayer p){
		super(p);
	}
	
	@Override
	public void makeElements(){
		super.makeElements();
		int HMIDDLE = Constants.getWidth()/2;
		int VMIDDLE = (int) (Constants.getHeight()/2-GUISlot.size*2);
		int HSIZE = (int) (GUISlot.size/2f);
		slot1 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE-HSIZE,player);
		slot2 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE-HSIZE,player);
		slot3 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE+HSIZE,player);
		slot4 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE+HSIZE,player);
		result = new GUISlot(HMIDDLE, VMIDDLE+HSIZE*4,player);
		result.canPlace = false;
		this.addElement(slot1);
		this.addElement(slot2);
		this.addElement(slot3);
		this.addElement(slot4);
		this.addElement(result);
	}
	
	@Override
	public void update(){
		super.update();
		ItemStack result = null;
		try {
			result = CraftingManager.checkRecipe(new Recipe(null,"12","34",'1',slot1.itemstack,'2',slot2.itemstack,'3',slot3.itemstack,'4',slot4.itemstack));
		} catch (InvalidRecipeException e) {
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
	}
	
	@Override
	public void onClose(){
		super.onClose();
		player.dropItem(slot1.itemstack);
		player.dropItem(slot2.itemstack);
		player.dropItem(slot3.itemstack);
		player.dropItem(slot4.itemstack);
		slot1.itemstack = new ItemStack();
		slot2.itemstack = new ItemStack();
		slot3.itemstack = new ItemStack();
		slot4.itemstack = new ItemStack();
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
		}
	}
}
