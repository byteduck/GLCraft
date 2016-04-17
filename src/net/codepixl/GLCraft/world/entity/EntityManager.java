package net.codepixl.GLCraft.world.entity;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagList;

import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;

public class EntityManager implements GameObj{
	
	private ArrayList<Entity> entities;
	private ArrayList<Entity> toAdd;
	private ArrayList<Entity> toRemove;
	private ArrayList<TileEntity> tileEntities;
	boolean shouldRemoveAll = false;
	private EntityPlayer player;
	private EntityPlayerMP otherPlayer;
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
		initGL();
	}
	
	public void initPlayer(){
		player = new EntityPlayer(new Vector3f(16,100,16),w);
		add(player);
	}
	
	public void initPlayerMP(){
		otherPlayer = new EntityPlayerMP(new Vector3f(0,0,0),w);
		add(otherPlayer);
	}
	
	private void initGL(){
		mobRenderID = glGenLists(1);
	}
	
	public void save() throws IOException{
		FileOutputStream outputStream;
		outputStream = new FileOutputStream("entities.nbt");
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

	public EntityPlayerMP getPlayerMP() {
		// TODO Auto-generated method stub
		return this.otherPlayer;
	}

	public void add(Entity e) {
			this.toAdd.add(e);
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
			if(e.pos.equals(new Vector3f(x,y,z))){
				ret = e;
			}
		}
		return ret;
	}

}
