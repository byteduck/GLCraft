package net.codepixl.GLCraft.world.entity.mob.AI;

import java.util.List;

import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.DamageSource;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class AIHurtNearest extends AI{

	private Class hurt;
	private float largSiz;
	
	public AIHurtNearest(Mob m, Class hurt) {
		super(m);
		this.hurt = hurt;
		largSiz = m.getAABB().getSize().x;
		if(m.getAABB().getSize().y > largSiz)
			largSiz= m.getAABB().getSize().y;
		if(m.getAABB().getSize().z > largSiz)
			largSiz = m.getAABB().getSize().z;
	}
	
	@Override
	public void executeAI(){
		List<Entity> a = mob.worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(mob, hurt, largSiz+1f);
		if(a.size() > 0){
			Mob m = (Mob)a.get(0);
			m.hurt(2, 1.0f, DamageSource.MOB);
		}
	}
}
