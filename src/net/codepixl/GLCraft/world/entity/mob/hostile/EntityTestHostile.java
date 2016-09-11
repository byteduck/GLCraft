package net.codepixl.GLCraft.world.entity.mob.hostile;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityTestHostile extends Hostile{

	private Vector3f lastpos;

	public EntityTestHostile(Vector3f pos, WorldManager w) {
		super(pos, w);
		this.lastpos = new Vector3f(0,0,0);
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
		
		List<Entity> e = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, EntityPlayer.class, 20f);
		if(e.size() != 0){
			this.setTarget(e.get(0));
			EntityPlayer player = (EntityPlayer) e.get(0);
			Vector3f ppos = player.getPos();
			//this.walkForward();
			if(pos.x == lastpos.x || pos.z == lastpos.z){
				//this.jump();
			}
			lastpos = pos;
			float distance = MathUtils.distance(ppos,pos);
			if(distance <= 2){
				player.hurt(3f, 1f);
			}
			
		}
	}

}
