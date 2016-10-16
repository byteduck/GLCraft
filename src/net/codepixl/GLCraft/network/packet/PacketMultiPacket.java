package net.codepixl.GLCraft.network.packet;

public class PacketMultiPacket extends Packet{
	public Packet[] packets;
	public PacketMultiPacket(Packet...packets){
		this.packets = packets;
	}
}
