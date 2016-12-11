package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileStone extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Stone";
	}
	
	@Override
	public float getHardness(){
		return 4f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(Tile.Cobblestone);
	}

}
