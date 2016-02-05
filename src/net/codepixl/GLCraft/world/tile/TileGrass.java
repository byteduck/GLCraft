package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileGrass extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public String getTextureName(){
		return "grass_top";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Grass";
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
