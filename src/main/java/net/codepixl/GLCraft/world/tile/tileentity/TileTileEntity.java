package net.codepixl.GLCraft.world.tile.tileentity;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.EnumFacing;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileTileEntity extends Tile{
	@Override
	public void onPlace(int x, int y, int z, byte meta, EnumFacing facing, WorldManager w){
		super.onPlace(x, y, z, meta, facing, w);
		w.entityManager.add(this.getSpawnTileEntity(x,y,z,w));
	}
	
	public TileEntity getSpawnTileEntity(int x, int y, int z, WorldManager w){
		return new TileEntity(x,y,z,w);
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, BreakSource source, WorldManager worldManager){
		super.onBreak(x, y, z, drop, source, worldManager);
		if(worldManager.getTileEntityAtPos(x, y, z) != null) worldManager.getTileEntityAtPos(x, y, z).setDead(true);
	}
}
