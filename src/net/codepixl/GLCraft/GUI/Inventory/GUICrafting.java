package net.codepixl.GLCraft.GUI.Inventory;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.crafting.CraftingManager;
import net.codepixl.GLCraft.world.item.crafting.Recipe;

public class GUICrafting extends GUIScreen{
	private GUISlot slot1,slot2,slot3,slot4,result;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = Constants.HEIGHT/2;
	private static final int HSIZE = (int) (GUISlot.size/2f);
	private EntityPlayer player;
	public GUICrafting(EntityPlayer p){
		player = p;
		slot1 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE-HSIZE);
		slot2 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE-HSIZE);
		slot3 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE+HSIZE);
		slot4 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE+HSIZE);
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
	public void input(int xof, int yof){
		super.input(xof,yof);
		GUISlot hoveredSlot = getHoveredSlot();
		if(hoveredSlot != null){
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						ItemStack tempStack = hoveredSlot.itemstack;
						hoveredSlot.itemstack = player.getSelectedItemStack();
						player.setSelectedItemStack(tempStack);
					}
					if(Mouse.isButtonDown(1)){
						if(player.getSelectedItemStack().compatible(hoveredSlot.itemstack)){
							int c = player.getSelectedItemStack().addToStack(hoveredSlot.itemstack.count);
							hoveredSlot.itemstack.count = c;
						}else if(player.getSelectedItemStack().isNull()){
							ItemStack tempStack = hoveredSlot.itemstack;
							hoveredSlot.itemstack = player.getSelectedItemStack();
							player.setSelectedItemStack(tempStack);
						}
					}
				}
			}
			int dWheel = Mouse.getDWheel();
			if(Mouse.hasWheel()) {
				if(player.getSelectedItemStack().isNull()){
					player.setSelectedItemStack(new ItemStack(hoveredSlot.itemstack));
					player.getSelectedItemStack().count = 0;
				}
				if(hoveredSlot.itemstack.isNull()){
					hoveredSlot.itemstack = new ItemStack(player.getSelectedItemStack());
					hoveredSlot.itemstack.count = 0;
				}
				if((player.getSelectedItemStack().compatible(hoveredSlot.itemstack) || player.getSelectedItemStack().isNull()) && dWheel != 0){
					for(int i = 0; i < dWheel / 120; i++){
						if(hoveredSlot.itemstack.count < 64 && player.getSelectedItemStack().count > 0 && !player.getSelectedItemStack().isNull()){
							
							hoveredSlot.itemstack.count++;
							player.getSelectedItemStack().count--;
						}
					}
					for(int i = 0; i > dWheel / 120; i--){
						if(player.getSelectedItemStack().count < 64 && hoveredSlot.itemstack.count > 0 && !hoveredSlot.itemstack.isNull()){
							hoveredSlot.itemstack.count--;
							player.getSelectedItemStack().count++;
						}
					}
				}
				if(player.getSelectedItemStack().count == 0)
					player.setSelectedItemStack(new ItemStack());
				if(hoveredSlot.itemstack.count == 0)
					hoveredSlot.itemstack = new ItemStack();
			}
		}
		if(result.hover && !result.itemstack.isNull()){
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						result.itemstack.count = player.addToInventory(result.itemstack);
						if(result.itemstack.count == 0){
							result.itemstack = new ItemStack();
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
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_1)){
			player.setSelectedSlot(0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_2)){
			player.setSelectedSlot(1);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_3)){
			player.setSelectedSlot(2);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_4)){
			player.setSelectedSlot(3);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_5)){
			player.setSelectedSlot(4);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_6)){
			player.setSelectedSlot(5);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_7)){
			player.setSelectedSlot(6);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_8)){
			player.setSelectedSlot(7);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_9)){
			player.setSelectedSlot(8);
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
