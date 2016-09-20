package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;

public class TileGoldOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Gold Ore";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 7;
	}
	
	@Override
	public float getHardness(){
		return 4.2f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(this);
	}

}
