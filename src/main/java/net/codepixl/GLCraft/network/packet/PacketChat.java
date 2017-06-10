package net.codepixl.GLCraft.network.packet;

public class PacketChat extends Packet {

	private static final long serialVersionUID = -4890548061644199844L;
	
	public String msg;
	public PacketChat(String msg){
		this.msg = msg;
	}
}
