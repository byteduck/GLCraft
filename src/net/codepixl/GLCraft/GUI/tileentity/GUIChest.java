package net.codepixl.GLCraft.GUI.tileentity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.codepixl.GLCraft.GUI.Inventory.GUIInventoryScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUIChest extends GUIInventoryScreen{
	
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
				slots[i] = new GUISlot((HMIDDLE+HSIZE*10)-HSIZE*2*i,VMIDDLE-HSIZE,p);
			}else{
				slots[i] = new GUISlot((HMIDDLE+HSIZE*10)-HSIZE*2*(i-10),VMIDDLE+HSIZE,p);
			}
			slots[i].itemstack = chest.getSlot(i);
			this.addElement(slots[i]);
		}
	}
	
	@Override
	public void input(int xof, int yof){
		for(int i = 0; i < slots.length; i++){
			slots[i].itemstack = chest.getSlot(i);
		}
		super.input(xof, yof);
		for(int i = 0; i < slots.length; i++){
			chest.getInventory()[i] = slots[i].itemstack;
		}
	}

}
