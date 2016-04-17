package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.WorldManager;

public class TileAir extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public boolean hasTexture(){
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Air";
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, WorldManager w){
		
	}

}
