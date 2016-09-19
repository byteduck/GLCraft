package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileBluestoneOre extends Tile{
	@Override
	public String getName(){
		return "Bluestone Ore";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
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
	public ItemStack getDrop(int x, int y, int z, WorldManager w){
		return new ItemStack(Tile.Bluestone, Constants.randInt(4, 8));
	}
}
