package net.codepixl.GLCraft.world.entity.particle;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class Particle extends Entity{
	
	private float texCoords[];
	private float size;
	private float lifetime;
	private Vector3f Vel;
	private boolean hasg;
	private float[] texCoordFrag;
	public Particle(Vector3f pos, Vector3f vel,WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.texCoords = Tile.Lava.getTexCoords();
		this.size = 0.2f;
		this.lifetime = 1.0f;
		this.Vel = vel;
		this.hasg = false;
		RegisterParicle();
		this.texCoordFrag = Tile.Lava.getTexCoords();
		
	}
	public void setTexCoords(float[] tc){
		this.texCoords = tc;
	}
	public void setSize(float size){
		this.size = size;
	}
	public void setLifeTime(float l){
		this.lifetime = l;
	}
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY(), getZ());
		GL11.glRotatef(-this.rot.y, 0.0f, 1.0f, 0f);
		GL11.glRotatef(-this.rot.z, 1.0f, 0f, 0f);
		GL11.glRotatef(180, 0f, 0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createSprite(0,0,0, Color4f.WHITE, this.texCoords, this.size);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	public void hasGravity(boolean k){
		this.hasg = k;
	}
	@Override
	public void update(){
		super.update();
		this.lifetime -= 0.05f;
		if(this.lifetime < 0){
			this.setDead(true);
		}
		if(this.hasg){
			this.Vel.y -= .01f;
		}
		this.pos.x += this.Vel.x/10;
		this.pos.y += this.Vel.y/10;
		this.pos.z += this.Vel.z/10;
		this.getVel().x /= 2;
		this.getVel().y /= 2; 
		this.getVel().z /= 2;
		List<Entity> e = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, EntityPlayer.class, 200f);
		if(e.size() != 0){
			EntityPlayer player = (EntityPlayer) e.get(0);
			Vector3f trot = player.getRot();
			this.setRot(trot);
		}
	}
	
	public void RegisterParicle(){
		
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w){
		return null;
	}

	
}
