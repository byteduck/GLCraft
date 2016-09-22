package net.codepixl.GLCraft.world.item.tool;

public class ItemDiamondPickaxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	@Override
	public float getStrength(){
		return 6.5f;
	}
	
	@Override
	public String getName(){
		return "Diamond Pickaxe";
	}
	
	@Override
	public String getTextureName(){
		return "pick_diamond";
	}
	
	@Override
	public byte getId(){
		return 11;
	}
}
