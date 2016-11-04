package net.codepixl.GLCraft.network.packet;

public class PacketRemoveEntity extends Packet{
	public int entityID;
	public PacketRemoveEntity(int id){
		this.entityID = id;
	}
}
