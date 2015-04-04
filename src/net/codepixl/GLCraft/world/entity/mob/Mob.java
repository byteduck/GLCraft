package net.codepixl.GLCraft.world.entity.mob;

import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.world.entity.Camera;
import net.codepixl.GLCraft.world.entity.Entity;

import org.lwjgl.util.vector.Vector3f;

public class Mob extends Entity implements GameObj{

	private int mobID;
	private boolean isDead;
	
	public Mob(Camera camera, int id, int mobID){
		super(camera,id);
		this.mobID = mobID;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		getCamera().updateKeyboard(32, 2);
		getCamera().updateMouse();
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public void move(){
		
	}
	
	public int getMobID(){
		return mobID;
	}
	
	public void setMobID(int id){
		this.mobID = id;
	}
	
	public boolean isDead(){
		return isDead;
	}
	
	public void setDead(boolean isDead){
		this.isDead = isDead;
	}

}
