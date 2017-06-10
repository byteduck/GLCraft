package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.BreakSource;

public class PacketBlockChange extends Packet{

	private static final long serialVersionUID = 3281692475818864594L;
	
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
