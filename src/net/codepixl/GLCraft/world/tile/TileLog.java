package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

public class TileLog extends Tile{
	
	private static String[] types = {"oak","darkoak","maple","pine","whiteoak"};
	
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
	public String getFolderSuffix(){
		return "wood";
	}
	
	@Override
	public String getIconName(byte i){
		String suffix;
		if(i < types.length){
			suffix = types[i];
		}else{
			suffix = types[0];
		}
		return "log_top_"+suffix;
	}
	
	@Override
	public boolean hasMultipleTextures(){
		return true;
	}
	
	@Override
	public boolean hasMetaTextures(){
		return true;
	}
	
	@Override
	public String[] getMultiTextureNames(byte i){
		String suffix;
		if(i < types.length){
			suffix = types[i];
		}else{
			suffix = types[0];
		}
		return new String[]{
			"log_top_"+suffix,
			"log_top_"+suffix,
			"log_side_"+suffix,
			"log_side_"+suffix,
			"log_side_"+suffix,
			"log_side_"+suffix
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
