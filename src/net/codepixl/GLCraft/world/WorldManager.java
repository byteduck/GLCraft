package net.codepixl.GLCraft.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.Inventory.GUICrafting;
import net.codepixl.GLCraft.GUI.Inventory.GUICraftingAdvanced;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.Frustum;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.OpenSimplexNoise;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityFallingBlock;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;

public class WorldManager {
	public int loop = 0;
	public static OpenSimplexNoise noise;
	public boolean doneGenerating = false;
	public EntityManager entityManager;
	private volatile HashMap<Vector3i,Chunk> activeChunks; //Vector3i because HashMap doesn't play well with floats (therefore vector3f)
	private ShaderProgram shader;
	public static Tile selectedBlock = Tile.Air;
	public CentralManager centralManager;
	public float tick = 0f;
	public int currentChunk = 0;
	private static WorldManager cw;
	private boolean saving = false;
	private Save currentSave;
	
	public WorldManager(CentralManager w){
		this.centralManager = w;
		cw = this;
		initGL();
		init();
		scheduleSaving();
		//createWorld();
	}
	
	private void initGL(){
		Shader temp = new Shader("/shaders/chunk.vert","/shaders/chunk.frag");
		shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}
	
	private void init(){
		entityManager = new EntityManager(this);
		entityManager.initPlayer();
		activeChunks = new HashMap<Vector3i,Chunk>();
		GUIManager.getMainManager().addGUI(new GUICrafting(entityManager.getPlayer()), "crafting");
		GUIManager.getMainManager().addGUI(new GUICraftingAdvanced(entityManager.getPlayer()), "adv_crafting");
	}
	
