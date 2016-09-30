package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class PacketPlayerPos extends Packet{
	float[] pos;
	float[] rot;
	float[] vel;
	int entityID;
	public PacketPlayerPos(EntityPlayer p){
		super((byte) 0x2);
		this.pos = new float[]{p.getPos().x,p.getPos().y,p.getPos().z};
		this.rot = new float[]{p.getRot().x,p.getRot().y,p.getRot().z};
		this.vel = new float[]{p.getVelocity().x,p.getVelocity().y,p.getVelocity().z};
		this.entityID = p.getID();
	}
}
