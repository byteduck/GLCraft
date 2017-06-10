package net.codepixl.GLCraft.network.packet;

public class PacketWorldTime extends Packet{

	private static final long serialVersionUID = 8067029509219497756L;
	
	public long worldTime;
	
	public PacketWorldTime(long worldTime){
		this.worldTime = worldTime;
	}
}
