package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.WorldManager;

public class PacketMessage extends Packet {

	private static final long serialVersionUID = -6535979937354825633L;
	
	public static final transient int BYTES = 0;
	public static final transient int STRING = 1;
	
	public byte[] message;
	public int type;
	
	public PacketMessage(byte[] message){
		this.message = message;
		this.type = BYTES;
	}
	
	public PacketMessage(String message){
		this.message = message.getBytes();
		this.type = STRING;
	}
	
	public void evaluate(WorldManager w){
		switch(type){
		case BYTES:
			break;
		case STRING:
			String msg = new String(message);
			if(msg.startsWith("SAVE|") && !w.isServer){
				if(msg.contains("start"))
					w.setSaving(true);
				else
					w.setSaving(false);
			}
			break;
		}
	}
	
}
