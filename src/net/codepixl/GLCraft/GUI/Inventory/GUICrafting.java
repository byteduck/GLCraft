package net.codepixl.GLCraft.GUI.Inventory;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.item.Item;
import net.codepixl.GLCraft.item.ItemStack;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GUI.Inventory.Elements.GUISlot;

public class GUICrafting extends GUIScreen{
	private GUISlot slot1,slot2,slot3,slot4;
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = Constants.HEIGHT/2;
	private static final int HSIZE = (int) (GUISlot.size/2f);
	public GUICrafting(EntityPlayer p){
		slot1 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE-HSIZE);
		slot2 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE-HSIZE);
		slot3 = new GUISlot(HMIDDLE+HSIZE,VMIDDLE+HSIZE);
		slot4 = new GUISlot(HMIDDLE-HSIZE,VMIDDLE+HSIZE);
		this.addElement(slot1);
		this.addElement(slot2);
		this.addElement(slot3);
		this.addElement(slot4);
		slot1.itemstack = new ItemStack(Tile.CoalOre,3);
		slot4.itemstack = new ItemStack(Tile.Fire,4);
		slot3.itemstack = new ItemStack(Tile.Glass,2);
		slot2.itemstack = new ItemStack(Item.seeds,5);
	}
}
