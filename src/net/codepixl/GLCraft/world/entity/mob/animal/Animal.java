package net.codepixl.GLCraft.world.entity.mob.animal;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class Animal extends Mob{
	protected float Vx;
	protected float Vy;
	protected float Vz;
	protected float speed;
	protected Entity target;
	
	public Animal(Vector3f pos, WorldManager w) {
		super(pos, w);
		this.Vx = 0;
		this.Vy = 0;
		this.Vz = 0;
		this.speed = 0.1f;
		this.target = null;
	}
	public void walkForward(){
		this.move(this.Vx,this.Vy,-this.Vz);
	}
	public void setSpeed(float speed){
		this.speed = speed;
	}
	public void setTarget(Entity target){
		this.target = target;
	}
	public Vector3f getMovementVel(){
		return new Vector3f(this.Vx,this.Vy,this.Vz);
	}
	@Override
	public void handleAI(){
		Vector3f vec = MathUtils.RotToVel(this.rot, this.speed);
		this.Vx = vec.x;
		this.Vy = vec.y;
		this.Vz = vec.z;
		if(this.target != null){
			Vector3f tpos = target.getPos();
			float angle = (float) Math.toDegrees(Math.atan2(tpos.x - this.pos.x, tpos.z - this.pos.z));	    
		    this.rot.y = angle;
		}
	}
}
