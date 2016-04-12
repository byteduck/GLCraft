package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityFallingBlock;

public class TileSand extends Tile{
	@Override
	public String getName() {
		return "Sand";
	}
	
	@Override
	public byte getId() {
		return 18;
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
	public void blockUpdate(int x, int y, int z, WorldManager w){
		if(Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canPassThrough()){
			w.setTileAtPos(x, y, z, Tile.Air.getId(), true);
			w.entityManager.add(new EntityFallingBlock(x, y, z, w));
		}
	}
}
