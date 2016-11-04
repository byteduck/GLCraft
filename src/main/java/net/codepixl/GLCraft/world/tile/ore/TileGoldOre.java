package net.codepixl.GLCraft.world.tile.ore;

import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.tool.Tool;

public class TileGoldOre extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Gold Ore";
	}
	
	@Override
	public Tool toolUsed(){
		return (Tool) Tool.IronPick;
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 7;
	}
	
	@Override
	public float getHardness(){
		return 4.2f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(this);
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
		return 30;
	}
	
	@Override
	public int[] veinRange(){
		return new int[]{1,3};
	}

}
