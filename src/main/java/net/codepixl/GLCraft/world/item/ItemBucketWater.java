package net.codepixl.GLCraft.world.item;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class ItemBucketWater extends Item{
	@Override
	public String getName(){
		return "Bucket of Water";
	}
	
	@Override
	public String getTextureName(){
		return "bucket_water";
	}
	
	@Override
	public void onClick(EntityPlayer p){
		p.placeTile(Tile.Water, (byte)1);
		p.setSelectedItemStack(new ItemStack(Item.Bucket));
	}
}
