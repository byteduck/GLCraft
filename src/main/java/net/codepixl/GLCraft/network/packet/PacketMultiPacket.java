package net.codepixl.GLCraft.network.packet;

import java.util.Collection;

public class PacketMultiPacket extends Packet {

	private static final long serialVersionUID = 8589156695067422769L;
	
	public Packet[] packets;
	public PacketMultiPacket(Packet...packets){
		this.packets = packets;
	}
	public PacketMultiPacket(Collection<Packet> packets){
		this.packets = new Packet[packets.size()];
		packets.toArray(this.packets);
	}
}
