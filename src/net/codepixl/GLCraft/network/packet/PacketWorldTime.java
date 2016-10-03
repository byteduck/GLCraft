package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.GameTime;

public class PacketWorldTime extends Packet{
	
	public long worldTime;
	
	public PacketWorldTime(long worldTime){
		this.worldTime = worldTime;
	}
}
