package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.world.tile.material.Material;

public class TileCobblestone extends Tile{
	@Override
	public String getName(){
		return "Cobblestone";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}
	
	@Override
	public float getHardness(){
		return 4f;
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 25;
	}
}
