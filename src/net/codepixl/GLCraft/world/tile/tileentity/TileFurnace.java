package net.codepixl.GLCraft.world.tile.tileentity;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;

public class TileFurnace extends TileTileEntity{
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 24;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Furnace";
	}

	@Override
	public TileEntity getSpawnTileEntity(int x, int y, int z, WorldManager w){
		//System.out.println("DOOT OOOODOT");
		return new TileEntityFurnace(x, y, z, w);
	}
	
	@Override
	public boolean onClick(int x, int y, int z, EntityPlayer p, WorldManager w){
		if(((TileEntityFurnace)w.getTileEntityAtPos(x, y, z)) != null){
			((TileEntityFurnace)w.getTileEntityAtPos(x, y, z)).openGUI(w, p);
		}
		return true;
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, WorldManager worldManager){
		super.onBreak(x, y, z, drop, worldManager);
		if(((TileEntityFurnace)worldManager.getTileEntityAtPos(x, y, z)) != null){
			((TileEntityFurnace)worldManager.getTileEntityAtPos(x, y, z)).dropAllItems();
		}
	}
	
	@Override
	public String getIconName(){
		return "furnace_front";
	}
	
	@Override
	public boolean hasMultipleTextures(){
		return true;
	}
	
	@Override
	public String[] getMultiTextureNames(){
		return new String[]{
			"furnace_side",
			"furnace_side",
			"furnace_front",
			"furnace_side",
			"furnace_side",
			"furnace_side"
		};
	}
	
}
