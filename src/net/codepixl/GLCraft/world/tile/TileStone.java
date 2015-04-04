package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Spritesheet;

import com.nishu.utils.Color4f;

public class TileStone extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Stone";
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
	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return new float[]{Spritesheet.tiles.uniformSize()*4,0};
	}

}
