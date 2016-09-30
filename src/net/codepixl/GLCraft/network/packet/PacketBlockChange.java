package net.codepixl.GLCraft.network.packet;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.BreakSource;

public class PacketBlockChange extends Packet{

	public BreakSource source;
	public int x,y,z;
	public byte id,meta;
	
	public PacketBlockChange(Vector3f pos, byte id, byte meta, BreakSource source) {
		this.source = source;
		this.x = (int) pos.x;
		this.y = (int) pos.y;
		this.z = (int) pos.z;
		this.meta = meta;
		this.id = id;
	}

}
