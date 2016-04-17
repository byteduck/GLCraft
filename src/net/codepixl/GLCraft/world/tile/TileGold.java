package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.tile.material.Material;

public class TileGold extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Gold Ore";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 7;
	}
	
	@Override
	public String getTextureName(){
		return "gold";
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 4.2f;
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
