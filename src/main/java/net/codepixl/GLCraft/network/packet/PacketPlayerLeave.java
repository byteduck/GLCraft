package net.codepixl.GLCraft.network.packet;

public class PacketPlayerLeave extends Packet {

	private static final long serialVersionUID = -9104982516475953935L;
	
	public int entityID;
	
	public PacketPlayerLeave(int id) {
		entityID = id;
	}
	
	public PacketPlayerLeave(){}
}