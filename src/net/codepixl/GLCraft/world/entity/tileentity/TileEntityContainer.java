package net.codepixl.GLCraft.world.entity.tileentity;

import java.util.ArrayList;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileEntityContainer extends TileEntity{
	
	private ItemStack[] inventory;
	
	public TileEntityContainer(int x, int y, int z, int size, WorldManager worldManager) {
		super(x, y, z, worldManager);
		inventory = new ItemStack[size];
		for(int i = 0; i < inventory.length; i++){
			inventory[i] = new ItemStack();
		}
	}
	
	public ItemStack getSlot(int slot){
		return inventory[slot];
	}
	
	public ItemStack[] getInventory(){
		return inventory;
	}
	
	public int addToContainer(ItemStack s){
		int c = s.count;
		ArrayList<Integer> blankSlots = new ArrayList<Integer>();
		ArrayList<Integer> compatibleSlots = new ArrayList<Integer>();
		for(int i = 0; i < inventory.length; i++){
			if(inventory[i].isNull()){
				blankSlots.add(i);
			}else if(inventory[i].compatible(s)){
				compatibleSlots.add(i);
			}
		}
		
		for(int i = 0; i < compatibleSlots.size(); i++){
			int cr = inventory[compatibleSlots.get(i)].addToStack(c);
			c=cr;
			if(cr == 0){
				return 0;
			}
		}
		
		if(blankSlots.size() > 0){
			inventory[blankSlots.get(0)] = new ItemStack(s);
			return 0;
		}
		return c;
	}
	
	public void dropAllItems(){
		for(int i = 0; i < inventory.length; i++){
			if(!inventory[i].isNull()){
				worldManager.spawnEntity(new EntityItem(inventory[i],this.pos,worldManager));
				inventory[i] = new ItemStack();
			}
		}
	}

}
