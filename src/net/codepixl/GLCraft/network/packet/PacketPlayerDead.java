package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class PacketPlayerDead extends Packet{
	
	public int entityID;
	
	public PacketPlayerDead(EntityPlayer p){
		this.entityID = p.getID();
	}
}
