package net.codepixl.GLCraft.world.entity;

import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntitySolid extends Entity{
	
	public AABB aabb;
	public boolean onGround = false;
	
	public EntitySolid(float x, float y, float z, WorldManager worldManager){
		super(x,y,z,worldManager);
		this.setAABB(this.getDefaultAABB());
	}
	
	public EntitySolid(float x, float y, float z, float rx, float ry, float rz, WorldManager worldManager){
		super(x,y,z,rx,ry,rz,worldManager);
		this.setAABB(this.getDefaultAABB());
	}
	
	public EntitySolid(Vector3f pos, Vector3f rot, Vector3f vel, WorldManager worldManager){
		super(pos,rot,vel,worldManager);
		this.setAABB(this.getDefaultAABB());
	}
	
	public final void move(float x, float y, float z){
		moveMain(x,0,0);
		moveMain(0,y,0);
		moveMain(0,0,z);
	}
	
	private final void moveMain(float x, float y, float z){
		Vector3f toPos = new Vector3f(this.getX()+x,this.getY()+y,this.getZ()+z);
		AABB toAABB = new AABB(this.aabb.getSize().x,this.aabb.getSize().y,this.aabb.getSize().z);
		toAABB.update(toPos);
		Iterator<AABB> i = worldManager.BlockAABBForEntity(this).iterator();
		boolean exit = false;
		boolean collided = false;
		while(i.hasNext() && !exit){
			AABB next = i.next();
			if(AABB.testAABB(toAABB, next)){
				collided = true;
				boolean found = false;
				float u = 1;
				while(!found){
					Vector3f to = MathUtils.PointAlongLine(this.getPos(), toPos, u);
					toAABB.update(to);
					if(!AABB.testAABB(toAABB, next)){
						toPos = to;
						found = true;
					}
					u = u/2f;
					if(u == 0){
						found = true;
					}
				}
				exit = true;
			}
		}
		onGround = testOnGround(new Vector3f(this.getX(),this.getY()-0.01f,this.getZ()));
		setPos(toPos);
	}
	
	private boolean testOnGround(Vector3f pos){
		AABB floorAABB = new AABB(this.aabb.getSize().x-0.05f,0,this.aabb.getSize().z-0.05f);
		floorAABB.update(pos);
		Iterator<AABB> i = worldManager.BlockAABBForEntity(this).iterator();
		while(i.hasNext()){
			AABB next = i.next();
			if(AABB.testAABB(floorAABB, next)){
				return true;
			}
		}
		return false;
	}
	
	private boolean testHitHead(Vector3f pos){
		AABB headAABB = new AABB(this.aabb.getSize().x-0.05f,0,this.aabb.getSize().z-0.05f);
		headAABB.update(pos);
		Iterator<AABB> i = worldManager.BlockAABBForEntity(this).iterator();
		while(i.hasNext()){
			AABB next = i.next();
			if(AABB.testAABB(headAABB, next)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void update(){
		super.update();
		aabb.update(new Vector3f(pos.x+aabb.r[0],pos.y,pos.z+aabb.r[2]));
		while(testHitHead(new Vector3f(this.getX(),this.getY()+this.aabb.getSize().y+0.01f,this.getZ()))){
			this.vel.y = 0f;
			this.pos.y-=0.01f;
		}
		this.move((this.getVelocity().x * (float)Time.getDelta() * 10),(this.getVelocity().y * (float)Time.getDelta() * 10),(this.getVelocity().z * (float)Time.getDelta() * 10));
		if(this.getVelocity().y > -3f && !onGround){
			this.getVelocity().y -= Time.getDelta()*3;
		}else if(!onGround){
			this.getVelocity().y = -3f;
		}else{
			this.getVelocity().y = 0.0f;
		}
		this.getVelocity().x = MathUtils.towardsZero(this.getVelocity().x,(float)Time.getDelta());
		this.getVelocity().z = MathUtils.towardsZero(this.getVelocity().z,(float)Time.getDelta());
		if(onGround){
			this.getVelocity().x = 0;
			this.getVelocity().y = 0;
			this.getVelocity().z = 0;
		}
	}
	
	public AABB getAABB(){
		return this.aabb;
	}
	
	public void setAABB(AABB aabb){
		this.aabb = aabb;
	}
	
	public AABB getDefaultAABB(){
		return new AABB(1,1,1);
	}

}
