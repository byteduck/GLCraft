package net.codepixl.GLCraft.network.packet;

public class PacketPing extends Packet {

	private static final long serialVersionUID = -3370864776248340542L;
	
	public boolean isResponse;
	public PacketPing(boolean isResponse){this.isResponse = isResponse;}
}
