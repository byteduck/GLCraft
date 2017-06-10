package net.codepixl.GLCraft.network.packet;

public class PacketRemoveEntity extends Packet{

	private static final long serialVersionUID = 5031278905409633198L;
	
	public int entityID;
	public PacketRemoveEntity(int id){
		this.entityID = id;
	}
}
