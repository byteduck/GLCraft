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
	public byte getId(){
		return 6;
	}
	
	@Override
	public void onClick(EntityPlayer p){
		p.placeTile(Tile.Water);
		p.setSelectedItemStack(new ItemStack(Item.Bucket));
	}
}
