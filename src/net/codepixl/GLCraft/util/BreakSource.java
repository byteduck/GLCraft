package net.codepixl.GLCraft.util;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.Mob;

public class BreakSource {
	public static enum Type{
		PLAYER, MOB, ENVIRONMENT;
	};
	public Type type;
	public EntityPlayer player;
	public Mob mob;
	
	public BreakSource(EntityPlayer p){
		this.type = Type.PLAYER;
		this.player = p;
	}
	
	public BreakSource(Mob m){
		this.type = Type.MOB;
		this.mob = m;
	}
	
	public BreakSource(){
		this.type = Type.ENVIRONMENT;
	}
}
