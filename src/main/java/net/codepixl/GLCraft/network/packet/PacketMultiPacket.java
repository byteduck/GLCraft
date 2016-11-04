package net.codepixl.GLCraft.network.packet;

import java.util.Collection;

public class PacketMultiPacket extends Packet{
	public Packet[] packets;
	public PacketMultiPacket(Packet...packets){
		this.packets = packets;
	}
	public PacketMultiPacket(Collection<Packet> packets){
		this.packets = new Packet[packets.size()];
		packets.toArray(this.packets);
	}
}
