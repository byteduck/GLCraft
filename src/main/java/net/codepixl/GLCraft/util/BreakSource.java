package net.codepixl.GLCraft.util;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.Mob;

import java.io.Serializable;

public class BreakSource implements Serializable{
	public static enum Type{
		PLAYER, MOB, ENVIRONMENT;
	};
	public Type type;
	public int entityID;
	public boolean sendPacket = true;
	
	public BreakSource(EntityPlayer p){
		this.type = Type.PLAYER;
		this.entityID = p.getID();
	}
	
	public BreakSource(Mob m){
		this.type = Type.MOB;
		this.entityID = m.getID();
	}
	
	public BreakSource(){
		this.type = Type.ENVIRONMENT;
		this.entityID = -1;
	}
	
	public Entity getEntity(WorldManager w){
		return w.getEntity(entityID);
	}
}
