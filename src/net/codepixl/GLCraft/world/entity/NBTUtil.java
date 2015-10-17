package net.codepixl.GLCraft.world.entity;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;

import net.codepixl.GLCraft.world.WorldManager;

public class NBTUtil {
	public static Entity readEntity(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		Vector3f pos = vecFromList("Pos",t);
		Vector3f rot = vecFromList("Rot",t);
		Vector3f vel = vecFromList("Vel",t);
		if(t.getString("type") != null){
			switch(t.getString("type")){
				case "EntityItem":
					return new EntityItem(pos,rot,vel,t,w);
			}
		}
		return null;
	}
	
	public static Vector3f vecFromList(String name, TagCompound t) throws UnexpectedTagTypeException, TagNotFoundException{
		List<TagFloat> f = t.getList(name, TagFloat.class);
		if(f.size() > 2){
			return new Vector3f(f.get(0).getValue(),f.get(1).getValue(),f.get(2).getValue());
		}
		return new Vector3f();
	}
}
