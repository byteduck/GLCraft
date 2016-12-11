package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.ItemStack;

public class TileLeaf extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Leaves";
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public float getHardness(){
		return 0.2f;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, BreakSource source, WorldManager worldManager){
		if(drop && Constants.rand.nextFloat() < 0.1){
			worldManager.spawnEntity(new EntityItem(new ItemStack(Tile.Sapling),(float)x+0.5f,(float)y+0.5f,(float)z+0.5f,worldManager));
		}
	}
	
	@Override
	public byte getTransparency(){
		return 2;
	}
}
