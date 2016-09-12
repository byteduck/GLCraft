package net.codepixl.GLCraft.world.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;

import net.codepixl.GLCraft.world.WorldManager;

public class NBTUtil {
	public static Entity readEntity(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		if(t.getString("type") != null){
			Class c = w.getEntityManager().getRegisteredEntity(t.getString("type"));
			Method m = c.getMethod("fromNBT", TagCompound.class, WorldManager.class);
			return (Entity) m.invoke(null, t, w);
			/*switch(t.getString("type")){
				case "EntityItem":
					return new EntityItem(pos,rot,vel,t,w);
				case "TileEntityChest":
					return TileEntityChest.fromNBT(t,w);
			}*/
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
