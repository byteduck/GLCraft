package net.codepixl.GLCraft.world.entity.tileentity;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.GUI.tileentity.GUIChest;
import net.codepixl.GLCraft.GUI.tileentity.GUIFurnace;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.crafting.CraftingManager;
import net.codepixl.GLCraft.world.item.crafting.FurnaceRecipe;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileEntityFurnace extends TileEntityContainer{
	
	private float progress = 0;
	private boolean cooking = false;
	private FurnaceRecipe currentRecipe;

	public TileEntityFurnace(int x, int y, int z, WorldManager worldManager) {
		super(x, y, z, 2, worldManager);
	}
	
	@Override
	public void update(){
		if(currentRecipe != null && progress >= currentRecipe.getCookTime()){
			cooking = false;
			progress = 0;
			if(getInventory()[1].isNull())
				getInventory()[1] = new ItemStack(currentRecipe.getOut());
			else
				getInventory()[1].addToStack(currentRecipe.getOut().count);
			if(getInventory()[0].subFromStack(1) == 1)
				getInventory()[0] = new ItemStack();
		}
		
		if(!cooking){
			currentRecipe = CraftingManager.checkRecipe(getInventory()[0]);
			if(currentRecipe != null && !currentRecipe.getOut().isNull() && (currentRecipe.getOut().compatible(getInventory()[1]) || getInventory()[1].isNull())){
				if(getInventory()[1].isNull() || getInventory()[1].addToStack(currentRecipe.getOut().count) == 0){
					getInventory()[1].subFromStack(currentRecipe.getOut().count);
					cooking = true;
				}
			}
		}
		
		if(cooking && CraftingManager.checkRecipe(getInventory()[0]) != currentRecipe)
			cooking = false;
		
		if(cooking)
			progress += Time.getDelta();
		else
			progress = 0;
	}

	public void openGUI(WorldManager w, EntityPlayer p) {
		w.centralManager.guiManager.showGUI(new GUIFurnace(this, p));
	}
	
	public float getProgress(){
		return progress;
	}
	
	public int getProgressPercent(){
		if(progress == 0)
			return 0;
		else
			return (int) ((progress/currentRecipe.getCookTime())*100);
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException {
		TileEntityContainer c = (TileEntityContainer) TileEntityContainer.fromNBT(t, w);
		TileEntityFurnace f = new TileEntityFurnace(c.getBlockpos().x, c.getBlockpos().y, c.getBlockpos().z, w);
		f.setInventory(c.getInventory());
		return f;
	}
	
	public void writeToNBT(TagCompound t, WorldManager w){
		super.writeToNBT(t);
	}

}