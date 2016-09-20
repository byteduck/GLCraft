package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileBluestoneOre extends TileOre{
	@Override
	public String getName(){
		return "Bluestone Ore";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 26;
	}
	
	@Override
	public float getHardness(){
		return 4f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(Tile.Bluestone);
	}
	
	@Override
	public int[] dropRange(){
		return new int[]{3,8};
	}
}
