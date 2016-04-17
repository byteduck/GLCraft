package net.codepixl.GLCraft.world.item.tool;

import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.tile.material.Material;

public class Tool extends Item{
	
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	public float getStrength(){
		return 1;
	}

	public float getStrengthForMaterial(Material material) {
		if(getToolType() == ToolType.PICKAXE && material == Material.STONE){
			return getStrength();
		}else if(getToolType() == ToolType.SHOVEL && material == Material.DIRT){
			return getStrength();
		}else if(getToolType() == ToolType.AXE && material == Material.WOOD){
			return getStrength();
		}
		return 0.8f;
	}
	
}
