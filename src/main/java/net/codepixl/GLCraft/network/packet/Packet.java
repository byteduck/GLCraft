package net.codepixl.GLCraft.network.packet;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

import net.codepixl.GLCraft.network.Compressor;

public class Packet implements Serializable{
	public byte[] getBytes() throws IOException{
		return Compressor.compress(SerializationUtils.serialize(this));
	}
}
