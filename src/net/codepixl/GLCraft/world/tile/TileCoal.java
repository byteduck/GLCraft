package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileCoal extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Coal Ore";
	}
	
	@Override
	public String getTextureName(){
		return "coal";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 4.5f;
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
