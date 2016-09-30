package net.codepixl.GLCraft.network.packet;

public class PacketPlayerLogin extends Packet{
	
	public byte protocolVersion = 0x0;
	public String name;
	
	public PacketPlayerLogin(String name){
		super((byte) 0x0);
		this.name = name;
	}
	
}
