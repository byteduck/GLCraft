package net.codepixl.GLCraft.world.item.tool;

public class ItemGoldAxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.AXE;
	}
	
	@Override
	public float getStrength(){
		return 6.3f;
	}
	
	@Override
	public String getName(){
		return "Gold Axe";
	}
	
	@Override
	public String getTextureName(){
		return "axe_gold";
	}
	
	@Override
	public byte getId(){
		return 17;
	}
}
