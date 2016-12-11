package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileCondensedBluestone extends Tile{
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Condensed Bluestone";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}
	
	@Override
	public float getHardness(){
		return 2f;
	}
	
	@Override
	public byte getPowerLevel(int x, int y, int z, WorldManager w){
		return 15;
	}
	
	@Override
	public ItemStack getDrop(int x, int y, int z, BreakSource source, WorldManager w){
		if(source.type == BreakSource.Type.PLAYER)
			return new ItemStack(Tile.Bluestone, 9);
		else
			return new ItemStack();
	}
	
}
