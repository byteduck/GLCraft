package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.Color4f;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileWood extends Tile{
	
	private static String[] types = {"oak","darkoak","maple","pine","whiteoak"};
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Wood";
	}
	
	@Override
	public Material getMaterial(){
		return Material.WOOD;
	}
	
	@Override
	public boolean hasMetaTextures(){
		return true;
	}
	
	@Override
	public String getFolderSuffix(){
		return "wood/";
	}
	
	@Override
	public String getTextureName(byte i){
		String suffix;
		if(i < types.length){
			suffix = types[i];
		}else{
			suffix = types[0];
		}
		return "wood_"+suffix;
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 16;
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
