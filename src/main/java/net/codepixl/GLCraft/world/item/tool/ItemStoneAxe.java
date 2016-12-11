package net.codepixl.GLCraft.world.item.tool;

public class ItemStoneAxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.AXE;
	}
	
	@Override
	public float getStrength(){
		return 3f;
	}
	
	@Override
	public String getName(){
		return "Stone Axe";
	}
	
	@Override
	public String getTextureName(){
		return "axe_stone";
	}
}
