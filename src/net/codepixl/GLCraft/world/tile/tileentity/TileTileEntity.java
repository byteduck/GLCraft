package net.codepixl.GLCraft.world.tile.tileentity;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileTileEntity extends Tile{
	@Override
	public void onPlace(int x, int y, int z, WorldManager w){
		super.onPlace(x, y, z, w);
		w.entityManager.add(this.getSpawnTileEntity(x,y,z,w));
	}
	
	public TileEntity getSpawnTileEntity(int x, int y, int z, WorldManager w){
		return new TileEntity(x,y,z,w);
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, WorldManager worldManager){
		super.onBreak(x, y, z, drop, worldManager);
		worldManager.getTileEntityAtPos(x, y, z).setDead(true);
	}
}