	public void createWorld(String name){
		System.out.println("Creating Chunks...");
		noise = new OpenSimplexNoise(Constants.rand.nextLong());
		centralManager.initSplashText();
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					currentChunk++;
					int progress = (int) (((float)currentChunk/(float)Math.pow(Constants.worldLengthChunks, 3))*100f);
					centralManager.renderSplashText("Terraforming...", progress+"%", progress);
					Chunk c = new Chunk(shader, 1, x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE, this);
					activeChunks.put(new Vector3i(c.getPos()), c);
					//saveChunk(activeChunks.get(activeChunks.size() - 1));
				}
			}
		}
		Iterator<Chunk> i = activeChunks.values().iterator();
		System.out.println("Populating World...");
		this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			int progress = (int) (((float)currentChunk/(float)Math.pow(Constants.worldLengthChunks, 3))*100f);
			centralManager.renderSplashText("Planting...", progress+"%", progress);
			i.next().populateChunk();
		}
		i = activeChunks.values().iterator();
		
		this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			int progress = (int) (((float)currentChunk/(float)Math.pow(Constants.worldLengthChunks, 3))*100f);
			centralManager.renderSplashText("Lighting...", progress+"%", progress);
			i.next().light();
		}
		
		i = activeChunks.values().iterator();
		
		this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			int progress = (int) (((float)currentChunk/(float)Math.pow(Constants.worldLengthChunks, 3))*100f);
			centralManager.renderSplashText("Decorating...", progress+"%", progress);
			Chunk c = i.next();
			c.rebuild();
			c.rebuildTickTiles();
		}
		centralManager.renderSplashText("Hold on...", "Beaming you down");
		System.out.println("Done!");
		doneGenerating = true;
		String saveName = name.replaceAll("[^ a-zA-Z0-9.-]", "_");
		this.currentSave = new Save(saveName, name, GLCraft.version, SaveManager.currentFormat);
		if(!SaveManager.saveWorld(this, currentSave, false)){
			doneGenerating = false;
			centralManager.renderSplashText("ERROR", "There was an error saving.");
			while(true){}
		}
	}
	
	public void worldFromBuf(){
		centralManager.initSplashText();
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					currentChunk++;
					Chunk c = new Chunk(shader, CentralManager.MIXEDCHUNK, x * Constants.CHUNKSIZE, 0, z * Constants.CHUNKSIZE, this, true);
					activeChunks.put(new Vector3i(c.getPos()), c);
					c.rebuild();
				}
			}
		}
		doneGenerating = true;
	}
	
	public void update(){
		DebugTimer.startTimer("entity_update");
		entityManager.update();
		DebugTimer.endTimer("entity_update");
		
		DebugTimer.startTimer("chunk_update");
		Iterator<Chunk> i = activeChunks.values().iterator();
		while(i.hasNext()){
			i.next().update();
		}
		DebugTimer.endTimer("chunk_update");
		
		
		tick+=Time.getDelta();
		if(tick > 1.0f/20f){
			DebugTimer.startTimer("chunk_tick");
			tick = 0f;
			i = activeChunks.values().iterator();
			while(i.hasNext()){
				i.next().tick();
			}
			Iterator<TickHelper> iter = Tile.tickMap.values().iterator();
			while(iter.hasNext()){
				iter.next().setUpdated(false);
			}
			DebugTimer.endTimer("chunk_tick");
		}
		
	}
	
	private void loadUnload() {
		EntityPlayer p = entityManager.getPlayer();
		Vector3f pos = p.getPos();
		Iterator<Chunk> i = activeChunks.values().iterator();
		List<Vector3f> chunkPos = getChunkPosInRadiusOfPlayer();
		ArrayList<Vector3f> there = new ArrayList<Vector3f>();
		while(i.hasNext()){
			Chunk c = i.next();
			if(MathUtils.distance(pos, new Vector3f(c.getPos().x,pos.y,c.getPos().z)) > Constants.worldLengthChunks*Constants.CHUNKSIZE){
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
			activeChunks.put(new Vector3i(c.getPos()), c);
			c.rebuild();
			c.populateChunk();
			c.rebuild();
		}
	}
	
	public List<Vector3f> getChunkPosInRadiusOfPlayer(){
		ArrayList<Vector3f> ret = new ArrayList<Vector3f>();
		EntityPlayer p = entityManager.getPlayer();
		Vector3f pos = p.getPos();
		for(int x = -Constants.worldLengthChunks * Constants.CHUNKSIZE; x < Constants.worldLengthChunks * Constants.CHUNKSIZE; x+=Constants.CHUNKSIZE){
			for(int y = -Constants.worldLengthChunks * Constants.CHUNKSIZE; y < Constants.worldLengthChunks * Constants.CHUNKSIZE; y+=Constants.CHUNKSIZE){
				for(int z = -Constants.worldLengthChunks * Constants.CHUNKSIZE; z < Constants.worldLengthChunks * Constants.CHUNKSIZE; z+=Constants.CHUNKSIZE){
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
	
	ArrayList<Chunk> toRender = new ArrayList<Chunk>();
	
	public void render(){
		if(doneGenerating){
			toRender.clear();
			DebugTimer.startTimer("chunk_render");
			Spritesheet.atlas.bind();
			getEntityManager().getPlayer().applyTranslations();
			Vector3f pos = getEntityManager().getPlayer().getPos();
			//if(getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == 0 || getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == -1 || getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == 9 || getTileAtPos((int)pos.x,(int)pos.y+2,(int)pos.z) == 4){
				Iterator<Chunk> i = activeChunks.values().iterator();
				while(i.hasNext()){
					Chunk c = i.next();
					if(Frustum.getFrustum().cubeInFrustum(c.getPos().getX(), c.getPos().getY(), c.getPos().getZ(), c.getPos().getX() + Constants.CHUNKSIZE, c.getPos().getY() + Constants.CHUNKSIZE, c.getPos().getZ() + Constants.CHUNKSIZE)){
						float distance=(float) Math.sqrt(Math.pow(c.getCenter().getX()-entityManager.getPlayer().getX(),2) + Math.pow(c.getCenter().getY()-entityManager.getPlayer().getY(),2) + Math.pow(c.getCenter().getZ()-entityManager.getPlayer().getZ(),2));
						if(distance < Constants.renderDistance*Constants.CHUNKSIZE){
							toRender.add(c);
						}
					}
				}
			//}
			i = toRender.iterator();
			while(i.hasNext()){
				i.next().render(false);
			}
			DebugTimer.pauseTimer("chunk_render");
			//System.out.println(Raytracer.getScreenCenterRay());
			DebugTimer.startTimer("entity_render");
			entityManager.render();
			DebugTimer.endTimer("entity_render");
			
			DebugTimer.startTimer("chunk_render");
			i = toRender.iterator();
			while(i.hasNext()){
				i.next().render(true);
			}
			DebugTimer.endTimer("chunk_render");
		}
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
	
	public void saveChunks(String name) throws IOException{
		Iterator<Chunk> i = this.activeChunks.values().iterator();
		int index = 0;
		File f = new File(Constants.GLCRAFTDIR+"saves/"+name+"/chunks");
		f.mkdirs();
		while(i.hasNext()){
			Chunk c = i.next();
			c.save(Constants.GLCRAFTDIR+"saves/"+name+"/chunks/chunk"+index+".nbt");
			index++;
		}
	}
	
	public void loadChunks(Save s) throws IOException{
		for(int i = 0; i < activeChunks.size(); i++){
			NbtInputStream in = new NbtInputStream(new FileInputStream(Constants.GLCRAFTDIR+"saves/"+s.name+"/chunks/chunk"+i+".nbt"));
			TagCompound t = (TagCompound) in.readTag();
			TagCompound post = t.getCompound("pos");
			Vector3i pos = new Vector3i(post.getFloat("x"), post.getFloat("y"), post.getFloat("z"));
			activeChunks.get(pos).load(t);
		}
		
		Iterator<Chunk> i = this.activeChunks.values().iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			c.rebuild();
			c.rebuildTickTiles();
		}
			
	}
	
	public ArrayList<AABB> BlockAABBForEntity(EntitySolid entitySolid){
		ArrayList<AABB> arraylist = new ArrayList<AABB>();
		AABB mAABB = entitySolid.getAABB();
		mAABB.render();
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
	
	public ArrayList<AABB> BlockAABBForEntity(EntitySolid entitySolid, Tile t){
		ArrayList<AABB> arraylist = new ArrayList<AABB>();
		AABB mAABB = entitySolid.getAABB();
		for(int x = (int) (entitySolid.getX()-1); x < entitySolid.getX()+mAABB.getSize().x; x++){
			for(int y = (int) (entitySolid.getY()-1); y < entitySolid.getY()+mAABB.getSize().y; y++){
				for(int z = (int) (entitySolid.getZ()-1); z < entitySolid.getZ()+mAABB.getSize().z; z++){
					if(Tile.getTile((byte)getTileAtPos(x,y,z)) == t){
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
		Chunk c = getChunk(new Vector3f(x,y,z));
		if(c == null)
			return -1;
		return c.getTileAtCoord(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z);
	}
	
	public boolean getIsTickTile(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.values().iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				return c.isTickTile(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z);
			}
		}
		return false;
	}
	
	public byte getMetaAtPos(int x, int y, int z){
		Chunk c = getChunk(x,y,z);
		return c.getMetaAtCoord(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z);
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild){
		setTileAtPos(x,y,z,tile,rebuild,(byte)0);
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild, byte meta){
		Chunk c = getChunk(x,y,z);
		c.setTileAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z, tile, false);
		setMetaAtPos(x,y,z,meta,rebuild);
		Tile.getTile((byte)getTileAtPos(x,y,z)).blockUpdate(x,y,z,this);
		Tile.getTile((byte)getTileAtPos(x+1,y,z)).blockUpdate(x+1,y,z,this);
		Tile.getTile((byte)getTileAtPos(x-1,y,z)).blockUpdate(x-1,y,z,this);
		Tile.getTile((byte)getTileAtPos(x,y+1,z)).blockUpdate(x,y+1,z,this);
		Tile.getTile((byte)getTileAtPos(x,y-1,z)).blockUpdate(x,y-1,z,this);
		Tile.getTile((byte)getTileAtPos(x,y,z+1)).blockUpdate(x,y,z+1,this);
		Tile.getTile((byte)getTileAtPos(x,y,z-1)).blockUpdate(x,y,z-1,this);
		return;
	}
	
	public void rebuildAtPos(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.values().iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				c.queueRebuild();
				return;
			}
		}
	}
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean rebuild){
		setMetaAtPos(x,y,z,meta,rebuild,true,true);
	}
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean rebuild, boolean blockUpdate, boolean updateSelf){
		Chunk c = getChunk(x,y,z);
		c.setMetaAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z,meta, rebuild);
		if(blockUpdate){
			if(updateSelf) Tile.getTile((byte)getTileAtPos(x,y,z)).blockUpdate(x,y,z,this);
			Tile.getTile((byte)getTileAtPos(x+1,y,z)).blockUpdate(x+1,y,z,this);
			Tile.getTile((byte)getTileAtPos(x-1,y,z)).blockUpdate(x-1,y,z,this);
			Tile.getTile((byte)getTileAtPos(x,y+1,z)).blockUpdate(x,y+1,z,this);
			Tile.getTile((byte)getTileAtPos(x,y-1,z)).blockUpdate(x,y-1,z,this);
			Tile.getTile((byte)getTileAtPos(x,y,z+1)).blockUpdate(x,y,z+1,this);
			Tile.getTile((byte)getTileAtPos(x,y,z-1)).blockUpdate(x,y,z-1,this);
		}
		return;
	}
	
	public Chunk getChunk(int x, int y, int z){
		return getChunk(new Vector3f(x,y,z));
	}
	
	@Deprecated
	public void rebuildInRadius(int x, int y, int z, int rad){
		Iterator<Chunk> i = activeChunks.values().iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(MathUtils.distance(c.getCenter(),new Vector3f(x,y,z)) <= rad){
				c.rebuild();
			}
		}
	}
	
	public Chunk getChunk(Vector3f pos){
		Vector3f opos = pos;
		pos = new Vector3f(pos);
		pos.x = (float) ((int)pos.x/16*16);
		pos.y = (float) ((int)pos.y/16*16);
		pos.z = (float) ((int)pos.z/16*16);
		Chunk c = activeChunks.get(new Vector3i(pos));
		if(c != null)
			return c;
		else
			return activeChunks.get(new Vector3i(0,0,0));
	}
	
	public int getLight(int x, int y, int z,boolean ChunkCoords){
		Vector3f posi = MathUtils.coordsToChunkPos(x, y, z);
		if(getChunk(posi) != null)
			if(isInBounds(new Vector3f(x,y,z)))
				return getChunk(posi).getLight(new Vector3f(x,y,z),ChunkCoords);
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
		if(getChunk(posi) != null)
		getChunk(posi).setLight(new Vector3f(x,y,z),light,ChunkCoords);

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
	
	public TileEntity getTileEntityAtPos(int x, int y, int z){
		return entityManager.getTileEntityForPos(x, y, z);
	}

	public boolean loadWorld(Save s) {
		this.currentSave = s;
		centralManager.initSplashText();
		centralManager.renderSplashText("Loading World...", "Hold on...");
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					Chunk c = new Chunk(shader, x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE, this);
					activeChunks.put(new Vector3i(c.getPos()), c);
				}
			}
		}
		boolean success = SaveManager.loadWorld(this, s.name);
		if(success){
			this.doneGenerating = true;
			return true;
		}else{
			return false;
		}
	}

	public static boolean saveWorld(boolean quit) {
		if(!cw.isSaving() && cw.doneGenerating)
			return SaveManager.saveWorld(cw, cw.currentSave, quit);
		else if(quit)
			System.exit(0);
		return true;
	}

	public int getMetaAtPos(float x, float y, float z) {
		return getMetaAtPos((int)x,(int)y,(int)z);
	}

	public void setSaving(boolean saving) {
		this.saving = saving;
	}

	public boolean isSaving() {
		return saving;
	}
	
	private void scheduleSaving(){
		int MINUTES = 3;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
		    @Override
		    public void run(){
		    	saveWorld(false);
		    }
		 }, 0, 1000 * 60 * MINUTES);
	}
	
}
