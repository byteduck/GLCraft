package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Spritesheet;

public class TileWater extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Water";
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}

	@Override
	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return new float[]{Spritesheet.tiles.uniformSize()*5,0};
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isTranslucent(){
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}

}
