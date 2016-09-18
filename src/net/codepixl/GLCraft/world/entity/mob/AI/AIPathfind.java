package net.codepixl.GLCraft.world.entity.mob.AI;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.entity.mob.Mob;
import net.codepixl.GLCraft.world.entity.mob.AI.pathfinding.Pathfinder;
import net.codepixl.GLCraft.world.tile.Tile;

public class AIPathfind extends AI{
	
	private Vector3f loc;
	private Pathfinder p;
	private int currentNode= 0;
	
	public AIPathfind(Mob m) {
		super(m);
	}
	
	public boolean setLocation(Vector3f loc){
		this.loc = loc;
		if(loc != null){
			this.p = new Pathfinder(new Vector3i(mob.getPos()), new Vector3i(loc), mob.worldManager);
			this.currentNode = 1;
			return this.p.pathfind(1000);
		}else{
			this.p = null;
			return false;
		}
	}
	
	public Vector3f getLocation(){
		return loc;
	}
	
	@Override
	public void executeAI(){
		if(loc != null && this.p != null && this.p.path.size() >= currentNode){
			Vector3f loc = this.p.path.get(currentNode).toVector3f();
			Vector3f vec = MathUtils.RotToVel(mob.getRot(), mob.getSpeed());
			mob.Vx = vec.x;
			mob.Vy = vec.y;
			mob.Vz = vec.z;
			float angle = (float) Math.toDegrees(Math.atan2(loc.x - mob.getX(), loc.z - mob.getZ()));	    
		    mob.setRotY(angle);
		    mob.walkForward();
		    if(System.currentTimeMillis() - mob.lastCollideX <= 25 || System.currentTimeMillis() - mob.lastCollideZ <= 25 || mob.isInWater())
		    	mob.jump();
		    if(MathUtils.distance(mob.getPos(), loc) <= 2){
		    	this.currentNode++;
		    	if(this.currentNode >= this.p.path.size())
		    		this.loc = null;
		    }
		}else if(loc != null && this.p != null){
			if(this.currentNode >= this.p.path.size())
	    		this.loc = null;
		}
		
		
	}

}
