package net.codepixl.GLCraft.network.packet;

import org.lwjgl.util.vector.Vector3f;

public class PacketPlayerLoginResponse extends Packet{
	
	public int entityID;
	public float x,y,z;
	public String message;
	public boolean accept = true;
	
	public PacketPlayerLoginResponse(int id, Vector3f pos){
		this.entityID = id;
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}
	
	public PacketPlayerLoginResponse(String message){
		this.message = message;
		this.accept = false;
	}
	
}
