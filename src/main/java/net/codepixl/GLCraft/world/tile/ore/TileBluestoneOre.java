package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.tool.Tool;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileBluestoneOre extends TileOre{
	@Override
	public String getName(){
		return "Bluestone Ore";
	}

	@Override
	public Tool toolUsed(){
		return (Tool) Tool.IronPick;
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
	
	@Override
	public float getRareness(){
		return 0.008f;
	}
	
	@Override
	public int getMinHeight(){
		return 1;
	}
	
	@Override
	public int getMaxHeight(){
		return 15;
	}
}
