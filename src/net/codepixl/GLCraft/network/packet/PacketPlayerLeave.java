package net.codepixl.GLCraft.network.packet;

public class PacketPlayerLeave extends Packet{
	
	public int entityID;
	
	public PacketPlayerLeave(int id) {
		entityID = id;
	}
	
	public PacketPlayerLeave(){}
}