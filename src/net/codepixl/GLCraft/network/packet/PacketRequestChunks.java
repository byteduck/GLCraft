package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.Vector2i;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.Chunk;

public class PacketRequestChunks extends Packet{
	public Vector2i pos;
	public PacketRequestChunks(Vector2i pos){
		this.pos = pos.mult(16);
	}
}