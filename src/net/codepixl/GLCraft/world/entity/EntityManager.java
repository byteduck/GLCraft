package net.codepixl.GLCraft.world.entity;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagList;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.PlayerMP;
import net.codepixl.GLCraft.world.entity.mob.animal.EntityTestAnimal;
import net.codepixl.GLCraft.world.entity.mob.hostile.EntityTestHostile;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;

public class EntityManager implements GameObj{
	
	private static HashMap<String, Class> registeredEntities = new HashMap<String, Class>();
	private ArrayList<Entity> entities;
	private ArrayList<Entity> toAdd;
	private ArrayList<Entity> toRemove;
	private ArrayList<TileEntity> tileEntities;
	boolean shouldRemoveAll = false;
	private EntityPlayer player;
	private PlayerMP otherPlayer;
	private WorldManager w;
	public boolean iterating = false;
	
	private int mobRenderID;
	private int currentId;
	
	public EntityManager(WorldManager w){
		this.w = w;
		init();
	}
	
	public void init(){
		currentId = 0;
		entities = new ArrayList<Entity>();
		toAdd = new ArrayList<Entity>();
		toRemove = new ArrayList<Entity>();
		tileEntities = new ArrayList<TileEntity>();
		registerEntities();
		initGL();
	}
	
	private static void registerEntities(){
		registerEntity("EntityPlayer", EntityPlayer.class);
		registerEntity("EntityItem", EntityItem.class);
		registerEntity("EntityTestAnimal", EntityTestAnimal.class);
		registerEntity("EntityTestHostile", EntityTestHostile.class);
		registerEntity("Particle", Particle.class);
		registerEntity("TileEntityChest", TileEntityChest.class);
		registerEntity("EntityFallingBlock", EntityFallingBlock.class);
		registerEntity("TileEntityFurnace", TileEntityFurnace.class);
	}
	
	public static void registerEntity(String name, Class entity){
		if(Entity.class.isAssignableFrom(entity)){
			registeredEntities.put(name, entity);
		}else{
			System.err.println("ERROR REGISTERING ENTITY "+entity.getName()+": NOT AN ENTITY");
		}
	}
	
	public void initPlayer(){
		player = new EntityPlayer(new Vector3f(16,100,16),w);
		add(player);
	}
	
	public void initPlayerMP(){
		otherPlayer = new PlayerMP(new Vector3f(0,0,0),w);
		add(otherPlayer);
	}
	
	private void initGL(){
		mobRenderID = glGenLists(1);
	}
	
	public void save(String name) throws IOException{
		FileOutputStream outputStream;
		File f = new File(Constants.GLCRAFTDIR+"saves/"+name+"/");
		f.mkdirs();
		outputStream = new FileOutputStream(new File(f,"entities.nbt"));
		NbtOutputStream nbtOutputStream = new NbtOutputStream(outputStream);
		TagCompound root = new TagCompound("");
		TagList list = new TagList("Entities");
		Iterator<Entity> i = this.entities.iterator();
		while(i.hasNext()){
			Entity e = i.next();
			if(!(e instanceof EntityPlayer)){
				TagCompound t = e.mainWriteToNBT();
				list.addTag(t);
			}
			
		}
		root.setTag(list);
		nbtOutputStream.write(root);
		nbtOutputStream.close();
	}
	
