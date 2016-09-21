package net.codepixl.GLCraft.world.item.tool;

public class ItemIronPickaxe extends Tool{
	@Override
	public ToolType getToolType(){
		return ToolType.PICKAXE;
	}
	
	@Override
	public float getStrength(){
		return 5.5f;
	}
	
	@Override
	public String getName(){
		return "Iron Pickaxe";
	}
	
	@Override
	public String getTextureName(){
		return "pick_iron";
	}
	
	@Override
	public byte getId(){
		return 7;
	}
}
