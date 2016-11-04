package net.codepixl.GLCraft.network.packet;

public class PacketLANBroadcast extends Packet{
	public String msg;
	public int port;
	public PacketLANBroadcast(String msg, int port){
		this.msg = msg;
		this.port = port;
	}
}
