package net.codepixl.GLCraft.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.io.Files;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.Splash;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.Inventory.GUICrafting;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Frustum;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.OpenSimplexNoise;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;

public class WorldManager {
	public int loop = 0;
	public Splash s;
	public static OpenSimplexNoise noise;
	public boolean doneGenerating = false;
	public EntityManager entityManager;
	private volatile ArrayList<Chunk> activeChunks;
	private ShaderProgram shader;
	public static Tile selectedBlock = Tile.Air;
	public CentralManager world;
	public float tick = 0f;
	
	public WorldManager(CentralManager w){
		this.world = w;
		initGL();
		init();
		//createWorld();
	}
	
	private void initGL(){
		Shader temp = new Shader("/shaders/chunk.vert","/shaders/chunk.frag");
		shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}
	
	private void init(){
		entityManager = new EntityManager(this);
		entityManager.initPlayer();
		activeChunks = new ArrayList<Chunk>();
		GUIManager.getMainManager().addGUI(new GUICrafting(entityManager.getPlayer()), "crafting");
	}
	
	public void createWorld(){
		System.out.println("Creating Chunks...");
		s = new Splash();
		noise = new OpenSimplexNoise(10);
		for(int x = 0; x < Constants.viewDistance; x++){
			for(int y = 0; y < Constants.viewDistance; y++){
				for(int z = 0; z < Constants.viewDistance; z++){
					activeChunks.add(new Chunk(shader, 1, x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE, this));
					//saveChunk(activeChunks.get(activeChunks.size() - 1));
				}
			}
		}
		Iterator<Chunk> i = activeChunks.iterator();
		System.out.println("Populating World...");
		while(i.hasNext()){
			i.next().populateChunk();
		}
		i = activeChunks.iterator();
		
		while(i.hasNext()){
			i.next().light();
		}
		
		i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			c.rebuild();
			c.rebuildTickTiles();
		}
		s.getSplash().splashOff();
		System.out.println("Done!");
		doneGenerating = true;
	}
	
	public void worldFromBuf(){
		s = new Splash();
		for(int x = 0; x < Constants.viewDistance; x++){
			for(int y = 0; y < Constants.viewDistance; y++){
				for(int z = 0; z < Constants.viewDistance; z++){
					Chunk c = new Chunk(shader, CentralManager.MIXEDCHUNK, x * Constants.CHUNKSIZE, 0, z * Constants.CHUNKSIZE, this, true);
					activeChunks.add(c);
					c.rebuild();
				}
			}
		}
		s.getSplash().splashOff();
		doneGenerating = true;
	}
	
	public void update(){
		//long utime = System.currentTimeMillis();
		entityManager.update();
		Iterator<Chunk> i = activeChunks.iterator();
		//long ctime = System.currentTimeMillis();
		while(i.hasNext()){
			i.next().update();
		}
		//ctime = System.currentTimeMillis() - ctime;
		//loadUnload();
		//long ttime = System.currentTimeMillis();
		tick+=Time.getDelta();
		if(tick > 1.0f/20f){
			tick = 0f;
			i = activeChunks.iterator();
			while(i.hasNext()){
				i.next().tick();
			}
		}
		//ttime = System.currentTimeMillis() - ttime;
		//utime = System.currentTimeMillis() - utime;
		//System.out.println("Total Update Time: "+utime+"ms Chunk update time: "+ctime+"ms Chunk Tick Time "+ttime+"ms");
	}
	
	private void loadUnload() {
		EntityPlayer p = entityManager.getPlayer();
		Vector3f pos = p.getPos();
		Iterator<Chunk> i = activeChunks.iterator();
		List<Vector3f> chunkPos = getChunkPosInRadiusOfPlayer();
		ArrayList<Vector3f> there = new ArrayList<Vector3f>();
		while(i.hasNext()){
			Chunk c = i.next();
			if(MathUtils.distance(pos, new Vector3f(c.getPos().x,pos.y,c.getPos().z)) > Constants.viewDistance*Constants.CHUNKSIZE){
				i.remove();
			}
			there.add(c.getPos());
		}
		Iterator<Vector3f> it = chunkPos.iterator();
		ArrayList<Vector3f> toAdd = new ArrayList<Vector3f>();
		while(it.hasNext()){
			Vector3f next = it.next();
			if(!there.contains(next)){
				toAdd.add(next);
			}
		}
		if(toAdd.size() > 0){
			Vector3f cpos = toAdd.get(0);
			Chunk c = new Chunk(shader, 1, cpos, this);
			activeChunks.add(c);
			c.rebuild();
			c.populateChunk();
			c.rebuild();
		}
	}
	
	public List<Vector3f> getChunkPosInRadiusOfPlayer(){
		ArrayList<Vector3f> ret = new ArrayList<Vector3f>();
		EntityPlayer p = entityManager.getPlayer();
		Vector3f pos = p.getPos();
		for(int x = -Constants.viewDistance * Constants.CHUNKSIZE; x < Constants.viewDistance * Constants.CHUNKSIZE; x+=Constants.CHUNKSIZE){
			for(int y = -Constants.viewDistance * Constants.CHUNKSIZE; y < Constants.viewDistance * Constants.CHUNKSIZE; y+=Constants.CHUNKSIZE){
				for(int z = -Constants.viewDistance * Constants.CHUNKSIZE; z < Constants.viewDistance * Constants.CHUNKSIZE; z+=Constants.CHUNKSIZE){
					Vector3f add = MathUtils.round(new Vector3f(x+pos.x-(Math.round(pos.x) % 16),y+pos.y-(Math.round(pos.y) % 16),z+pos.z-(Math.round(pos.z) % 16)));
					if(add.x >= 0)
						if(add.y >= 0)
							if(add.z >= 0)
								ret.add(add);
				}
			}
		}
		return ret;
	}

	public void spawnEntity(Entity e){
		entityManager.add(e);
	}
	
	public void render(){
		long rtime = System.currentTimeMillis();
		Spritesheet.atlas.bind();
		getEntityManager().getPlayer().applyTranslations();
		Vector3f pos = getEntityManager().getPlayer().getPos();
		//if(getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == 0 || getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == -1 || getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == 9 || getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == 4){
			for(int i = 0; i < activeChunks.size(); i++){
				if(Frustum.getFrustum().cubeInFrustum(activeChunks.get(i).getPos().getX(), activeChunks.get(i).getPos().getY(), activeChunks.get(i).getPos().getZ(), activeChunks.get(i).getPos().getX() + Constants.CHUNKSIZE, activeChunks.get(i).getPos().getY() + Constants.CHUNKSIZE, activeChunks.get(i).getPos().getZ() + Constants.CHUNKSIZE)){
					float distance=(float) Math.sqrt(Math.pow(activeChunks.get(i).getCenter().getX()-entityManager.getPlayer().getX(),2) + Math.pow(activeChunks.get(i).getCenter().getY()-entityManager.getPlayer().getY(),2) + Math.pow(activeChunks.get(i).getCenter().getZ()-entityManager.getPlayer().getZ(),2));
					if(distance < 10*Constants.CHUNKSIZE){
						activeChunks.get(i).render();
					}
				}
			}
		//}
		//System.out.println(Raytracer.getScreenCenterRay());
		entityManager.render();
		rtime = System.currentTimeMillis() - rtime;
		System.out.println("Render Time: "+rtime+"ms");
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
	
	public void saveChunks() throws IOException{
		Files.createParentDirs(new File("Chunks/test.chunk"));
		Iterator<Chunk> i = this.activeChunks.iterator();
		int index = 0;
		while(i.hasNext()){
			Chunk c = i.next();
			c.save("Chunks/Chunk"+index+".nbt");
			index++;
		}
	}
	
	public void loadChunks() throws IOException{
		Iterator<Chunk> i = this.activeChunks.iterator();
		int index = 0;
		while(i.hasNext()){
			Chunk c = i.next();
			c.load("Chunks/Chunk"+index+".nbt");
			index++;
		}
	}
	
	public ArrayList<AABB> BlockAABBForEntity(EntitySolid entitySolid){
		ArrayList<AABB> arraylist = new ArrayList<AABB>();
		AABB mAABB = entitySolid.getAABB();
		for(int x = (int) (entitySolid.getX()-1); x < entitySolid.getX()+mAABB.getSize().x; x++){
			for(int y = (int) (entitySolid.getY()-1); y < entitySolid.getY()+mAABB.getSize().y; y++){
				for(int z = (int) (entitySolid.getZ()-1); z < entitySolid.getZ()+mAABB.getSize().z; z++){
					if(!Tile.getTile((byte)getTileAtPos(x,y,z)).canPassThrough()){
						AABB aabb = new AABB(1,1,1);
						aabb.update(new Vector3f(x+0.5f,y,z+0.5f));
						//if(!AABB.testAABB(aabb, mAABB))
							arraylist.add(aabb);
					}
				}
			}
		}
		return arraylist;
	}
	
	public int getTileAtPos(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				return c.getTileAtCoord(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z);
			}
		}
		return -1;
	}
	
	public byte getMetaAtPos(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				return c.getMetaAtCoord(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z);
			}
		}
		return -1;
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				c.setTileAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z,tile, rebuild);
				Tile.getTile((byte)getTileAtPos(x,y,z)).blockUpdate(x,y,z,this);
				Tile.getTile((byte)getTileAtPos(x+1,y,z)).blockUpdate(x+1,y,z,this);
				Tile.getTile((byte)getTileAtPos(x-1,y,z)).blockUpdate(x-1,y,z,this);
				Tile.getTile((byte)getTileAtPos(x,y+1,z)).blockUpdate(x,y+1,z,this);
				Tile.getTile((byte)getTileAtPos(x,y-1,z)).blockUpdate(x,y-1,z,this);
				Tile.getTile((byte)getTileAtPos(x,y,z+1)).blockUpdate(x,y,z+1,this);
				Tile.getTile((byte)getTileAtPos(x,y,z-1)).blockUpdate(x,y,z-1,this);
				return;
			}
		}
	}
	
	public void rebuildAtPos(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				c.queueRebuild();
				return;
			}
		}
	}
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean rebuild){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				c.setMetaAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z,meta, rebuild);
				Tile.getTile((byte)getTileAtPos(x,y,z)).blockUpdate(x,y,z,this);
				Tile.getTile((byte)getTileAtPos(x+1,y,z)).blockUpdate(x+1,y,z,this);
				Tile.getTile((byte)getTileAtPos(x-1,y,z)).blockUpdate(x-1,y,z,this);
				Tile.getTile((byte)getTileAtPos(x,y+1,z)).blockUpdate(x,y+1,z,this);
				Tile.getTile((byte)getTileAtPos(x,y-1,z)).blockUpdate(x,y-1,z,this);
				Tile.getTile((byte)getTileAtPos(x,y,z+1)).blockUpdate(x,y,z+1,this);
				Tile.getTile((byte)getTileAtPos(x,y,z-1)).blockUpdate(x,y,z-1,this);
				return;
			}
		}
	}
	
	@Deprecated
	public Chunk getChunk(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(c.getPos().x <= x && c.getPos().y <= y && c.getPos().z <= z){
				if(c.getPos().x+Constants.CHUNKSIZE >= x && c.getPos().y+Constants.CHUNKSIZE >= y && c.getPos().z+Constants.CHUNKSIZE >= z)
					return c;
			}
		}
		return null;
	}
	
	@Deprecated
	public void rebuildInRadius(int x, int y, int z, int rad){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(MathUtils.distance(c.getCenter(),new Vector3f(x,y,z)) <= rad){
				c.rebuild();
			}
		}
	}
	
	public Chunk getChunkAtCoords(Vector3f pos){
		pos = new Vector3f(pos);
		pos.x = (float) (Math.floor(pos.x/16) * 16);
		pos.y = (float) (Math.floor(pos.y/16) * 16);
		pos.z = (float) (Math.floor(pos.z/16) * 16);
		if(pos.x % 16 == 0 && pos.y % 16 == 0 && pos.z % 16 == 0){
			Iterator<Chunk> i = activeChunks.iterator();
			while(i.hasNext()){
				Chunk c = i.next();
				if(c.getPos().equals(pos)){
					return c;
				}
			}
		}
		System.err.println("Coordinates must be divisible by 15!");
		return activeChunks.get(0);
	}
	
	public int getLight(int x, int y, int z,boolean ChunkCoords){
		Vector3f posi = MathUtils.coordsToChunkPos(x, y, z);
		if(getChunkAtCoords(posi) != null)
			if(isInBounds(new Vector3f(x,y,z)))
				return getChunkAtCoords(posi).getLight(new Vector3f(x,y,z),ChunkCoords);
		return 0;
	}
	
	public boolean isInBounds(Vector3f pos){
		if(pos.x < 0 || pos.x > Constants.worldLength)
			return false;
		if(pos.y < 0 || pos.y > Constants.worldLength)
			return false;
		if(pos.z < 0 || pos.z > Constants.worldLength)
			return false;
		return true;
	}

	public void setLight(int x, int y, int z, int light, boolean ChunkCoords) {
		Vector3f posi = MathUtils.coordsToChunkPos(x, y, z);
		if(getChunkAtCoords(posi) != null)
		getChunkAtCoords(posi).setLight(new Vector3f(x,y,z),light,ChunkCoords);

	}
	
	public void putLight(int x, int y, int z, int light){
	     int xDecreasing = x - 1;
	     int xIncreasing = x + 1;
	     int yDecreasing = y - 1;
	     int yIncreasing = y + 1;
	     int zDecreasing = z - 1;
	     int zIncreasing = z + 1;
	     if(isInBounds(new Vector3f(x,y,z)))
        	 setLight(x,y,z,light,false);
	     light-=1;
	     if (light > 0)
	     {
	         if(getLight(xIncreasing, y, z, false) < light && Tile.getTile((byte)getTileAtPos(x,y,z)).isTransparent()){
	        	 putLight(xIncreasing, y, z, light);
	         }
	         if(getLight(xDecreasing, y, z, false) < light && Tile.getTile((byte)getTileAtPos(x,y,z)).isTransparent()){
	        	 putLight(xDecreasing, y, z, light);
	         }
	         if(getLight(x, yIncreasing, z, false) < light && Tile.getTile((byte)getTileAtPos(x,y,z)).isTransparent()){
	        	 putLight(x, yIncreasing, z, light);
	         }
	         if(getLight(x, yDecreasing, z, false) < light && Tile.getTile((byte)getTileAtPos(x,y,z)).isTransparent()){
	        	 putLight(x, yDecreasing, z, light);
	         }
	         if(getLight(x, y, zIncreasing, false) < light && Tile.getTile((byte)getTileAtPos(x,y,z)).isTransparent()){
	        	 putLight(x, y, zIncreasing, light);
	         }
	         if(getLight(x, y, zDecreasing, false) < light && Tile.getTile((byte)getTileAtPos(x,y,z)).isTransparent()){
	        	 putLight(x, y, zDecreasing, light);
	         }
	     }
	 }

	public int getTileAtPos(Vector3f p) {
		return getTileAtPos((int)p.x,(int)p.y,(int)p.z);
	}

	public int getTileAtPos(float x, float y, float z) {
		return getTileAtPos((int)x,(int)y,(int)z);
	}

	public void setTileAtPos(Vector3f thisPos, byte tile, boolean rebuild) {
		setTileAtPos((int)thisPos.x,(int)thisPos.y,(int)thisPos.z,tile,rebuild);
	}
	
}
