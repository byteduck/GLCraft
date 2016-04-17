package net.codepixl.GLCraft.packet;

import java.nio.ByteBuffer;

import net.codepixl.GLCraft.world.Chunk;

public class PacketChunk extends Packet{
	public PacketChunk(Chunk c) throws PacketTooLongException {
		super((byte)1);
		ByteBuffer buf = ByteBuffer.allocate(16*16*16*2+6);
		buf.putShort((short)c.getPos().x);
		buf.putShort((short)c.getPos().y);
		buf.putShort((short)c.getPos().z);
		byte[][][] tiles = c.getTiles();
		for(int x = 0; x < tiles.length; x++){
			for(int y = 0; y < tiles.length; y++){
				for(int z = 0; z < tiles.length; z++){
					buf.put(tiles[x][y][z]);
				}
			}
		}
		byte[][][] meta = c.getMeta();
		for(int x = 0; x < meta.length; x++){
			for(int y = 0; y < meta.length; y++){
				for(int z = 0; z < meta.length; z++){
					buf.put(meta[x][y][z]);
				}
			}
		}
		buf.rewind();
		this.setData(buf.array());
	}
}
