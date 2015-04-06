package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Spritesheet;

import com.nishu.utils.Color4f;

public class TileLog extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Log";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 8;
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
				Spritesheet.tiles.uniformSize()*12, Spritesheet.tiles.uniformSize(),
				Spritesheet.tiles.uniformSize()*12, Spritesheet.tiles.uniformSize(),
				Spritesheet.tiles.uniformSize()*11, Spritesheet.tiles.uniformSize(),
				Spritesheet.tiles.uniformSize()*11, 0,
				Spritesheet.tiles.uniformSize()*11, Spritesheet.tiles.uniformSize(),
				Spritesheet.tiles.uniformSize()*11, Spritesheet.tiles.uniformSize()
			};
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

}
