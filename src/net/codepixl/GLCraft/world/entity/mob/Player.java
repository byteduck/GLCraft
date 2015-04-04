package net.codepixl.GLCraft.world.entity.mob;

import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.Raytracer;
import net.codepixl.GLCraft.world.entity.Camera;


public class Player extends Mob{

	private static float breakCooldown;
	
	public Player(Camera camera,int id) {
		super(camera, id, 1);
		breakCooldown = 0;
	}

	public void update(){
		move();
		setPos(getCamera().getPos());
		updateBreakCooldown();
	}
	
	public void render(){
		
	}
	
	public void dispose(){
		
	}
	
	public void move(){
		getCamera().updateMouse();
		getCamera().updateKeyboard(32, 0.25f);
	}
	
	public static float getBreakCooldown(){
		return breakCooldown;
	}
	
	private void updateBreakCooldown(){
		if(breakCooldown-Time.getDelta() > 0){
			breakCooldown-=Time.getDelta();
		}else{
			breakCooldown = 0;
		}
	}
	
	public static void setBreakCooldown(float cooldown){
		breakCooldown = cooldown;
	}
}
