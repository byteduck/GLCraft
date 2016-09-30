package net.codepixl.GLCraft.network.packet;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.BreakSource;

public class PacketBlockChange extends Packet{

	public BreakSource source;
	public int x,y,z;
	
	public PacketBlockChange(Vector3f pos, BreakSource source) {
		super((byte)0x03);
		this.source = source;
		this.x = (int) pos.x;
		this.y = (int) pos.y;
		this.z = (int) pos.z;
	}

}
