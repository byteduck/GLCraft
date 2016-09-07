package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.tile.material.Material;

public class TileDirt extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Coal Ore";
	}
	
	@Override
	public Material getMaterial(){
		return Material.DIRT;
	}
	
	@Override
	public String getTextureName(){
		return "dirt";
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
	public boolean isTransparent() {
		return false;
	}

	@Override
	public boolean canPassThrough() {
		return false;
	}

}
