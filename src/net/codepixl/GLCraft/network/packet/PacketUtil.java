package net.codepixl.GLCraft.network.packet;

import java.io.IOException;

import org.apache.commons.lang3.SerializationUtils;

import net.codepixl.GLCraft.network.Compressor;

public class PacketUtil {
	public static Packet getPacket(byte[] packet) throws ClassCastException, IOException{
		return (Packet) SerializationUtils.deserialize(Compressor.decompress(packet));
	}
}
