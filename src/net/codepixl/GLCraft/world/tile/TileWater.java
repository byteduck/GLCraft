package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.WorldManager;

public class TileWater extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Water";
	}
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isTranslucent(){
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public float getHardness(){
		return 0.1f;
	}
	@Override
	public void tick(int x, int y, int z, WorldManager w){
		//System.out.println("water tick");
		if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId()){
			w.setTileAtPos(x+1, y, z, getId(), true);
		}
		if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId()){
			w.setTileAtPos(x-1, y, z, getId(), true);
		}
		if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId()){
			w.setTileAtPos(x, y, z+1, getId(), true);
		}
		if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId()){
			w.setTileAtPos(x, y, z-1, getId(), true);
		}
		if(w.getTileAtPos(x, y-1, z) == Tile.Air.getId()){
			w.setTileAtPos(x, y-1, z, getId(), true);
		}
	}
	@Override
	public boolean needsConstantTick(){
		return true;
	}
}
