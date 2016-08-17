package net.codepixl.GLCraft.world.entity;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.evilco.mc.nbt.tag.TagList;
import com.evilco.mc.nbt.tag.TagLong;
import com.evilco.mc.nbt.tag.TagString;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.TileFire;
import net.codepixl.GLCraft.world.tile.TileLava;
import net.codepixl.GLCraft.world.tile.TileWater;

public class Entity implements GameObj{
	protected Vector3f pos, rot, vel;
	protected int id;
	public WorldManager worldManager;
	protected boolean dead = false;
	public long timeAlive = 0;
	public float onFire = 0;
	
	public Entity(float x, float y, float z, WorldManager worldManager){
		this.pos = new Vector3f(x,y,z);
		this.rot = new Vector3f(0,0,0);
		this.setVelocity(new Vector3f(0,0,0));
		this.id = worldManager.getEntityManager().getNewId();
		this.worldManager = worldManager;
	}
	
	public Entity(float x, float y, float z, float rx, float ry, float rz, WorldManager worldManager){
		this.pos = new Vector3f(x,y,z);
		this.rot = new Vector3f(rx,ry,rz);
		this.setVelocity(new Vector3f(0,0,0));
		this.id = worldManager.getEntityManager().getNewId();
		this.worldManager = worldManager;
	}
	
	public Entity(Vector3f pos, Vector3f rot, Vector3f vel, WorldManager worldManager){
		this.pos = pos;
		this.rot = rot;
		this.setVelocity(vel);
		this.id = worldManager.getEntityManager().getNewId();
		this.worldManager = worldManager;
	}
	
	public int getID(){
		return id;
	}
	
	public Vector3f getPos(){
		return pos;
	}
	
	public Vector3f getRot(){
		return rot;
	}
	
	public void setPos(Vector3f pos){
		this.pos = pos;
	}
	
	public void setPos(float x, float y, float z){
		this.pos.x = x;
		this.pos.y = y;
		this.pos.z = z;
	}
	
	public void setRot(Vector3f rot){
		this.rot = rot;
	}
	
	public float getX(){
		return pos.x;
	}
	
	public float getY(){
		return pos.y;
	}
	
	public void setRotX(float r){
		rot.x = r;
	}
	
	public void setRotY(float r){
		rot.y = r;
	}
	
	public void setRotZ(float r){
		rot.z = r;
	}
	
	public float getZ(){
		return pos.z;
	}
	
	public void setX(float x){
		pos.x = x;
	}
	
	public void setY(float y){
		pos.y = y;
	}
	
	public void setZ(float z){
		pos.z = z;
	}

	public Vector3f getVelocity() {
		return vel;
	}

	public void setVelocity(Vector3f vel) {
		this.vel = vel;
	}
	
	public final TagCompound mainWriteToNBT(){
		TagCompound t = new TagCompound("");
		TagList posList = new TagList("Pos");
		posList.addTag(new TagFloat("",this.pos.x));
		posList.addTag(new TagFloat("",this.pos.y));
		posList.addTag(new TagFloat("",this.pos.z));
		TagList rotList = new TagList("Rot");
		rotList.addTag(new TagFloat("",this.rot.x));
		rotList.addTag(new TagFloat("",this.rot.y));
		rotList.addTag(new TagFloat("",this.rot.z));
		TagList velList = new TagList("Vel");
		velList.addTag(new TagFloat("",this.vel.x));
		velList.addTag(new TagFloat("",this.vel.y));
		velList.addTag(new TagFloat("",this.vel.z));
		TagLong timeTag = new TagLong("TimeAlive",timeAlive);
		TagString typeTag = new TagString("type",this.getClass().getSimpleName());
		t.setTag(posList);
		t.setTag(rotList);
		t.setTag(velList);
		t.setTag(timeTag);
		t.setTag(typeTag);
		writeToNBT(t);
		return t;
	}
	
	public void writeToNBT(TagCompound t){
		
	}

	@Override
	public void update() {
		timeAlive+=(Time.getDelta()*1000f);
		this.rot = MathUtils.modulus(this.rot, 360f);
		if(this.onFire>0f){
			this.onFire-=Time.getDelta();
		}else{
			this.onFire = 0f;
		}
		Tile t = Tile.getTile((byte)worldManager.getTileAtPos(pos));
		t.onCollide((int)pos.x, (int)pos.y, (int)pos.z, worldManager, this);
		if(t instanceof TileFire){
			this.onFire = 7f;
		}else if(t instanceof TileLava){
			this.onFire = 7f;
		}else if(t instanceof TileWater){
			this.onFire = 0;
		}
		voidHurt();
	}

	@Override
	public void render() {
		
	}

	@Override
	public void dispose() {
		
	}

	public boolean isDead() {
		// TODO Auto-generated method stub
		return dead;
	}
	
	public void setDead(boolean isDead){
		this.dead = isDead;
		this.onFire = 0;
	}

	protected void voidHurt() {
		if(this.getY() < -5){
			this.setDead(true);
		}
	}

	public void setFire(float Time) {
		this.onFire = Time;
	}
}
