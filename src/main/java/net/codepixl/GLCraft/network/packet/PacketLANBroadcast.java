package net.codepixl.GLCraft.network.packet;

public class PacketLANBroadcast extends Packet {

	private static final long serialVersionUID = -5718331482366287376L;
	
	public String msg;
	public int port;
	public PacketLANBroadcast(String msg, int port){
		this.msg = msg;
		this.port = port;
	}
}
