package net.codepixl.GLCraft.world.entity.tileentity;

import com.nishu.utils.Time;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileEntity extends Entity{

	public TileEntity(int x, int y, int z, WorldManager worldManager) {
		super(x, y, z, worldManager);
	}
	
	@Override
	public void update(){
		timeAlive+=(Time.getDelta()*1000f);
	}

}
