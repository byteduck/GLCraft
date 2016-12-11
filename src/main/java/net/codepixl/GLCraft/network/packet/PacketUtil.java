package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.network.Compressor;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;

public class PacketUtil {
	public static Packet getPacket(byte[] packet) throws ClassCastException, IOException{
		return (Packet) SerializationUtils.deserialize(Compressor.decompress(packet));
	}
}
