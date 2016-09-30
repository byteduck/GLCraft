package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class PacketSendChunk extends Packet{
	
	public byte[][][] tiles;
	public byte[][][] meta;
	public transient EntityPlayerMP sendTo;
	public boolean failed = false;
	
	public PacketSendChunk(Chunk c, EntityPlayerMP sendTo){
		this.tiles = c.getTiles();
		this.meta = c.getMeta();
		this.sendTo = sendTo;
	}
	
	public PacketSendChunk(){
		failed = true;
	}
}
