package net.codepixl.GLCraft.world.entity.mob.hostile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.AI.AIFollowNearest;
import net.codepixl.GLCraft.world.entity.mob.AI.AIWander;
import net.codepixl.GLCraft.world.entity.mob.animal.EntityTestAnimal;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityTestHostile extends Hostile{

	private Vector3f lastpos;

	public EntityTestHostile(Vector3f pos, WorldManager w) {
		super(pos, w);
		this.lastpos = new Vector3f(0,0,0);
		this.addAI(new AIFollowNearest(this, EntityTestAnimal.class));
		this.addAI(new AIWander(this, 1, 5, 3, 4));
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		GL11.glRotatef(this.rot.y, 0f, 2.0f, 0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCube(-0.5f,0,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		//Shape.createCube(-0.5f,1,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		//Shape.createCube(-1.5f,1,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		//Shape.createCube(0.5f,1,-0.5f, Color4f.WHITE, Tile.Fire.getTexCoords(), 1f);
		GL11.glEnd();
		GL11.glPopMatrix();
		
	}
	
	@Override
	public void handleAI(){
		super.handleAI();
		
		if(Constants.randInt(0, 0) == 0){
			Particle particle = new Particle(new Vector3f(this.getX()+Constants.randFloat(-1f, 1f),this.getY()+.5f+Constants.randFloat(-1f, 1f),this.getZ()+Constants.randFloat(-1f, 1f)), new Vector3f(0,0.5f,0), worldManager);
			particle.setTexCoords(Tile.Lava.getIconCoords());
			particle.setSize(.2f);
			worldManager.entityManager.add(particle);
		}
	}

}
