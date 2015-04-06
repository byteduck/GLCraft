package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Spritesheet;

import com.nishu.utils.Color4f;

public class TileGrass extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}

	@Override
	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return new float[]{
			Spritesheet.tiles.uniformSize()*2, Spritesheet.tiles.uniformSize(),
			Spritesheet.tiles.uniformSize(), Spritesheet.tiles.uniformSize(),
			Spritesheet.tiles.uniformSize()*3, Spritesheet.tiles.uniformSize(),
			Spritesheet.tiles.uniformSize()*3, 0,
			Spritesheet.tiles.uniformSize()*3, Spritesheet.tiles.uniformSize(),
			Spritesheet.tiles.uniformSize()*3, Spritesheet.tiles.uniformSize()
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

}
