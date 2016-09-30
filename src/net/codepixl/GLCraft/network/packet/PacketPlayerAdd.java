package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class PacketPlayerAdd extends Packet{
	
	public int entityID;
	public String name;
	
	public PacketPlayerAdd(int id, String name){
		super((byte)0x04);
		this.entityID = id;
		this.name = name;
	}

}
