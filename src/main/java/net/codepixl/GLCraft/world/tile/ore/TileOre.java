package net.codepixl.GLCraft.world.tile.ore;

import java.util.ArrayList;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
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
	
	/***Range of the possible sizes of a vein of this ore.***/
	public int[] veinRange(){
		return new int[]{2,5};
	}
	
	public void spawnVein(int x, int y, int z, WorldManager w){
		int oresLeft = Constants.randInt(veinRange()[0], veinRange()[1]);
		Vector3i currentPos = new Vector3i(x,y,z);
		Vector3i newPos = new Vector3i(x,y,z);
		w.setTileAtPos(newPos.x, newPos.y, newPos.z, getId(), true);
		while(oresLeft > 0){
			while(currentPos.equals(newPos)){
				newPos = new Vector3i(currentPos);
				switch(Constants.randInt(0, 6)){
					case 1:
						newPos.x++;
						break;
					case 2:
						newPos.y++;
						break;
					case 3:
						newPos.z++;
						break;
					case 4:
						newPos.x--;
						break;
					case 5:
						newPos.y--;
						break;
					case 6:
						newPos.z--;
						break;
				}
			}
			oresLeft--;
			if(w.getTileAtPos(newPos.x, newPos.y, newPos.z) == Tile.Stone.getId())
				w.setTileAtPos(newPos.x, newPos.y, newPos.z, getId(), true);
			currentPos = newPos;
		}
	}
	
	@Override
	public ItemStack getDrop(int x, int y, int z, BreakSource b, WorldManager w){
		if(b.type == BreakSource.Type.PLAYER){
			Entity e = b.getEntity(w);
			EntityPlayer p;
			if(e != null && e instanceof EntityPlayer)
				p = (EntityPlayer)e;
			else
				return new ItemStack();
			if(p.getSelectedItemStack().isItem() && p.getSelectedItemStack().getItem() instanceof Tool){
				Tool t = (Tool) p.getSelectedItemStack().getItem();
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
