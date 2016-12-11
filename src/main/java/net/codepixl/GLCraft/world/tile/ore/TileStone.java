package net.codepixl.GLCraft.world.tile.ore;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileStone extends TileOre{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Stone";
	}
	
	@Override
	public float getHardness(){
		return 4f;
	}
	
	@Override
	public ItemStack dropItem(){
		return new ItemStack(Tile.Cobblestone);
	}

}
