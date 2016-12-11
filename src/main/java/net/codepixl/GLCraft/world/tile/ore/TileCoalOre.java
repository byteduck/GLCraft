package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;

public class TileCoalOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Coal Ore";
	}
	
	@Override
	public float getHardness(){
		return 4.5f;
	}
	
	@Override
	public float getRareness(){
		return 0.01f;
	}
	
	@Override
	public int getMinHeight(){
		return 5;
	}
	
	@Override
	public int getMaxHeight(){
		return 80;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(Item.Coal);
	}
	
	@Override
	public int[] veinRange(){
		return new int[]{2,10};
	}

}
