package net.codepixl.GLCraft.GUI.tileentity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.codepixl.GLCraft.GUI.Elements.GUIProgressBar;
import net.codepixl.GLCraft.GUI.Inventory.GUIInventoryScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUIFurnace extends GUIInventoryScreen{
	
	private TileEntityFurnace furnace;
	private EntityPlayer player;
	private GUISlot in,out,fuel;
	private GUIProgressBar progressBar;
	private GUIProgressBar fuelBar;
	
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = Constants.HEIGHT/2;
	private static final int HSIZE = (int) (GUISlot.size/2f);
	private static final int PBSIZE = 100;
	
	
	public GUIFurnace(TileEntityFurnace furnace, EntityPlayer player) {
		this.furnace = furnace;
		this.player = player;
		in = new GUISlot(HMIDDLE-HSIZE-PBSIZE/2-10,VMIDDLE,player);
		out = new GUISlot(HMIDDLE+HSIZE+PBSIZE/2+10,VMIDDLE,player);
		out.canPlace = false;
		progressBar = new GUIProgressBar(HMIDDLE-PBSIZE/2, VMIDDLE-GUIProgressBar.PB_HEIGHT/2, PBSIZE);
		fuelBar = new GUIProgressBar(HMIDDLE-5, VMIDDLE+PBSIZE/2+10, PBSIZE/2, true);
		fuel = new GUISlot(HMIDDLE, VMIDDLE+PBSIZE/2+HSIZE+20, player);
		
		addElements(in, out, progressBar, fuelBar, fuel);
	}
	
	@Override
	public void update(){
		progressBar.setProgress(furnace.getProgressPercent());
		fuelBar.setProgress(furnace.getFuelPercent());
	}
	
	@Override
	public void input(int xof, int yof){
		in.itemstack = furnace.getSlot(0);
		out.itemstack = furnace.getSlot(1);
		fuel.itemstack = furnace.getSlot(2);
		super.input(xof, yof);
		furnace.getInventory()[0] = in.itemstack;
		furnace.getInventory()[1] = out.itemstack;
		furnace.getInventory()[2] = fuel.itemstack;
	}

}
