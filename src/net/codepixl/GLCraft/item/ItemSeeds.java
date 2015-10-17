package net.codepixl.GLCraft.item;

import net.codepixl.GLCraft.util.Spritesheet;

public class ItemSeeds extends Item{
	@Override
	public String getName(){
		return "Seeds";
	}
	
	@Override
	public byte getId(){
		return 1;
	}
	
	@Override
	public float[] getTexCoords(){
		return new float[]{Spritesheet.tiles.uniformSize()*12,Spritesheet.tiles.uniformSize()};
	}
}
