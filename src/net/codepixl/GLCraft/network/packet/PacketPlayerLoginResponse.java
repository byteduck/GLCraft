package net.codepixl.GLCraft.network.packet;

public class PacketPlayerLoginResponse extends Packet{
	
	public int entityID;
	
	public PacketPlayerLoginResponse(int id){
		super((byte) 0x1);
		this.entityID = id;
	}
	
}
