package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class PacketPlayerPos extends Packet{
	public float[] pos;
	public float[] rot;
	public float[] vel;
	public int entityID;
	public PacketPlayerPos(EntityPlayer p){
		this.pos = new float[]{p.getPos().x,p.getPos().y,p.getPos().z};
		this.rot = new float[]{p.getRot().x,p.getRot().y,p.getRot().z};
		this.vel = new float[]{p.getVelocity().x,p.getVelocity().y,p.getVelocity().z};
		this.entityID = p.getID();
	}
	public PacketPlayerPos(PacketPlayerPos p){
		this.pos = p.pos;
		this.rot = p.rot;
		this.vel = p.vel;
		this.entityID = p.entityID;
	}
}
