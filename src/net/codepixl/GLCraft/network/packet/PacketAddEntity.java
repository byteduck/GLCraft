package net.codepixl.GLCraft.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagCompound;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.NBTUtil;

public class PacketAddEntity extends Packet{
	public byte[] entityData;
	public int entityID;
	public PacketAddEntity(Entity e){
		TagCompound t = e.mainWriteToNBT();
		ByteArrayOutputStream ops = new ByteArrayOutputStream();
		NbtOutputStream o = new NbtOutputStream(ops);
		try {
			o.write(t);
			o.close();
			this.entityData = ops.toByteArray();
			ops.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.entityID = e.getID();
	}
	
	public Entity getEntity(WorldManager w) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		ByteArrayInputStream bis = new ByteArrayInputStream(entityData);
		NbtInputStream in = new NbtInputStream(bis);
		Entity e = NBTUtil.readEntity((TagCompound)in.readTag(), w);
		e.setId(entityID);
		return e;
	}
}
