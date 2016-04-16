package net.codepixl.GLCraft.world.item.tool;

public class ItemStonePickaxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	@Override
	public float getStrength(){
		return 4.5f;
	}
	
	@Override
	public String getName(){
		return "Stone Pickaxe";
	}
	
	@Override
	public String getTextureName(){
		return "pick_stone";
	}
	
	@Override
	public byte getId(){
		return 2;
	}
}
