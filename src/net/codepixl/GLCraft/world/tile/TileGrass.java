package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Color4f;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileGrass extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public Material getMaterial(){
		return Material.DIRT;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public String getIconName(){
		return "grass_side";
	}
	
	@Override
	public boolean hasMultipleTextures(){
		return true;
	}
	
	@Override
	public String[] getMultiTextureNames(){
		return new String[]{
			"dirt",
			"grass_top",
			"grass_side",
			"grass_side",
			"grass_side",
			"grass_side"
		};
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Grass";
	}
	
	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return false;
	}

}
