package net.codepixl.GLCraft.network.packet;

public class PacketPing extends Packet{
	public boolean isResponse;
	public PacketPing(boolean isResponse){this.isResponse = isResponse;}
}
