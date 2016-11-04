package net.codepixl.GLCraft.world.entity.mob.AI;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class AIPathfindAway extends AI{
	
	Vector3f loc;
	
	public AIPathfindAway(Mob m) {
		super(m);
	}
	
	public void setLocation(Vector3f loc){
		this.loc = loc;
	}
	
	@Override
	public void executeAI(){
		if(loc != null){
			Vector3f vec = MathUtils.RotToVel(mob.getRot(), mob.getSpeed());
			mob.Vx = vec.x;
			mob.Vy = vec.y;
			mob.Vz = vec.z;
			float angle = (float) Math.toDegrees(Math.atan2(loc.x - mob.getX(), loc.z - mob.getZ()));	
			angle+=180;
		    mob.setRotY(angle);
		    mob.walkForward();
		    if(System.currentTimeMillis() - mob.lastCollideX <= 25 || System.currentTimeMillis() - mob.lastCollideZ <= 25 || mob.isInWater())
		    	mob.jump();
		}
	}
	
}
