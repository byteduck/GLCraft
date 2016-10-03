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
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagList;

import net.codepixl.GLCraft.network.packet.PacketAddEntity;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.entity.mob.animal.EntityTestAnimal;
import net.codepixl.GLCraft.world.entity.mob.hostile.EntityTestHostile;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityChest;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;

public class EntityManager implements GameObj{
	
	private static HashMap<String, Class> registeredEntities = new HashMap<String, Class>();
	private HashMap<Integer, Entity> entities;
	private ArrayList<Entity> toAdd;
	private ArrayList<Entity> toRemove;
	boolean shouldRemoveAll = false;
	private EntityPlayer player;
	private WorldManager w;
	public boolean iterating = false;
	public boolean isServer;
	
	private int mobRenderID;
	
	public EntityManager(WorldManager w, boolean isServer){
		this.w = w;
		this.isServer = isServer;
		init();
	}
	
	public void init(){
		entities = new HashMap<Integer,Entity>();
		toAdd = new ArrayList<Entity>();
		toRemove = new ArrayList<Entity>();
		registerEntities();
		if(!isServer)
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
		registerEntity("EntityPlayerMP", EntityPlayerMP.class);
	}
	
	public static void registerEntity(String name, Class entity){
		if(Entity.class.isAssignableFrom(entity)){
			registeredEntities.put(name, entity);
		}else{
			System.err.println("ERROR REGISTERING ENTITY "+entity.getName()+": NOT AN ENTITY");
		}
	}
	
	public void initPlayer(){
		player = new EntityPlayer(new Vector3f(16,200,16),w);
		toAdd.add(player);
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
		Iterator<Entry<Integer,Entity>> i = this.entities.entrySet().iterator();
		while(i.hasNext()){
			final Entity e = i.next().getValue();
			if(!(e instanceof EntityPlayer)){
				try{
					TagCompound t = e.mainWriteToNBT();
					list.addTag(t);
				}catch(final Exception ex){
					Thread t = new Thread(new Runnable(){
				        public void run(){
				            JOptionPane.showInternalMessageDialog(null, "There was an error saving an "+e.getClass()+".\nYour world may not load as expected.\nPlease report this bug and upload the log saved at\n"+Constants.GLCRAFTDIR+"GLCraft.log", "Error saving Entity", JOptionPane.ERROR_MESSAGE);
				            ex.printStackTrace();
				        }
				    });
					t.start();
				}
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
			Iterator<Entry<Integer,Entity>> it = this.entities.entrySet().iterator();
			iterating = true;
		    while (it.hasNext()) {
		        Entity e = it.next().getValue();
		        if(e instanceof EntityPlayer){}else{it.remove();}
		    }
		    iterating = false;
		    shouldRemoveAll = false;
		}
		Iterator<Entity> i = toAdd.iterator();
		while(i.hasNext()){
			Entity e = i.next();
			entities.put(e.getID(),e);
			if(!(e instanceof EntityPlayer))
				w.sendPacket(new PacketAddEntity(e));
			i.remove();
		}
		Iterator<Entry<Integer,Entity>> it = this.entities.entrySet().iterator();
	    iterating = true;
	    while (it.hasNext()) {
	        Entity e = it.next().getValue();
	        if(e.isDead() && !(e instanceof EntityPlayer)){
	        	it.remove();
	        }
	        e.update();
	    }
	    iterating = false;
	    DebugTimer.endTimer("ai_time");
	}
	
	public Entity getEntity(int id){
		return this.entities.get(id);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		Iterator<Entry<Integer,Entity>> it = this.entities.entrySet().iterator();
		iterating = true;
	    while (it.hasNext()) {
	    	Entity e = it.next().getValue();
	    	w.shader.use();
	        e.render();
	        w.shader.release();
	        /**if(e instanceof EntitySolid && !(e instanceof EntityPlayer)){
	        	((EntitySolid) e).aabb.render();
	        }**/
	    }
		glCallList(mobRenderID);
		iterating = false;
	}
	
	public List<Entity> getEntitiesInRadiusOfEntity(Entity e, float rad){
		ArrayList<Entity> list = new ArrayList<Entity>();
		Iterator<Entry<Integer,Entity>> it = this.entities.entrySet().iterator();
		iterating = true;
	    while (it.hasNext()) {
	    	Entity ent = it.next().getValue();
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
		Iterator<Entry<Integer,Entity>> it = this.entities.entrySet().iterator();
		iterating = true;
	    while (it.hasNext()) {
	    	Entity ent = it.next().getValue();
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

	public void add(Entity e) {
		if(registeredEntities.containsValue(e.getClass()))
			this.toAdd.add(e);
		else{
			System.err.println("Tried to add unregistered entity "+e.getClass().getName());
			new Throwable().printStackTrace();
		}
	}
	
	public void add(Entity e, int id) {
		if(registeredEntities.containsValue(e.getClass())){
			e.setId(id);
			this.toAdd.add(e);
		}else{
			System.err.println("Tried to add unregistered entity "+e.getClass().getName());
			new Throwable().printStackTrace();
		}
	}
	
	public void remove(Entity e){
		this.toRemove.add(e);
	}
	
	public int getNewId(){
		int id = Constants.randInt(1, Integer.MAX_VALUE);
		while(entities.containsKey(id)){
			id = Constants.randInt(1, Integer.MAX_VALUE);
		}
		return id;
	}

	public int totalEntities() {
		return entities.size();
	}
	
	public TileEntity getTileEntityForPos(int x, int y, int z){
		Iterator<Entry<Integer,Entity>> it = this.entities.entrySet().iterator();
		TileEntity ret = null;
		while(it.hasNext()){
			Entity e = it.next().getValue();
			if( e instanceof TileEntity && ((TileEntity)e).getBlockpos().equals(new Vector3i(x,y,z))){
				ret = (TileEntity)e;
			}
		}
		return ret;
	}

	public Class getRegisteredEntity(String name) {
		return registeredEntities.get(name);
	}

}
