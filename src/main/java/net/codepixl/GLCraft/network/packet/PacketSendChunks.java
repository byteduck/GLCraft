package net.codepixl.GLCraft.network.packet;

import java.util.Collection;
import java.util.Iterator;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.Chunk;

public class PacketSendChunks extends Packet{
	
	public byte[][][][] tiles;
	public byte[][][][] meta;
	public boolean failed = false;
	public Vector3i[] pos;
	public int numChunks;
	
	public PacketSendChunks(Collection<Chunk> cs){
		this.tiles = new byte[cs.size()][][][];
		this.meta = new byte[cs.size()][][][];
		pos = new Vector3i[cs.size()];
		Iterator<Chunk> i = cs.iterator();
		int ind = 0;
		while(i.hasNext()){
			Chunk c = i.next();
			if(c != null){
				tiles[ind] = c.getTiles();
				meta[ind] = c.getMeta();
				this.pos[ind] = new Vector3i(c.getPos());
			}else{
				this.failed = true;
			}
			ind++;
		}
	}
	
	public PacketSendChunks(){
		this.failed = true;
	}
	
	public PacketSendChunks(int numChunks){
		this.numChunks = numChunks;
	}
}
