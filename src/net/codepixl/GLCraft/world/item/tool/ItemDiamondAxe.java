package net.codepixl.GLCraft.world.item.tool;

public class ItemDiamondAxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.AXE;
	}
	
	@Override
	public float getStrength(){
		return 6.5f;
	}
	
	@Override
	public String getName(){
		return "Diamond Axe";
	}
	
	@Override
	public String getTextureName(){
		return "axe_diamond";
	}
	
	@Override
	public byte getId(){
		return 18;
	}
}
