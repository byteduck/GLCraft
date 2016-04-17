package net.codepixl.GLCraft.packet;

import org.apache.commons.lang3.SerializationUtils;

public class PacketUtil {
	public static Packet getPacket(byte[] packet){
		return (Packet) SerializationUtils.deserialize(packet);
	}
}
