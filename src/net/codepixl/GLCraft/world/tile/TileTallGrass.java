package net.codepixl.GLCraft.world.tile;

import java.util.Random;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;

public class TileTallGrass extends Tile {

	@Override
	public String getName() {
		return "Tall Grass";
	}
	
	@Override
	public String getTextureName(){
		return "tall_grass";
	}

	@Override
	public byte getId() {
		return 10;
	}

	@Override
	public Color4f getColor() {
		return Color4f.WHITE;
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, WorldManager worldManager){
		if(drop && new Random().nextFloat() < 0.1){
			worldManager.spawnEntity(new EntityItem(new ItemStack(Item.Seeds), (float)x+0.5f, (float)y+0.5f,(float)z+0.5f, worldManager));
		}
	}
	
	@Override
	public void blockUpdate(int x, int y, int z, WorldManager worldManager){
		if(Tile.getTile((byte)worldManager.getTileAtPos(x,y-1,z)).canPassThrough()){
			worldManager.setTileAtPos(x,y,z, Tile.Air.getId(), true);
		}
	}
	
	@Override
	public float getHardness(){
		return 0f;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CROSS;
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}

	@Override
	public boolean canPassThrough() {
		return true;
	}
	
	@Override
	public boolean canBeDestroyedByLiquid(){
		return true;
	}

}
