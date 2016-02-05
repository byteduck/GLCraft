package net.codepixl.GLCraft.world.item;

import net.codepixl.GLCraft.util.Spritesheet;

public class ItemSeeds extends Item{
	@Override
	public String getName(){
		return "Seeds";
	}
	@Override
	public String getTextureName(){
		return "seed";
	}
	@Override
	public byte getId(){
		return 1;
	}
	
	@Override
	public float[] getTexCoords(){
		return new float[]{Spritesheet.atlas.uniformSize()*12,Spritesheet.atlas.uniformSize()};
	}
}
