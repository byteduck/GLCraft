package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.EnumFacing;

public class PacketOnPlace extends Packet {

	private static final long serialVersionUID = 2372955776682421276L;
	
	public EnumFacing facing;
	public int x,y,z;
	public byte tile, meta;
	
	public PacketOnPlace(int x, int y, int z, EnumFacing facing, byte tile, byte meta){
		this.x = x;
		this.y = y;
		this.z = z;
		this.facing = facing;
		this.meta = meta;
		this.tile = tile;
	}
}
