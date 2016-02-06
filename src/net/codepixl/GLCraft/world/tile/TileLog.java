package net.codepixl.GLCraft.world.tile;

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
	public float getHardness(){
		return 1.5f;
	}
	
	@Override
	public String getIconName(){
		return "log_top";
	}
	
	@Override
	public boolean hasMultipleTextures(){
		return true;
	}
	
	@Override
	public String[] getMultiTextureNames(){
		return new String[]{
			"log_top",
			"log_top",
			"log_side",
			"log_side",
			"log_side",
			"log_side"
		};
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
