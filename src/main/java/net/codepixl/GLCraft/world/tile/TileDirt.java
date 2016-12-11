package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileDirt extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Dirt";
	}
	
	@Override
	public Material getMaterial(){
		return Material.DIRT;
	}

	@Override
	public Color4f getColor() {
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 0.5f;
	}

}
