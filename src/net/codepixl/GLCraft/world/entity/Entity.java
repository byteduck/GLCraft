package net.codepixl.GLCraft.world.entity;

import net.codepixl.GLCraft.world.WorldManager;

import org.lwjgl.util.vector.Vector3f;

public class Entity {
	private Vector3f pos, rot;
	private int id;
	private Camera camera;
	public WorldManager worldManager;
	
	public Entity(float x, float y, float z, int id, WorldManager worldManager){
		this.pos = new Vector3f(x,y,z);
		this.rot = new Vector3f(0,0,0);
		this.id = id;
		this.worldManager = worldManager;
	}
	
	public Entity(float x, float y, float z, float rx, float ry, float rz, int id, WorldManager worldManager){
		this.pos = new Vector3f(x,y,z);
		this.rot = new Vector3f(rx,ry,rz);
		this.id = id;
		this.worldManager = worldManager;
	}
	
	public Entity(Vector3f pos, Vector3f rot, int id, WorldManager worldManager){
		this.pos = pos;
		this.rot = rot;
		this.id = id;
		this.worldManager = worldManager;
	}
	
	public Entity(Camera camera, int id){
		this.pos = new Vector3f(camera.getX(),camera.getY(),camera.getZ());
		this.rot = new Vector3f(camera.getPitch(),camera.getYaw(),camera.getRoll());
		this.id = id;
		this.setCamera(camera);
		this.worldManager = camera.worldManager;
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
	
	public void setRot(Vector3f rot){
		this.rot = rot;
	}
	
	public float getPitch(){
		return rot.y;
	}
	
	public float getYaw(){
		return rot.x;
	}
	
	public float getRoll(){
		return rot.z;
	}
	
	public void setYaw(float yaw){
		this.rot.x = yaw;
	}
	
	public void setRoll(float roll){
		this.rot.z = roll;
	}
	
	public void setPitch(float pitch){
		this.rot.y = pitch;
	}
	
	public float getX(){
		return pos.x;
	}
	
	public float getY(){
		return pos.y;
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

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
