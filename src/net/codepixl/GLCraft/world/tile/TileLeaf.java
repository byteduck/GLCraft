package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileLeaf extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Leaves";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 9;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 0.2f;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return false;
	}

}
