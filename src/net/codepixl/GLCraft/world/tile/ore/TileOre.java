package net.codepixl.GLCraft.world.tile.ore;

import java.util.ArrayList;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.tool.Tool;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileOre extends Tile{
	public static ArrayList<TileOre> ores = new ArrayList<TileOre>();
	
	public TileOre(){
		super();
		ores.add(this);
	}
	
	/***A float from 0-1 that represents the chance this ore will spawn at a block.***/
	public float getRareness(){
		return 0.0f;
	}
	
	/***Minimum height this ore will spawn at.***/
	public int getMinHeight(){
		return 5;
	}
	
	/***Maximum height this ore will spawn at.***/
	public int getMaxHeight(){
		return 20;
	}
	
	/***Minimum tool that must be used for this ore's drops to drop.***/
	public Tool toolUsed(){
		return (Tool) Tool.WoodPick;
	}
	
	/***The Item/Tile this ore will drop.***/
	public ItemStack dropItem(){
		return new ItemStack(this);
	}
	
	/***Range of the amount of items this ore will drop.***/
	public int[] dropRange(){
		return new int[]{1,1};
	}
	
	@Override
	public ItemStack getDrop(int x, int y, int z, BreakSource b, WorldManager w){
		if(b.type == BreakSource.Type.PLAYER){
			if(b.player.getSelectedItemStack().isItem() && b.player.getSelectedItemStack().getItem() instanceof Tool){
				Tool t = (Tool) b.player.getSelectedItemStack().getItem();
				if(t.getStrength() >= toolUsed().getStrength())
					return new ItemStack(dropItem(),Constants.randInt(dropRange()[0],dropRange()[1]));
			}
		}
		return new ItemStack();
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}
}
