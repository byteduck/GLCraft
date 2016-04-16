package net.codepixl.GLCraft.world.entity.particle;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class Particle extends EntitySolid{
	
	private float texCoords[];
	private float size;
	private float lifetime;
	private Vector3f Vel;
	public Particle(Vector3f pos, Vector3f vel,WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.texCoords = Tile.Lava.getTexCoords();
		this.size = 0.5f;
		this.lifetime = 5.0f;
		this.Vel = vel;
		
	}
	public void setTexCoords(float[] tc){
		this.texCoords = tc;
	}
	public void setSize(float size){
		this.size = size;
	}
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY()+1, getZ());
		
		GL11.glRotatef(this.rot.y, 0f, 1.0f, 0f);
		GL11.glRotatef(-this.rot.x, 1.0f, 0f, 0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createPlane(0,0,0, Color4f.WHITE, this.texCoords, this.size);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}
	@Override
	public void update(){
		super.update();
		this.lifetime -= 0.05f;
		if(this.lifetime < 0){
			this.setDead(true);
		}
		this.setVelocity(new Vector3f(0,0,0));
		this.pos.x += this.Vel.x/10;
		this.pos.y += this.Vel.y/10;
		this.pos.z += this.Vel.z/10;
		this.vel.x /= 2;
		this.vel.y /= 2; 
		this.vel.z /= 2; 
		List<Entity> e = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, EntityPlayer.class, 200f);
		if(e.size() != 0){
			EntityPlayer player = (EntityPlayer) e.get(0);
			Vector3f tpos = player.getPos();
			this.rot.y = (float) Math.toDegrees(Math.atan2(tpos.x - this.pos.x, tpos.z - this.pos.z));
			this.rot.x = (float) Math.toDegrees(Math.atan2(tpos.y - this.pos.y, tpos.z - this.pos.z));
			if(tpos.z<pos.z){
				this.rot.x = -this.rot.x;
				
			}
			//this.rot.z = (float) Math.toDegrees(Math.atan2(tpos.x - this.pos.x, tpos.z - this.pos.z));
		
		}
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.1f,0.1f,0.1f);
	}
}
