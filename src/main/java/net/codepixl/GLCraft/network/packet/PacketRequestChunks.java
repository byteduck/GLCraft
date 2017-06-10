package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.network.Server;
import net.codepixl.GLCraft.util.Vector2i;

import java.util.List;

public class PacketRequestChunks extends Packet{

	private static final long serialVersionUID = -6085796777757386251L;
	
	public Vector2i[] pos;
	public transient Server.ServerClient client;
	public transient Vector2i indivPos;

	public PacketRequestChunks(List<Vector2i> pos){
		this.pos = pos.toArray(new Vector2i[pos.size()]);
	}

	public PacketRequestChunks(Server.ServerClient client, Vector2i indivPos){
		this.client = client;
		this.indivPos = indivPos;
	}
}