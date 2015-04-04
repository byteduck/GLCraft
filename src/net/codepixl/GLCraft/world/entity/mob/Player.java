package net.codepixl.GLCraft.world.entity.mob;

import net.codepixl.GLCraft.util.Raytracer;
import net.codepixl.GLCraft.world.entity.Camera;


public class Player extends Mob{

	public Player(Camera camera,int id) {
		super(camera, id, 1);
	}

	public void update(){
		move();
		setPos(getCamera().getPos());
	}
	
	public void render(){
		
	}
	
	public void dispose(){
		
	}
	
	public void move(){
		getCamera().updateMouse();
		getCamera().updateKeyboard(32, 0.25f);
	}
}
