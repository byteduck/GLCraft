package net.codepixl.GLCraft.GUI.tileentity;

import java.util.concurrent.Callable;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.Elements.GUIProgressBar;
import net.codepixl.GLCraft.GUI.Inventory.GUIInventoryScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;

public class GUIFurnace extends GUIInventoryScreen{
	
	private TileEntityFurnace furnace;
	private GUISlot in,out,fuel;
	private GUIProgressBar progressBar;
	private GUIProgressBar fuelBar;
	
	
	public GUIFurnace(final TileEntityFurnace furnace, EntityPlayer player) {
		super(player);
		this.furnace = furnace;
	}
	
	public void makeElements(){
		super.makeElements();
		final int HMIDDLE = Constants.getWidth()/2;
		final int VMIDDLE = (int) (Constants.getHeight()/2-GUISlot.size*2);
		final int HSIZE = (int) (GUISlot.size/2f);
		final int PBSIZE = 100;
		Callable<Void> updateListener = new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				furnace.updatedInventory = true;
				return null;
			}
		};
		in = new GUISlot(HMIDDLE-HSIZE-PBSIZE/2-10,VMIDDLE,player);
		out = new GUISlot(HMIDDLE+HSIZE+PBSIZE/2+10,VMIDDLE,player);
		out.canPlace = false;
		progressBar = new GUIProgressBar(HMIDDLE-PBSIZE/2, VMIDDLE-GUIProgressBar.PB_HEIGHT/2, PBSIZE);
		fuelBar = new GUIProgressBar(HMIDDLE-5, VMIDDLE+PBSIZE/2+10, PBSIZE/2, true, new Color4f(0.8f, 0.1f, 0.1f, 1f), new Color4f(0.2f, 0f, 0f, 1f));
		fuel = new GUISlot(HMIDDLE, VMIDDLE+PBSIZE/2+HSIZE+20, player);
		
		in.setListener(updateListener);
		out.setListener(updateListener);
		fuel.setListener(updateListener);
		
		addElements(in, out, progressBar, fuelBar, fuel);
	}
	
	@Override
	public void update(){
		super.update();
		progressBar.setProgress(furnace.getProgressPercent());
		fuelBar.setProgress(furnace.getFuelPercent());
	}
	
	@Override
	public void input(int xof, int yof){
		in.itemstack = furnace.getInventory(0);
		out.itemstack = furnace.getInventory(1);
		fuel.itemstack = furnace.getInventory(2);
		super.input(xof, yof);
		furnace.getInventory()[0] = in.itemstack;
		furnace.getInventory()[1] = out.itemstack;
		furnace.getInventory()[2] = fuel.itemstack;
	}

}
