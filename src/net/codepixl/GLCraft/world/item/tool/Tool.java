package net.codepixl.GLCraft.world.item.tool;

import net.codepixl.GLCraft.world.item.Item;

public class Tool extends Item{
	
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	public float getStrength(){
		return 1;
	}
	
}
