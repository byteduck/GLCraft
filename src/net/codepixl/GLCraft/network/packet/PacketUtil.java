package net.codepixl.GLCraft.network.packet;

import org.apache.commons.lang3.SerializationUtils;

public class PacketUtil {
	public static Packet getPacket(byte[] packet){
		return (Packet) SerializationUtils.deserialize(packet);
	}
}
