package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.Chunk;

public class PacketRequestChunk extends Packet{
	public Vector3i pos;
	public PacketRequestChunk(Chunk c){
		this.pos = new Vector3i(c.getPos());
	}
}
