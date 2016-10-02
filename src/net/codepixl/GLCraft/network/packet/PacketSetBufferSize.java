package net.codepixl.GLCraft.network.packet;

public class PacketSetBufferSize extends Packet{
	public int bufferSize;
	public PacketSetBufferSize(int bufferSize){
		this.bufferSize = bufferSize;
	}
}
