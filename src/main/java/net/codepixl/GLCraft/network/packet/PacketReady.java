package net.codepixl.GLCraft.network.packet;

/***
 * Sent to the Server when the client has finished receiving chunk packets and should start being updated.
 */

public class PacketReady extends Packet {

	private static final long serialVersionUID = 4242813800587732328L;

	public PacketReady(){}
}
