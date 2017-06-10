package net.codepixl.GLCraft.network.packet;

public class PacketKick extends Packet {

	private static final long serialVersionUID = 4095385726043418837L;
	
	public String message;
	public PacketKick(String message){
		this.message = message;
	}
}
