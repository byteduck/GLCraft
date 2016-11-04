package net.codepixl.GLCraft.network.packet;

public class PacketChat extends Packet{
	public String msg;
	public PacketChat(String msg){
		this.msg = msg;
	}
}
