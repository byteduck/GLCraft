package net.codepixl.GLCraft.world.entity;

import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntitySolid extends Entity{
	
	public AABB aabb;
	public boolean onGround = false;
	public Vector3f collideVec = new Vector3f();
	
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
	
	public void move(float x, float y, float z){
		if(moveMain(x,0,0))
			this.getVel().x = 0;
		if(moveMain(0,y,0)){
			this.getVel().y = 0;
		}
		if(moveMain(0,0,z))
			this.getVel().z = 0;
	}
	
	protected final boolean moveMain(float x, float y, float z){
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
					MathUtils.PointAlongLine(this.getPos(), toPos, collideVec, u);
					toAABB.update(collideVec);
					if(!AABB.testAABB(toAABB, next)){
						toPos = collideVec;
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
		setPos(toPos);
		return collided;
	}
	
	private boolean testOnGround(Vector3f pos){
		AABB floorAABB = new AABB(this.aabb.getSize().x,0,this.aabb.getSize().z);
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
	
	public boolean isInWater(){
		Iterator<AABB> i = worldManager.BlockAABBForEntity(this, Tile.Water).iterator();
		getAABB().update(pos);
		while(i.hasNext()){
			AABB next = i.next();
			if(AABB.testAABB(getAABB(), next)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isSubmerged(){
		Iterator<AABB> i = worldManager.BlockAABBForEntity(this, Tile.Water).iterator();
		AABB aabb = new AABB(getAABB().getSize().x, getAABB().getSize().y/2f, getAABB().getSize().z);
		aabb.update(new Vector3f(getX(), getY()+aabb.getSize().y/2, getZ()));
		while(i.hasNext()){
			AABB next = i.next();
			if(AABB.testAABB(aabb, next)){
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
		aabb.update(new Vector3f(pos.x+(float)aabb.r[0],pos.y,pos.z+(float)aabb.r[2]));
		/*while(testHitHead(new Vector3f(this.getX(),this.getY()+this.aabb.getSize().y+0.01f,this.getZ()))){
			this.getVel().y = 0f;
		}*/
		onGround = this.testOnGround(new Vector3f(getX(),getY()-0.005f,getZ()));
		if(worldManager.isServer && this instanceof EntityPlayer)
			return;
		this.move((this.getVelocity().x * (float)Time.getDelta() * 10),(this.getVelocity().y * (float)Time.getDelta() * 10),(this.getVelocity().z * (float)Time.getDelta() * 10));
		if(this.isInWater()){
			this.getVelocity().y = MathUtils.towardsValue(this.getVelocity().y, (float)Time.getDelta()*3, -0.2f);
		}else if(this.getVelocity().y > -3f && !onGround){
			this.getVelocity().y -= Time.getDelta()*3;
		}else if(!onGround){
			this.getVelocity().y = -3f;
		}else{
			this.getVelocity().y = 0.0f;
		}
		this.getVelocity().x = MathUtils.towardsZero(this.getVelocity().x,(float)Time.getDelta());
		this.getVelocity().z = MathUtils.towardsZero(this.getVelocity().z,(float)Time.getDelta());
		if(onGround){
			this.getVelocity().x = MathUtils.towardsZero(this.getVelocity().x,(float)Time.getDelta()*4);
			this.getVelocity().z = MathUtils.towardsZero(this.getVelocity().z,(float)Time.getDelta()*4);
			this.getVelocity().y = 0;
		}
	}
	
	@Override
	public void clientUpdate(){
		super.clientUpdate();
		//All of this is to move the entity based on it's last position & velocity from the server, so movement looks smooth.
		if(!(this instanceof EntityPlayer) || this instanceof EntityPlayerMP){
			onGround = this.testOnGround(new Vector3f(getX(),getY()-0.005f,getZ()));
			aabb.update(new Vector3f(pos.x+(float)aabb.r[0],pos.y,pos.z+(float)aabb.r[2]));
			/*while(testHitHead(new Vector3f(this.getX(),this.getY()+this.aabb.getSize().y+0.01f,this.getZ()))){
				this.getVel().y = 0f;
			}*/
			this.move((this.getVelocity().x * (float)Time.getDelta() * 10),(this.getVelocity().y * (float)Time.getDelta() * 10),(this.getVelocity().z * (float)Time.getDelta() * 10));
			if(this.isInWater()){
				this.getVelocity().y = MathUtils.towardsValue(this.getVelocity().y, (float)Time.getDelta()*3, -0.2f);
			}else if(this.getVelocity().y > -3f && !onGround){
				this.getVelocity().y -= Time.getDelta()*3;
			}else if(!onGround){
				this.getVelocity().y = -3f;
			}else{
				this.getVelocity().y = 0.0f;
			}
			this.getVelocity().x = MathUtils.towardsZero(this.getVelocity().x,(float)Time.getDelta());
			this.getVelocity().z = MathUtils.towardsZero(this.getVelocity().z,(float)Time.getDelta());
			if(onGround){
				this.getVelocity().x = MathUtils.towardsZero(this.getVelocity().x,(float)Time.getDelta()*4);
				this.getVelocity().z = MathUtils.towardsZero(this.getVelocity().z,(float)Time.getDelta()*4);
				this.getVelocity().y = 0;
			}
		}
	}
	
	public AABB getAABB(){
		return this.aabb;
	}
	
	public void setAABB(AABB aabb){
		this.aabb = aabb;
	}
	
	public AABB getDefaultAABB(){
		return new AABB(0.98f,0.98f,0.98f);
	}

}
