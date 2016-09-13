package net.codepixl.GLCraft.world.tile;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileParticleProjector extends Tile{
	private Particle particle;
	public TileParticleProjector(){
		super();
		this.particle = null;
	}
	@Override
	public String getName() {
		return "Particle Projector";
	}
	
	@Override
	public Material getMaterial(){
		return Material.STONE;
	}
	
	@Override
	public byte getId() {
		return 19;
	}
	
	@Override
	public int tickRate(){
		return 1;
	}
	
	@Override
	public void tick(int x, int y, int z, WorldManager worldManager){
			this.particle = new Particle(new Vector3f(x+.5f,y+.5f,z+.5f), new Vector3f(Constants.randFloat(-0.1f, 0.1f),Constants.randFloat(0, 1),Constants.randFloat(-0.1f, 0.1f)), worldManager);
			particle.setTexCoords(Tile.Tnt.getTexCoords());
			worldManager.entityManager.add(this.particle);
	}
	@Override
	public boolean needsConstantTick(){
		return true;
	}
	@Override
	public String getTextureName(){
		return "particle_projector";
	}
	@Override
	public boolean isTransparent(){
		return true;
	}
}
