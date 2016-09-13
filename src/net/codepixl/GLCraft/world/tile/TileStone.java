package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Color4f;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileStone extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Stone";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 4f;
	}
	
	@Override
	public ItemStack getDrop(int x, int y, int z, WorldManager w){
		return new ItemStack(Tile.Cobblestone, 1);
	}

}
