package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileDirt extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Dirt";
	}
	
	@Override
	public Material getMaterial(){
		return Material.DIRT;
	}

	@Override
	public byte getId() {
		return 23;
	}

	@Override
	public Color4f getColor() {
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 0.5f;
	}
	
	@Override
	public byte getPowerLevel(int x, int y, int z, WorldManager w){
		return 15;
	}

}
