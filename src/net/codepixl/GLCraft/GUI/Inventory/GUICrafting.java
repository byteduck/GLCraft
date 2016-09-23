package net.codepixl.GLCraft.GUI.Inventory;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.Recipe;
import net.codepixl.GLCraft.world.crafting.Recipe.InvalidRecipeException;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUICrafting extends GUIInventoryScreen{
	private GUISlot slot1,slot2,slot3,slot4,result;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = (int) (Constants.HEIGHT/2-GUISlot.size*2);
	private static final int HSIZE = (int) (GUISlot.size/2f);
	public GUICrafting(EntityPlayer p){
		super(p);
		slot1 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE-HSIZE,p);
		slot2 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE-HSIZE,p);
		slot3 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE+HSIZE,p);
		slot4 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE+HSIZE,p);
		result = new GUISlot(HMIDDLE, VMIDDLE+HSIZE*4,p);
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
		player.dropItem(slot1.itemstack);
		player.dropItem(slot2.itemstack);
		player.dropItem(slot3.itemstack);
		player.dropItem(slot4.itemstack);
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
