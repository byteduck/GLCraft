package net.codepixl.GLCraft.world.entity.mob.AI;

import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.Mob;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class AIRunAway extends AIPathfindAway{

	private Class<? extends Entity> follow;
	
	public AIRunAway(Mob m, Class<? extends Entity> type) {
		super(m);
		this.follow = type;
	}
	
	public void executeAI(){
		super.executeAI();
		List<Entity> e = mob.worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(mob, follow, 20f);
		if(e.size() > 0){
			Vector3f ppos = e.get(0).getPos();
			this.loc = ppos;
			mob.setAiBusy(true);
		}else{
			this.loc = null;
			mob.setAiBusy(false);
		}
	}

}
