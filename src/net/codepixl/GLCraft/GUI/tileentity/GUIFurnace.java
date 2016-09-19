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
	private GUISlot in,out;
	private GUIProgressBar progressBar;
	
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
		
		addElements(in,out, progressBar);
	}
	
	@Override
	public void update(){
		progressBar.setProgress(furnace.getProgressPercent());
	}
	
	@Override
	public void input(int xof, int yof){
		in.itemstack = furnace.getSlot(0);
		out.itemstack = furnace.getSlot(1);
		super.input(xof, yof);
		furnace.getInventory()[0] = in.itemstack;
		furnace.getInventory()[1] = out.itemstack;
	}

}
