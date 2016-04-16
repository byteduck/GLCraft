package net.codepixl.GLCraft.world.tile;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.particle.Particle;

public class TileParticleProjector extends Tile{
	private int timer = 0;
	@Override
	public String getName() {
		return "Particle Projector";
	}
	@Override
	public byte getId() {
		return 19;
	}
	@Override
	public void tick(int x, int y, int z, WorldManager worldManager){
		timer++;
		if(timer>= 1){
			Particle particle = new Particle(new Vector3f(x+.5f,y,z+.5f), new Vector3f(Constants.randFloat(-0.1f, 0.1f),Constants.randFloat(0, 1),Constants.randFloat(-0.1f, 0.1f)), worldManager);
			//particle.setTexCoords(Tile.Tnt.getTexCoords());
			worldManager.entityManager.add(particle);
			timer = 0;
		}
	}
	@Override
	public boolean needsConstantTick(){
		return true;
	}
	@Override
	public String getTextureName(){
		return "particle_projector";
	}
}
