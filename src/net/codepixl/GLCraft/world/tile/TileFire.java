package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class TileFire extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Fire";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 12;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 0f;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CROSS;
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
	public void onBreak(int x, int y, int z, WorldManager worldManager){
		
	}
	
	@Override
	public void randomTick(int x, int y, int z, WorldManager w){
		if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId() && !Tile.getTile((byte) w.getTileAtPos(x+1, y-1, z)).canPassThrough()){
			w.setTileAtPos(x+1, y, z, getId(), true);
		}
		if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId() && !Tile.getTile((byte) w.getTileAtPos(x-1, y-1, z)).canPassThrough()){
			w.setTileAtPos(x-1, y, z, getId(), true);
		}
		if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId() && !Tile.getTile((byte) w.getTileAtPos(x, y-1, z+1)).canPassThrough()){
			w.setTileAtPos(x, y, z+1, getId(), true);
		}
		if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId() && !Tile.getTile((byte) w.getTileAtPos(x, y-1, z-1)).canPassThrough()){
			w.setTileAtPos(x, y, z-1, getId(), true);
		}
	}
	
	@Override
	public void onCollide(int x, int y, int z, WorldManager worldManager, Entity e) {
		if(e instanceof Mob){
			((Mob) e).hurt(2f,0.5f);
			((Mob) e).setFire(10);
		}
	}

}
