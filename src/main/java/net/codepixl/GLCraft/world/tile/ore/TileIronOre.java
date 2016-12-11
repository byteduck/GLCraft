package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;

public class TileIronOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Iron Ore";
	}
	
	@Override
	public float getHardness(){
		return 5.5f;
	}

	@Override
	public ItemStack dropItem(){
		return new ItemStack(this);
	}
	
	@Override
	public float getRareness(){
		return 0.006f;
	}
	
	@Override
	public int getMinHeight(){
		return 5;
	}
	
	@Override
	public int getMaxHeight(){
		return 60;
	}
	
	@Override
	public int[] veinRange(){
		return new int[]{1,6};
	}

}
