package net.codepixl.GLCraft.world.tile.tileentity;

import net.codepixl.GLCraft.util.Color4f;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileChest extends TileTileEntity{
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Chest";
	}
	
	@Override
	public Material getMaterial(){
		return Material.WOOD;
	}
	
	@Override
	public String getIconName(){
		return "chest_front";
	}
	
	@Override
	public boolean hasMultipleTextures(){
		return true;
	}
	
	@Override
	public String[] getMultiTextureNames(){
		return new String[]{
			"chest_top",
			"chest_top",
			"chest_front",
			"chest_side",
			"chest_side",
			"chest_side"
		};
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 22;
	}
	
	@Override
	public TileEntity getSpawnTileEntity(int x, int y, int z, WorldManager w){
		return new TileEntityChest(x, y, z, 20, w);
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 2.5f;
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, WorldManager worldManager){
		super.onBreak(x, y, z, drop, worldManager);
		((TileEntityChest)worldManager.getTileEntityAtPos(x, y, z)).dropAllItems();
	}
	
	@Override
	public boolean onClick(int x, int y, int z, EntityPlayer p, WorldManager w){
		if(((TileEntityChest)w.getTileEntityAtPos(x, y, z)) != null){
			((TileEntityChest)w.getTileEntityAtPos(x, y, z)).openGUI(w, p);
		}
		return true;
	}
}
