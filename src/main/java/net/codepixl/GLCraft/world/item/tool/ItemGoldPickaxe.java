package net.codepixl.GLCraft.world.item.tool;

public class ItemGoldPickaxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	@Override
	public float getStrength(){
		return 6.3f;
	}
	
	@Override
	public String getName(){
		return "Gold Pickaxe";
	}
	
	@Override
	public String getTextureName(){
		return "pick_gold";
	}
}
