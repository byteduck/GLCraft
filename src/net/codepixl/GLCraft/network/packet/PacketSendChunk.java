package net.codepixl.GLCraft.network.packet;

import java.io.IOException;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class PacketSendChunk extends Packet{
	
	public static transient int TYPE_CHUNK = 0;
	public static transient int TYPE_PREPARE = 1;
	
	public byte[][][] tiles;
	public byte[][][] meta;
	public transient EntityPlayerMP sendTo;
	public boolean failed = false;
	public Vector3i pos;
	public int numChunks;
	public int type;
	
	public PacketSendChunk(Chunk c, EntityPlayerMP sendTo){
		this.tiles = c.getTiles();
		this.meta = c.getMeta();
		this.sendTo = sendTo;
		this.pos = new Vector3i(c.getPos());
		this.type = TYPE_CHUNK;
	}
	
	public PacketSendChunk(){
		failed = true;
	}
	
	public PacketSendChunk(int num){
		this.type = TYPE_PREPARE;
		this.numChunks = num;
	}
}
