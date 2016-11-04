package net.codepixl.GLCraft.network.packet;

public class PacketServerClose extends Packet{
	
	public String message;

	public PacketServerClose(String message){
		this.message = message;
	}
}
