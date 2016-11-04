package net.codepixl.GLCraft.network.packet;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.BreakSource;

public class PacketBlockChange extends Packet{

	public BreakSource source;
	public int x,y,z;
	public byte id,meta;
	public boolean justMeta = false;
	
	public PacketBlockChange(int x, int y, int z, byte id, byte meta, BreakSource source) {
		this.source = source;
		this.source.sendPacket = false;
		this.x = x;
		this.y = y;
		this.z = z;
		this.meta = meta;
		this.id = id;
	}

	public PacketBlockChange(int x, int y, int z, byte meta) {
		this.justMeta = true;
		this.x = x;
		this.y = y;
		this.z = z;
		this.meta = meta;
	}

}
