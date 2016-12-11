package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.Vector2i;

public class PacketRequestChunks extends Packet{
	public Vector2i pos;
	public PacketRequestChunks(Vector2i pos){
		this.pos = pos.mult(16);
	}
}