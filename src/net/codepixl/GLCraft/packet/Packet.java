package net.codepixl.GLCraft.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Packet{
	
	byte[] data;
	byte identifier;
	
	public Packet(byte identifier){
		this.identifier = identifier;
	}
	
	public void setData(byte[] data) throws PacketTooLongException{
		if(data.length > 0xffff){
			throw new PacketTooLongException("Packet length ("+data.length+") is above "+0xffff+" byte limit.");
		}
		this.data = data;
	}
	
	public byte[] getPacket(){
		/**Packet structure is as follows: |Identifier (1b)|Length (2b)|Data (1b-0xfffb)|**/
		ByteBuffer buf = ByteBuffer.allocate(3+data.length);
		buf.put(identifier);
		buf.putShort((short)data.length);
		buf.put(data);
		buf.rewind();
		return buf.array();
	}
	
	public class PacketTooLongException extends IOException{
		
		public PacketTooLongException(String message) {
	        super(message);
	    }

	    public PacketTooLongException(String message, Throwable throwable) {
	        super(message, throwable);
	    }
	    
	}
	
}
