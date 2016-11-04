package net.codepixl.GLCraft.world.entity.tileentity;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;

import net.codepixl.GLCraft.GUI.tileentity.GUIChest;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class TileEntityChest extends TileEntityContainer{

	public TileEntityChest(int x, int y, int z, int size, WorldManager worldManager) {
		super(x, y, z, size, worldManager);
	}
	
	public TileEntityChest(int x, int y, int z, ItemStack[] inventory, WorldManager worldManager){
		super(x, y, z, inventory, worldManager);
	}
	
	@Override
	public void update(){
		super.update();
	}
	
	public void openGUI(WorldManager w, EntityPlayer p){
		w.centralManager.guiManager.showGUI(new GUIChest(this, p));
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		TileEntityContainer c = (TileEntityContainer) TileEntityContainer.fromNBT(t, w);
		TileEntityChest f = new TileEntityChest(c.getBlockpos().x, c.getBlockpos().y, c.getBlockpos().z, c.getInventory().length, w);
		f.setInventory(c.getInventory());
		return f;
	}

}
