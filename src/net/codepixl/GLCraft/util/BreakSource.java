package net.codepixl.GLCraft.util;

import java.io.Serializable;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class BreakSource implements Serializable{
	public static enum Type{
		PLAYER, MOB, ENVIRONMENT;
	};
	public Type type;
	public int entityID;
	public transient EntityPlayer player;
	public transient Mob mob;
	public boolean sendPacket = true;
	
	public BreakSource(EntityPlayer p){
		this.type = Type.PLAYER;
		this.player = p;
		this.entityID = p.getID();
	}
	
	public BreakSource(Mob m){
		this.type = Type.MOB;
		this.mob = m;
		this.entityID = m.getID();
	}
	
	public BreakSource(){
		this.type = Type.ENVIRONMENT;
		this.entityID = -1;
	}
}
