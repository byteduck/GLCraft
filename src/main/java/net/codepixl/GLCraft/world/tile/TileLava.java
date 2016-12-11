package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.DamageSource;
import net.codepixl.GLCraft.world.entity.mob.Mob;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileLava extends Tile{
	int tick = 0;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Lava";
	}
	
	@Override
	public Material getMaterial(){
		return Material.LIQUID;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isTranslucent(){
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void tick(int x, int y, int z, WorldManager w){
		tick++;
		if(tick >= 10){
			int xx = Constants.randInt(-1,1);
			int yy = Constants.randInt(-1,0);
			int zz = Constants.randInt(-1,1);
			if(w.getTileAtPos(x+xx, y+yy, z+zz) == Tile.Air.getId()){
				w.setTileAtPos(x, y, z, Tile.Air.getId(), true);
				w.setTileAtPos(x+xx, y+yy, z+zz, Tile.Lava.getId(), true);
			}
		tick = 0;
		}
	}
	
	@Override
	public boolean needsConstantTick(){
		return true;
	}
	
	@Override
	public void onCollide(int x, int y, int z, WorldManager worldManager, Entity e) {
		if(e instanceof Mob){
			((Mob) e).hurt(2f,0.4f,DamageSource.FIRE);
			((Mob) e).setFire(10);
		}
	}
}
