package net.codepixl.GLCraft.world.item;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class ItemBucketEmpty extends Item{
	@Override
	public String getName(){
		return "Bucket";
	}
	
	@Override
	public String getTextureName(){
		return "bucket";
	}
	
	@Override
	public byte getId(){
		return 5;
	}
	
	@Override
	public void onClick(EntityPlayer p){
		if(p.worldManager.getTileAtPos(p.getLookRayPos()) == Tile.Water.getId() && p.worldManager.getMetaAtPos(p.getLookRayPos()) < 2){
			p.setSelectedItemStack(new ItemStack(Item.BucketWater));
			p.worldManager.setTileAtPos(p.getLookRayPos(), Tile.Air.getId(), true);
		}
	}
	
}
