package net.codepixl.GLCraft.network.packet;

public class PacketWorldTime extends Packet{
	
	public long worldTime;
	
	public PacketWorldTime(long worldTime){
		this.worldTime = worldTime;
	}
}
