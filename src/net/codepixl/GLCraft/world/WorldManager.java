package net.codepixl.GLCraft.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.Inventory.GUICrafting;
import net.codepixl.GLCraft.GUI.Inventory.GUICraftingAdvanced;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.Frustum;
import net.codepixl.GLCraft.util.GameTime;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.OpenSimplexNoise;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.Vector2i;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;

public class WorldManager {
	public int loop = 0;
	public static OpenSimplexNoise elevationNoise, roughnessNoise, detailNoise;
	public boolean doneGenerating = false;
	public EntityManager entityManager;
	private volatile HashMap<Vector3i,Chunk> activeChunks; //Vector3i because HashMap doesn't play well with floats (therefore vector3f)
	public ShaderProgram shader;
	public static Tile selectedBlock = Tile.Air;
	public CentralManager centralManager;
	public float tick = 0f;
	public int currentChunk = 0;
	private static WorldManager cw;
	private boolean saving = false;
	private Save currentSave;
	private long worldTime;
	private GameTime gameTime = new GameTime(0);
	public Queue<Light> lightQueue = new LinkedList<Light>();
	public Queue<Light> sunlightQueue = new LinkedList<Light>();
	public Queue<LightRemoval> lightRemovalQueue = new LinkedList<LightRemoval>();
	public ArrayList<Chunk> lightRebuildQueue = new ArrayList<Chunk>();
	public Queue<LightRemoval> sunlightRemovalQueue = new LinkedList<LightRemoval>();
	private int currentRebuild = 0;
	
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
		elevationNoise = new OpenSimplexNoise(Constants.rand.nextLong());
		roughnessNoise = new OpenSimplexNoise(Constants.rand.nextLong());
		detailNoise = new OpenSimplexNoise(Constants.rand.nextLong());
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
		
		/*this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			int progress = (int) (((float)currentChunk/(float)Math.pow(Constants.worldLengthChunks, 3))*100f);
			centralManager.renderSplashText("Lighting...", progress+"%", progress);
		}*/
		
		i = activeChunks.values().iterator();
		
