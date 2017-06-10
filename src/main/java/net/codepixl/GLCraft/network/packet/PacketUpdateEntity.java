package net.codepixl.GLCraft.network.packet;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagCompound;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.NBTUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class PacketUpdateEntity extends Packet {

	private static final long serialVersionUID = -4110929033411560063L;

	public enum Type{
		UPDATENBT, POSITION;
	}
	
	public byte[] entityData;
	public float x,y,z,xr,yr,zr,xv,yv,zv;
	public int entityID;
	public Type type;
	
	public PacketUpdateEntity(Entity e, Type type){
		if(type == Type.UPDATENBT){
			TagCompound t = e.mainWriteToNBT();
			ByteArrayOutputStream ops = new ByteArrayOutputStream();
			NbtOutputStream o = new NbtOutputStream(ops);
			try {
				o.write(t);
				o.close();
				this.entityData = ops.toByteArray();
				ops.close();
			} catch (IOException e1) {
				try {
					o.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				e1.printStackTrace();
			}
		}else if(type == Type.POSITION){
			this.x = e.getX();
			this.y = e.getY();
			this.z = e.getZ();
			this.xr = e.getRot().x;
			this.yr = e.getRot().y;
			this.zr = e.getRot().z;
			this.xv = e.getVel().x;
			this.yv = e.getVel().y;
			this.zv = e.getVel().z;
		}
		this.entityID = e.getID();
		this.type = type;
	}
	
	public Entity getEntity(WorldManager w) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		ByteArrayInputStream bis = new ByteArrayInputStream(entityData);
		NbtInputStream in = new NbtInputStream(bis);
		TagCompound t = (TagCompound) in.readTag();
		in.close();
		return NBTUtil.readEntity(t, w);
	}
}
