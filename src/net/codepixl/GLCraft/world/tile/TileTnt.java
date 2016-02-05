package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityTNT;

public class TileTnt extends Tile {
	@Override
	public float[] getTexCoords(){
		return new float[]{Spritesheet.tiles.uniformSize()*5,Spritesheet.tiles.uniformSize()*2};
	}
	
	@Override
	public void onBreak(int x, int y, int z, WorldManager w){
		EntityTNT tnt = new EntityTNT(x,y,z,w);
		w.spawnEntity(tnt);
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Tnt";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 14;
	}
}
