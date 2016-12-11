package net.codepixl.GLCraft.world.item.tool;

public class ItemWoodPickaxe extends Tool{
	
	@Override
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	@Override
	public float getStrength(){
		return 2.5f;
	}
	
	@Override
	public String getName(){
		return "Wooden Pickaxe";
	}
	
	@Override
	public String getTextureName(){
		return "pick_wood";
	}
	
}
