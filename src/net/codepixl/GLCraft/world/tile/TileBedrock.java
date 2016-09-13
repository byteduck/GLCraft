package net.codepixl.GLCraft.world.tile;


import net.codepixl.GLCraft.util.Color4f;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileBedrock extends Tile{
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Bedrock";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 21;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 99999999999999999999999999999999999999f;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return false;
	}
}