		this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			int progress = (int) (((float)currentChunk/(float)Math.pow(Constants.worldLengthChunks, 3))*100f);
			centralManager.renderSplashText("Decorating...", progress+"%", progress);
			Chunk c = i.next();
			c.rebuildBase(true);
			c.rebuildBase(false);
			c.rebuildTickTiles();
		}
		centralManager.renderSplashText("Hold on...", "Beaming you down");
		reSunlight();
		System.out.println("Done!");
		entityManager.getPlayer().respawn();
		doneGenerating = true;
		String saveName = name.replaceAll("[^ a-zA-Z0-9.-]", "_");
		this.currentSave = new Save(saveName, name, GLCraft.version, SaveManager.currentFormat);
		if(!SaveManager.saveWorld(this, currentSave, false)){
			doneGenerating = false;
			centralManager.renderSplashText("ERROR", "There was an error saving.");
			while(true){}
		}
		this.worldTime = Constants.dayLengthMS/2;
		this.gameTime = new GameTime(this.worldTime);
	}
	
	public void showMessage(double seconds, String message){
		centralManager.showMessage(seconds, message);
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
		
		worldTime+=Time.getDelta()*1000;
		gameTime.updateTime(worldTime);
		
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
			
			DebugTimer.startTimer("entity_render");
			entityManager.render();
			DebugTimer.endTimer("entity_render");
			
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
	
	public float getSkyLightIntensity(){
		if(this.gameTime.getHours() < 12)
			return this.gameTime.getHours()/12f+this.gameTime.getMinutes()/600f;
		else
			return (-this.gameTime.getHours()+24)/12f+this.gameTime.getMinutes()/600f;
	}
	
	public void saveChunks(String name) throws IOException{
		Iterator<Chunk> i = this.activeChunks.values().iterator();
		int index = 0;
		File f = new File(Constants.GLCRAFTDIR+"saves/"+name+"/region");
		f.mkdirs();
		HashMap<Vector2i,TagCompound> regions = new HashMap<Vector2i,TagCompound>();
		while(i.hasNext()){
			Chunk c = i.next();
			Vector2i reg = c.getRegion();
			if(!regions.containsKey(reg)){
				regions.put(reg, new TagCompound(reg.toString()));
			}
			c.save(regions.get(reg));
			index++;
		}
		Iterator<Entry<Vector2i, TagCompound>> it = regions.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<Vector2i, TagCompound> next = it.next();
	    	NbtOutputStream s = new NbtOutputStream(new FileOutputStream(new File(f,"r"+next.getKey().toString().replace("[","").replace("]","").replace(',', '.')+".nbt")));
	    	s.write(next.getValue());
	    	s.close();
	    }
	}
	
	public void loadChunks(Save s) throws IOException{
		File regionsFile = new File(Constants.GLCRAFTDIR+"saves/"+s.name+"/region");
		String[] files = regionsFile.list();
		for(String file : files){
			NbtInputStream in = new NbtInputStream(new FileInputStream(new File(regionsFile,file)));
			TagCompound t = (TagCompound) in.readTag();
			Map<String, ITag> tags = t.getTags();
			Iterator<Entry<String, ITag>> i = tags.entrySet().iterator();
			while(i.hasNext()){
				Entry<String, ITag> next = i.next();
				if(next.getValue() instanceof TagCompound){
					TagCompound chunkTag = (TagCompound) next.getValue();
					TagCompound post = chunkTag.getCompound("pos");
					Vector3i pos = new Vector3i(post.getFloat("x"), post.getFloat("y"), post.getFloat("z"));
					activeChunks.get(pos).load(chunkTag);
				}
			}
			
		}
		
		Iterator<Chunk> i = this.activeChunks.values().iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			c.rebuildBase(true);
			c.rebuildBase(false);
			c.rebuildTickTiles();
		}
		
		reSunlight();
			
	}
	
	private void reSunlight() {
		for(int x = 0; x < Constants.worldLength; x++)
			for(int z = 0; z < Constants.worldLength; z++){
				sunlightQueue.add(new Light(new Vector3i(x,Constants.worldLength-1,z)));
				setSunlight(x,Constants.worldLength-1,z,15,false);
			}
		relight();
	}

	public ArrayList<AABB> BlockAABBForEntity(EntitySolid entitySolid){
		ArrayList<AABB> arraylist = new ArrayList<AABB>();
		AABB mAABB = entitySolid.getAABB();
		mAABB.render();
		for(int x = (int) (entitySolid.getX()-1); x < entitySolid.getX()+mAABB.getSize().x; x++){
			for(int y = (int) (entitySolid.getY()-1); y < entitySolid.getY()+mAABB.getSize().y+1; y++){
				for(int z = (int) (entitySolid.getZ()-1); z < entitySolid.getZ()+mAABB.getSize().z; z++){
					if(!Tile.getTile((byte)getTileAtPos(x,y,z)).canPassThrough()){
						AABB aabb = Tile.getTile((byte)getTileAtPos(x,y,z)).getAABB(x,y,z,getMetaAtPos(x,y,z),this);
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
						AABB aabb = Tile.getTile((byte)getTileAtPos(x,y,z)).getAABB(x,y,z,getMetaAtPos(x,y,z),this);
						//if(!AABB.testAABB(aabb, mAABB))
							arraylist.add(aabb);
					}
				}
			}
		}
		return arraylist;
	}
	
	public int getTileAtPos(int x, int y, int z){
		Chunk c = getChunk(new Vector3i(x,y,z));
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
		if(c != null)
			return c.getMetaAtCoord(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z);
		else
			return -1;
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild){
		setTileAtPos(x,y,z,tile,new BreakSource(),rebuild,(byte)0);
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, BreakSource source, boolean rebuild){
		setTileAtPos(x,y,z,tile,source,rebuild,(byte)0);
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild, byte meta){
		setTileAtPos(x,y,z,tile,new BreakSource(),rebuild,meta);
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, BreakSource source, boolean rebuild, byte meta){
		Chunk c = getChunk(x,y,z);
		c.setTileAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z, tile, meta, source, true);
		blockUpdate(x+1,y,z);
		blockUpdate(x-1,y,z);
		blockUpdate(x,y+1,z);
		blockUpdate(x,y-1,z);
		blockUpdate(x,y,z+1);
		blockUpdate(x,y,z-1);
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
			if(updateSelf) blockUpdate(x,y,z);
			blockUpdate(x+1,y,z);
			blockUpdate(x-1,y,z);
			blockUpdate(x,y+1,z);
			blockUpdate(x,y-1,z);
			blockUpdate(x,y,z+1);
			blockUpdate(x,y,z-1);
		}
		return;
	}
	
	public void blockUpdate(int x, int y, int z){
		Chunk c = getChunk(x,y,z);
		if(c != null)
		c.blockUpdate(x, y, z);
	}
	
	public Chunk getChunk(int x, int y, int z){
		return getChunk(new Vector3i(x,y,z));
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
	
	public Chunk getChunk(Vector3i pos){
		Vector3i vector3i = new Vector3i(pos);
		vector3i.x = (int) (Math.floor(vector3i.x/16f)*16);
		vector3i.y = (int) (Math.floor(vector3i.y/16f)*16);
		vector3i.z = (int) (Math.floor(vector3i.z/16f)*16);
		Chunk c = activeChunks.get(vector3i);
		return c;
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
		this.worldTime = s.worldTime;
		this.gameTime = new GameTime(s.worldTime);
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

	public byte getMetaAtPos(Vector3f pos) {
		return getMetaAtPos((int)pos.x, (int)pos.y, (int)pos.z);
	}
	
	public int getSunlight(int x, int y, int z) {
		Chunk c = getChunk(x,y,z);
		if(c != null)
			return c.getSunlight(x, y, z);
		else
			return 0;
	}

	public void setSunlight(int x, int y, int z, int val, boolean relight) {
		Chunk c = getChunk(x,y,z);
		if(c != null)
			c.setSunlight(x, y, z, val, relight);
	}

	public int getBlockLight(int x, int y, int z) {
		Chunk c = getChunk(x,y,z);
		if(c != null)
			return c.getBlockLight(x, y, z);
		else
			return 0;
	}
	
	public void setBlockLight(int x, int y, int z, int val, boolean relight) {
		Chunk c = getChunk(x,y,z);
		if(c != null)
			c.setBlockLight(x, y, z, val, relight);
	}
	
	public int getLight(int x, int y, int z){
		Chunk c = getChunk(x,y,z);
		if(c != null){
			int ret = (c.getBlockLight(x, y, z)+c.getSunlight(x, y, z));
			if(ret > 15)
				ret = 15;
			return ret;
		}else
			return 0;
	}
	
	public float getLightIntensity(int x, int y, int z){
		float ret = getLight(x,y,z)/15f+0.1f;
		if(ret > 1)
			ret = 1;
		return ret;
	}

	public Chunk getChunk(Vector3f pos) {
		return getChunk(new Vector3i(pos));
	}
	
	public void relight(){
		lightRebuildQueue.clear();
		while(!lightRemovalQueue.isEmpty()){
			LightRemoval next = lightRemovalQueue.poll();
			Vector3i npos = next.pos;
			Vector3i pos = new Vector3i(npos.x+1, npos.y, npos.z);
			evalLightRemoval(npos,pos,next);
			pos = new Vector3i(npos.x-1, npos.y, npos.z);
			evalLightRemoval(npos,pos,next);
			pos = new Vector3i(npos.x, npos.y+1, npos.z);
			evalLightRemoval(npos,pos,next);
			pos = new Vector3i(npos.x, npos.y-1, npos.z);
			evalLightRemoval(npos,pos,next);
			pos = new Vector3i(npos.x, npos.y, npos.z+1);
			evalLightRemoval(npos,pos,next);
			pos = new Vector3i(npos.x, npos.y, npos.z-1);
			evalLightRemoval(npos,pos,next);
		}
		while(!lightQueue.isEmpty()){
			Light nextl = lightQueue.poll();
			Vector3i next = nextl.pos;
			Vector3i pos = new Vector3i(next.x+1, next.y, next.z);
			evalLight(nextl,pos);
			pos = new Vector3i(next.x-1, next.y, next.z);
			evalLight(nextl,pos);
			pos = new Vector3i(next.x, next.y+1, next.z);
			evalLight(nextl,pos);
			pos = new Vector3i(next.x, next.y-1, next.z);
			evalLight(nextl,pos);
			pos = new Vector3i(next.x, next.y, next.z+1);
			evalLight(nextl,pos);
			pos = new Vector3i(next.x, next.y, next.z-1);
			evalLight(nextl,pos);
		}
		while(!sunlightRemovalQueue.isEmpty()){
			LightRemoval next = sunlightRemovalQueue.poll();
			Vector3i npos = next.pos;
			this.setSunlight(npos.x, npos.y, npos.z, 0, false);
			Vector3i pos = new Vector3i(npos.x+1, npos.y, npos.z);
			evalSunlightRemoval(npos,pos,next,false);
			pos = new Vector3i(npos.x-1, npos.y, npos.z);
			evalSunlightRemoval(npos,pos,next,false);
			pos = new Vector3i(npos.x, npos.y+1, npos.z);
			evalSunlightRemoval(npos,pos,next,false);
			pos = new Vector3i(npos.x, npos.y-1, npos.z);
			evalSunlightRemoval(npos,pos,next,true);
			pos = new Vector3i(npos.x, npos.y, npos.z+1);
			evalSunlightRemoval(npos,pos,next,false);
			pos = new Vector3i(npos.x, npos.y, npos.z-1);
			evalSunlightRemoval(npos,pos,next,false);
		}
		while(!sunlightQueue.isEmpty()){
			Light nextl = sunlightQueue.poll();
			Vector3i next = nextl.pos;
			Vector3i pos = new Vector3i(next.x+1, next.y, next.z);
			evalSunlight(nextl,pos,false);
			pos = new Vector3i(next.x-1, next.y, next.z);
			evalSunlight(nextl,pos,false);
			pos = new Vector3i(next.x, next.y+1, next.z);
			evalSunlight(nextl,pos,false);
			pos = new Vector3i(next.x, next.y-1, next.z);
			evalSunlight(nextl,pos,true);
			pos = new Vector3i(next.x, next.y, next.z+1);
			evalSunlight(nextl,pos,false);
			pos = new Vector3i(next.x, next.y, next.z-1);
			evalSunlight(nextl,pos,false);
		}
		for(Chunk c : lightRebuildQueue){
			c.rebuild();
		}
	}
	
	private void evalSunlight(Light next, Vector3i dest, boolean downwards) {
		int bl, dbl;
		if(next.chunk != null){
			bl = next.chunk.getSunlight(next.pos.x, next.pos.y, next.pos.z);
			Chunk c = getChunk(dest.x, dest.y, dest.z);
			if(c != null){
				dbl = c.getSunlight(dest.x, dest.y, dest.z);
				if(dbl < bl-1){
					sunlightQueue.add(new Light(dest, c));
					byte tra = Tile.getTile((byte)getTileAtPos(dest.x, dest.y, dest.z)).getTransparency();
					byte res = 0;
					if(bl == 15 && downwards && tra <= 1)
						tra-=1;
					if(tra < bl)
						res = (byte) (bl-tra);
					c.setSunlight(dest.x, dest.y, dest.z, res, false);
					lightRebuildQueue.add(c);
				}
			}
		}
	}
	
	private void evalSunlightRemoval(Vector3i src, Vector3i dest, LightRemoval rem, boolean down){
		Chunk c = getChunk(dest.x, dest.y, dest.z);
		if(c != null){
			int dbl = c.getSunlight(dest.x, dest.y, dest.z);
			if(down){
				c.setSunlight(dest.x, dest.y, dest.z, 0, false);
				sunlightRemovalQueue.add(new LightRemoval(dest, (byte) dbl, c));
				dbl = 0;
			}
			int bl = rem.level;
			if(dbl != 0 && dbl < bl){
				c.setSunlight(dest.x, dest.y, dest.z, 0, false);
				sunlightRemovalQueue.add(new LightRemoval(dest, (byte) dbl, c));
			}else if(dbl >= bl){
				sunlightQueue.add(new Light(dest,c));
			}
			lightRebuildQueue.add(c);
		}
	}

	private void evalLight(Light next, Vector3i dest){
		int bl, dbl;
		if(next.chunk != null){
			bl = next.chunk.getBlockLight(next.pos.x, next.pos.y, next.pos.z);
			Chunk c = getChunk(dest.x, dest.y, dest.z);
			if(c != null){
				dbl = c.getBlockLight(dest.x, dest.y, dest.z);
				if(dbl < bl-1){
					lightQueue.add(new Light(dest, c));
					byte tra = Tile.getTile((byte)getTileAtPos(dest.x, dest.y, dest.z)).getTransparency();
					byte res = 0;
					if(tra < bl)
						res = (byte) (bl-tra);
					c.setBlockLight(dest.x, dest.y, dest.z, res, false);
					lightRebuildQueue.add(c);
				}
			}
		}
	}
	
	private void evalLightRemoval(Vector3i src, Vector3i dest, LightRemoval rem){
		Chunk c = getChunk(dest.x, dest.y, dest.z);
		if(c != null){
			int dbl = c.getBlockLight(dest.x, dest.y, dest.z);
			int bl = rem.level;
			if(dbl != 0 && dbl < bl){
				c.setBlockLight(dest.x, dest.y, dest.z, 0, false);
				lightRemovalQueue.add(new LightRemoval(dest, (byte) dbl, c));
			}else if(dbl >= bl){
				lightQueue.add(new Light(dest,c));
			}
			lightRebuildQueue.add(c);
		}
	}
	
	public long getWorldTime(){
		return this.worldTime;
	}
	
	public GameTime getTime(){
		return this.gameTime;
	}
	
	public static class LightRemoval{
		public Vector3i pos;
		public byte level;
		public Chunk chunk;
		public LightRemoval(Vector3i pos, byte level, Chunk chunk){
			this.pos = pos;
			this.level = level;
			this.chunk = chunk;
		}
	}
	
	public static class Light{
		public Vector3i pos;
		public Chunk chunk;
		public Light(Vector3i pos, Chunk chunk){
			this.pos = pos;
			this.chunk = chunk;
		}
		public Light(Vector3i pos) {
			this.pos = pos;
			this.chunk = GLCraft.getGLCraft().getWorldManager().getChunk(pos);
		}
	}

	public void rebuildNextChunk() {
		if(activeChunks.size() > 0){
			Chunk c = (new ArrayList<Chunk>(activeChunks.values())).get(currentRebuild);
			c.rebuildBase(true);
			c.rebuildBase(false);
			currentRebuild++;
			currentRebuild%=activeChunks.size();
		}
	}
	
}
