package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.tool.Tool;

public class TileDiamondOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Diamond Ore";
	}
	
	@Override
	public Tool toolUsed(){
		return (Tool) Tool.IronPick;
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 27;
	}
	
	@Override
	public float getHardness(){
		return 5f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(Item.Diamond);
	}
	
	@Override
	public float getRareness(){
		return 0.001f;
	}
	
	@Override
	public int getMinHeight(){
		return 1;
	}
	
	@Override
	public int getMaxHeight(){
		return 15;
	}
	
	@Override
	public int[] veinRange(){
		return new int[]{1,4};
	}

}
