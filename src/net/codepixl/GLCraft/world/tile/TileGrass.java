package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Spritesheet;

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
	public String getTextureName(){
		return "grass_side";
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
	public float[] getIconCoords(){
		return new float[]{Spritesheet.tiles.uniformSize()*3, 0};
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