	public void removeAll(){
		//EXCEPT FOR THE PLAYER, OF COURSE.
		shouldRemoveAll = true;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		Iterator<Entity> itt = toRemove.iterator();
		while(itt.hasNext()){
			Entity e = itt.next();
			entities.remove(e);
			itt.remove();
		}
		if(shouldRemoveAll){
			Iterator<Entity> it = entities.iterator();
			iterating = true;
		    while (it.hasNext()) {
		        Entity e = it.next();
		        if(e instanceof EntityPlayer){}else{it.remove();}
		    }
		    iterating = false;
		    shouldRemoveAll = false;
		}
		Iterator<Entity> i = toAdd.iterator();
		while(i.hasNext()){
			Entity e = i.next();
			entities.add(e);
			if(e instanceof TileEntity){
				tileEntities.add((TileEntity) e);
			}
			i.remove();
		}
	    Iterator<Entity> it = entities.iterator();
	    iterating = true;
	    while (it.hasNext()) {
	        Entity e = it.next();
	        if(e.isDead() && !(e instanceof EntityPlayer)){
	        	it.remove();
	        }
	        e.update();
	    }
	    iterating = false;
	    DebugTimer.endTimer("ai_time");
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		Iterator<Entity> it = entities.iterator();
		iterating = true;
	    while (it.hasNext()) {
	    	Entity e = it.next();
	        e.render();
	        /**if(e instanceof EntitySolid && !(e instanceof EntityPlayer)){
	        	((EntitySolid) e).aabb.render();
	        }**/
	    }
		glCallList(mobRenderID);
		iterating = false;
	}
	
	public List<Entity> getEntitiesInRadiusOfEntity(Entity e, float rad){
		ArrayList<Entity> list = new ArrayList<Entity>();
		Iterator<Entity> it = entities.iterator();
		iterating = true;
	    while (it.hasNext()) {
	    	Entity ent = it.next();
	        if(MathUtils.distance(ent.getPos(), e.getPos()) < rad && ent != e && !ent.isDead() && !e.isDead()){
	        	list.add(ent);
	        }
	    }
	    final Entity e2 = e;
	    Collections.sort(list, new Comparator<Entity>(){
    	   public int compare(Entity o1, Entity o2){
    	      return (int) (MathUtils.distance(o1.getPos(), e2.getPos()) - MathUtils.distance(o2.getPos(), e2.getPos()));
    	   }
    	});
	    iterating = false;
	    return list;
	}
	
	public List<Entity> getEntitiesInRadiusOfEntityOfType(Entity e, Class type, float rad){
		ArrayList<Entity> list = new ArrayList<Entity>();
		Iterator<Entity> it = entities.iterator();
		iterating = true;
	    while (it.hasNext()) {
	    	Entity ent = it.next();
	        if(type.isInstance(ent) && MathUtils.distance(ent.getPos(), e.getPos()) < rad && ent != e && !ent.isDead() && !e.isDead()){
	        	list.add(ent);
	        }
	    }
	    final Entity e2 = e;
	    Collections.sort(list, new Comparator<Entity>(){
    	   public int compare(Entity o1, Entity o2){
    	      return (int) (MathUtils.distance(o1.getPos(), e2.getPos()) - MathUtils.distance(o2.getPos(), e2.getPos()));
    	   }
    	});
	    iterating = false;
	    return list;
	}

	@Override
	public void dispose() {
		player.dispose();
		glDeleteLists(mobRenderID, 1);
	}
	
	public EntityPlayer getPlayer(){
		return player;
	}

	public PlayerMP getPlayerMP() {
		// TODO Auto-generated method stub
		return this.otherPlayer;
	}

	public void add(Entity e) {
		if(registeredEntities.containsValue(e.getClass()))
			this.toAdd.add(e);
		else
			System.err.println("Tried to add unregistered entity "+e.getClass().getName());
	}
	
	public void remove(Entity e){
			this.toRemove.add(e);
	}
	
	public int getNewId(){
		currentId++;
		return currentId;
	}

	public int totalEntities() {
		return entities.size();
	}
	
	public TileEntity getTileEntityForPos(int x, int y, int z){
		Iterator<TileEntity> i = tileEntities.iterator();
		TileEntity ret = null;
		while(i.hasNext()){
			TileEntity e = i.next();
			if(e.getBlockpos().equals(new Vector3i(x,y,z))){
				ret = e;
			}
		}
		return ret;
	}

	public Class getRegisteredEntity(String name) {
		return registeredEntities.get(name);
	}

}
