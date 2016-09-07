package net.codepixl.GLCraft.GUI.tileentity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUIChest extends GUIScreen{
	
	private TileEntityChest chest;
	private EntityPlayer player;
	private GUISlot[] slots;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = Constants.HEIGHT/2;
	private static final int HSIZE = (int) (GUISlot.size/2f);
	
	public GUIChest(TileEntityChest chest, EntityPlayer p) {
		this.player = p;
		this.chest = chest;
		slots = new GUISlot[20];
		for(int i = 0; i < slots.length; i++){
			if(i < 10){
				slots[i] = new GUISlot((HMIDDLE+HSIZE*10)-HSIZE*2*i,VMIDDLE-HSIZE);
			}else{
				slots[i] = new GUISlot((HMIDDLE+HSIZE*10)-HSIZE*2*(i-10),VMIDDLE+HSIZE);
			}
			slots[i].itemstack = chest.getSlot(i);
			this.addElement(slots[i]);
		}
	}
	
	@Override
	public void update(){
		super.update();
		for(int i = 0; i < slots.length; i++){
			chest.getInventory()[i] = slots[i].itemstack;
		}
	}
	
	@Override
	public void input(int xof, int yof){
		super.input(xof, yof);
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
		for(GUISlot slot:slots){
			if(slot.hover) return slot;
		}
		return null;
	}

}
