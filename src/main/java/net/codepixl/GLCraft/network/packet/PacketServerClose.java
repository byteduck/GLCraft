package net.codepixl.GLCraft.network.packet;

public class PacketServerClose extends Packet{

	private static final long serialVersionUID = 8758433681436182329L;
	
	public String message;

	public PacketServerClose(String message){
		this.message = message;
	}
}
