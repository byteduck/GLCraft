package net.codepixl.GLCraft.packet;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

public class Packet implements Serializable{
	public byte[] getBytes(){
		return SerializationUtils.serialize(this);
	}
}
