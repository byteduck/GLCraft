package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.network.Server;
import net.codepixl.GLCraft.util.Vector2i;

public class PacketRequestChunks extends Packet{
	public Vector2i pos;
	public Server.ServerClient client;

	public PacketRequestChunks(Vector2i pos){
		this.pos = pos;
	}
}