package net.codepixl.GLCraft.network.packet;

public class PacketKick extends Packet{
	public String message;
	public PacketKick(String message){
		this.message = message;
	}
}
