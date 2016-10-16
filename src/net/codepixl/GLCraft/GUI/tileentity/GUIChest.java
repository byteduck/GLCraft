package net.codepixl.GLCraft.GUI.tileentity;

import java.util.concurrent.Callable;

import net.codepixl.GLCraft.GUI.Inventory.GUIInventoryScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;

public class GUIChest extends GUIInventoryScreen{
	
	private TileEntityChest chest;
	private GUISlot[] slots;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = (int) (Constants.HEIGHT/2-GUISlot.size*2);
	private static final int HSIZE = (int) (GUISlot.size/2f);
	
	public GUIChest(final TileEntityChest chest, EntityPlayer p) {
		super(p);
		this.chest = chest;
		slots = new GUISlot[20];
		Callable<Void> updateListener = new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				chest.updatedInventory = true;
				return null;
			}
		};
		for(int i = 0; i < slots.length; i++){
			if(i < 10){
				slots[i] = new GUISlot((HMIDDLE+HSIZE*10)-HSIZE*2*i-(int)GUISlot.size/2,VMIDDLE-HSIZE,p);
			}else{
				slots[i] = new GUISlot((HMIDDLE+HSIZE*10)-HSIZE*2*(i-10)-(int)GUISlot.size/2,VMIDDLE+HSIZE,p);
			}
			slots[i].itemstack = chest.getInventory(i);
			slots[i].setListener(updateListener);
			this.addElement(slots[i]);
		}
	}
	
	@Override
	public void input(int xof, int yof){
		for(int i = 0; i < slots.length; i++){
			slots[i].itemstack = chest.getInventory(i);
		}
		super.input(xof, yof);
		for(int i = 0; i < slots.length; i++){
			chest.getInventory()[i] = slots[i].itemstack;
		}
	}

}
