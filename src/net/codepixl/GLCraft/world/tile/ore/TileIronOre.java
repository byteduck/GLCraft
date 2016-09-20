package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;

public class TileIronOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Iron Ore";
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 6;
	}
	
	@Override
	public float getHardness(){
		return 5.5f;
	}

	@Override
	public ItemStack dropItem(){
		return new ItemStack(this);
	}

}
