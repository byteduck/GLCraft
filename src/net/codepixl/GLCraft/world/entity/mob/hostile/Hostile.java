package net.codepixl.GLCraft.world.entity.mob.hostile;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class Hostile extends Mob{
	
	private float Vx;
	private float Vy;
	private float Vz;
	private float speed;
	private Entity target;
	private int rotspeed;
	private int tmpr;
	public Hostile(Vector3f pos, WorldManager w) {
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
	public Vector3f getVel(){
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
		    /*if(this.rot.y > angle+2 ){
		    	this.rot.y-= 2;
		    }else if(this.rot.y < angle-2){
		    	this.rot.y+= 2;
		    }*/
		}
	}
	
}
