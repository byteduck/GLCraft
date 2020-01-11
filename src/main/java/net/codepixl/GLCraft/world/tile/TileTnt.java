package net.codepixl.GLCraft.world.tile;

import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityTNT;
import org.lwjgl.util.vector.Vector3f;

public class TileTnt extends Tile {
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, BreakSource source, WorldManager w){
		if(source.type == BreakSource.Type.PLAYER){
			EntityTNT tnt = new EntityTNT(new Vector3f(x + 0.5f,y,z + 0.5f), new Vector3f(), new Vector3f(),w);
			w.spawnEntity(tnt);
		}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Tnt";
	}
}
