package net.codepixl.GLCraft.world.entity.particle;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Color4f;
import com.nishu.utils.Time;

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
	
	public Particle(Vector3f pos, Vector3f vel,WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.texCoords = Tile.Lava.getTexCoords();
		this.size = 0.2f;
		this.lifetime = 1.0f;
		this.setVel(vel);
		RegisterParicle();
		
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
		GL11.glTranslatef(getX(), getY()+this.size, getZ());
		GL11.glRotatef(-this.rot.y, 0.0f, 1.0f, 0f);
		GL11.glRotatef(-this.rot.z, 1.0f, 0f, 0f);
		GL11.glRotatef(180, 0f, 0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createSprite(0,0,0, this.getColor(), this.texCoords, this.size);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	@Override
	public void update(){
		super.update();
		this.lifetime -= Time.getDelta();
		if(this.lifetime < 0){
			this.setDead(true);
		}
		List<Entity> e = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, EntityPlayer.class, 200f);
		if(e.size() != 0){
			EntityPlayer player = (EntityPlayer) e.get(0);
			Vector3f trot = player.getRot();
			this.setRot(trot);
		}
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.2f, 0.2f, 0.2f);
	}
	
	public void RegisterParicle(){
		
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w){
		return null;
	}

	public void setLifeTime(float l) {
		this.lifetime = l;
	}

	
}
