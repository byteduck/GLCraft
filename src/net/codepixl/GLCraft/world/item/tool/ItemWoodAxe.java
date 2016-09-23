package net.codepixl.GLCraft.world.item.tool;

public class ItemWoodAxe extends Tool{
	
	@Override
	public ToolType getToolType(){
		return ToolType.AXE;
	}
	
	@Override
	public float getStrength(){
		return 2.5f;
	}
	
	@Override
	public String getName(){
		return "Wooden Axe";
	}
	
	@Override
	public String getTextureName(){
		return "axe_wood";
	}
	
	@Override
	public byte getId(){
		return 14;
	}
	
}