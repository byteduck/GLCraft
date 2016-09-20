package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;

public class TileCoalOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Coal Ore";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 5;
	}
	
	@Override
	public float getHardness(){
		return 4.5f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(this);
	}

}
