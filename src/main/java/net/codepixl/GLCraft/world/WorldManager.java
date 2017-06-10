package net.codepixl.GLCraft.world;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;
import com.nishu.utils.Time;
import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIServerError;
import net.codepixl.GLCraft.GUI.GUIStartScreen;
import net.codepixl.GLCraft.network.Server;
import net.codepixl.GLCraft.network.packet.*;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.util.*;
import net.codepixl.GLCraft.util.data.saves.Save;
import net.codepixl.GLCraft.util.data.saves.SaveManager;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.glClearColor;

public class WorldManager {
	public int loop = 0;
	public static OpenSimplexNoise elevationNoise, roughnessNoise, detailNoise;
	public boolean doneGenerating = false;
	public EntityManager entityManager;
	private volatile HashMap<Vector3i,Chunk> activeChunks; //Vector3i because HashMap doesn't play well with floats (therefore vector3f)
	public ShaderProgram shader,cloudShader,waterShader;
	public static Tile selectedBlock = Tile.Air;
	public CentralManager centralManager;
	public float tick = 0f;
	public int currentChunk = 0;
	private boolean saving = false;
	public Save currentSave;
	private long worldTime;
	private GameTime gameTime = new GameTime(0);
	public Queue<Light> lightQueue = new ConcurrentLinkedQueue<Light>();
	public Queue<Light> sunlightQueue = new ConcurrentLinkedQueue<Light>();
	public Queue<LightRemoval> lightRemovalQueue = new ConcurrentLinkedQueue<LightRemoval>();
	public ArrayList<Chunk> lightRebuildQueue = new ArrayList<Chunk>();
	public Queue<LightRemoval> sunlightRemovalQueue = new ConcurrentLinkedQueue<LightRemoval>();
	private int currentRebuild = 0;
	public boolean isServer;
	ArrayList<Chunk> toRender = new ArrayList<Chunk>();
	ArrayList<Vector2i> playerChunks = new ArrayList<>(), playerChunksNew = new ArrayList<>();
	private Queue<Callable<Void>> actionQueue = new LinkedList<Callable<Void>>();
	public boolean sendBlockPackets = true;
	public boolean kicked = false;
	private static WorldManager cw;
	public boolean isHost = false; //Set if this worldManager is a client and is hosting a LAN world
	public WeatherState previousWeather = new WeatherState(WeatherType.CLEAR, this);
	public WeatherState currentWeather = new WeatherState(WeatherType.CLEAR, this);
	public float weatherTransitionCountdown = 0.0f;
	private ArrayList<Vector2i> chunkRequests = new ArrayList<Vector2i>();
	private boolean queueRelight = false;
	
	public WorldManager(CentralManager w, boolean isServer){
		this.centralManager = w;
		this.isServer = isServer;
		if(isServer){
			cw = this;
			scheduleSaving();
		}else
			initGL();
		init();
		//createWorld();
	}
	
	private void initGL(){
		Shader temp = new Shader("/shaders/chunk.vert", "/shaders/chunk.frag");
		shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
		temp = new Shader("/shaders/clouds.vert","/shaders/clouds.frag");
		cloudShader = new ShaderProgram(temp.getvShader(), temp.getfShader());
		temp = new Shader("/shaders/water.vert", "/shaders/water.frag");
		waterShader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}
	
	private void init(){
		entityManager = new EntityManager(this, isServer);
		activeChunks = new HashMap<Vector3i,Chunk>();
		if(!isServer){
			entityManager.initPlayer();
		}
	}
	
	public void createBlankWorld(){
		/*for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					Chunk c = new Chunk(shader, waterShader, new Vector3f(x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE), this);
					activeChunks.put(new Vector3i(c.getPos()), c);
					//saveChunk(activeChunks.get(activeChunks.size() - 1));
				}
			}
		}*/
		this.doneGenerating = true;
	}
	
	public void createWorld(String name){
		createWorld(name, false);
	}
	
	public void createWorld(String name, boolean dedicated){
		this.sendBlockPackets = false;
		GLogger.log("Creating Chunks...", LogSource.SERVER);
		long seed = Constants.rand.nextLong();
		elevationNoise = new OpenSimplexNoise(seed*2);
		roughnessNoise = new OpenSimplexNoise(seed);
		detailNoise = new OpenSimplexNoise(seed/2);
		//centralManager.initSplashText();
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					currentChunk++;
					//centralManager.renderSplashText("Terraforming...", progress+"%", progress);
					Chunk c = new Chunk(shader, waterShader, 1, x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE, this);
					activeChunks.put(new Vector3i(c.getPos()), c);
					//saveChunk(activeChunks.get(activeChunks.size() - 1));
				}
			}
		}
		Iterator<Chunk> i = activeChunks.values().iterator();
		GLogger.log("Populating World...", LogSource.SERVER);
		this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			//centralManager.renderSplashText("Planting...", progress+"%", progress);
			i.next().populateChunk();
		}
		i = activeChunks.values().iterator();
		
		/*this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			centralManager.renderSplashText("Lighting...", progress+"%", progress);
		}*/
		
		i = activeChunks.values().iterator();
		
		this.currentChunk = 0;
		while(i.hasNext()){
			this.currentChunk++;
			//centralManager.renderSplashText("Decorating...", progress+"%", progress);
			Chunk c = i.next();
			c.rebuildTickTiles();
		}
		//centralManager.renderSplashText("Hold on...", "Beaming you down");
		GLogger.log("Done!", LogSource.SERVER);
		doneGenerating = true;
		String saveName = name.replaceAll("[^ a-zA-Z0-9.-]", "_");
		this.currentSave = new Save(saveName, name, GLCraft.version, SaveManager.currentFormat, seed);
		this.currentSave.isDedicated = dedicated;
		if(!SaveManager.saveWorld(this, currentSave)){
			doneGenerating = false;
			//centralManager.renderSplashText("ERROR", "There was an error saving.");
			while(true){}
		}
		this.worldTime = Constants.dayLengthMS/2;
		this.gameTime = new GameTime(this.worldTime);
		this.currentWeather = new WeatherState(WeatherType.CLEAR, this);
		this.weatherTransitionCountdown = 0;
		this.sendBlockPackets = true;
		if(!GLCraft.getGLCraft().isServer()) this.centralManager.getServer().setBroadcast(saveName);
		int x = (int) (Constants.CHUNKSIZE*(Constants.worldLengthChunks/2f));
		int z = (int) (Constants.CHUNKSIZE*(Constants.worldLengthChunks/2f));
		int y = Constants.CHUNKSIZE*Constants.worldLengthChunks+1;
		while(Tile.getTile((byte) getTileAtPos(x, y-1, z)).canPassThrough() || Tile.getTile((byte) getTileAtPos(x, y-1, z)) == Tile.Void){y--;}
		centralManager.getServer().spawnPos = new Vector3f(x,y,z);
	}
	
	public void showMessage(double seconds, String message){
		centralManager.showMessage(seconds, message);
	}
	
	public void worldFromBuf(){
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					currentChunk++;
					Chunk c = new Chunk(shader, waterShader, CentralManager.MIXEDCHUNK, x * Constants.CHUNKSIZE, 0, z * Constants.CHUNKSIZE, this, true);
					activeChunks.put(new Vector3i(c.getPos()), c);
					c.rebuild();
				}
			}
		}
		doneGenerating = true;
	}
	
	public void queueAction(Callable<Void> action){
		actionQueue.add(action);
	}
	
	public void update(){
		while(!actionQueue.isEmpty())
			try {
				actionQueue.poll().call();
			} catch (Exception e) {
				e.printStackTrace();
			}

		if(!isServer && queueRelight)
			new Thread(() -> relightMain()).start();
		
		DebugTimer.startTimer("entity_update");
		entityManager.update();
		DebugTimer.endTimer("entity_update");
		
		DebugTimer.startTimer("chunk_update");
		Iterator<Chunk> i = activeChunks.values().iterator();
		while(i.hasNext()){
			i.next().update();
		}
		DebugTimer.endTimer("chunk_update");
		
		if(this.isServer){
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

			if(currentWeather.isWeatherOver())
				changeWeather();
		}

		loadUnload();

		if(this.isServer){
			if(!centralManager.getServer().chunkQueue.isEmpty()) {
				PacketRequestChunks p = centralManager.getServer().chunkQueue.poll();
				//Shouldn't need this if statement, but apparently I do.
				if(p != null) {
					Vector2i pos = p.indivPos;
					ArrayList<Chunk> chunks = new ArrayList<Chunk>();
					// Chunk c;
					boolean failed = false;
					for (int y = 0; y < Constants.worldLengthChunks && !failed; y++) {
						chunks.add(getChunk(new Vector3i(pos.x, y * 16, pos.y)));
						if (chunks.get(chunks.size() - 1) == null)
							failed = true;
					}
					if (failed)
						centralManager.getServer().chunkQueue.add(p);
					else
						sendPacket(new PacketSendChunks(chunks), p.client.player);
				}
			}
		}

		worldTime+=Time.getDelta()*1000;
		gameTime.updateTime(worldTime);
		if(weatherTransitionCountdown > 0) weatherTransitionCountdown -= Time.getDelta(); else weatherTransitionCountdown = 0;
	}

	@SuppressWarnings("unchecked")
	private void loadUnload(){
		playerChunksNew.clear();
		Vector2i chunkPos;

		if(this.isServer) {
			for (Server.ServerClient c : centralManager.getServer().getClients()) {
				if (!(chunkPos = posToChunkPos2i(c.player.getPos())).equals(c.player.chunkPos)) {
					c.player.playerChunks.clear();
					c.player.playerChunks.addAll(getChunkPosInRadiusOfPlayer(c.player, Constants.LOAD_DISTANCE));
					c.player.chunkPos = chunkPos;
				}
			}
			for (Server.ServerClient c : centralManager.getServer().getClients())
				playerChunksNew.addAll(c.player.playerChunks);
		}else if(centralManager.getClient().connectionState.connected){
			if (!(chunkPos = posToChunkPos2i(getPlayer().getPos())).equals(getPlayer().chunkPos)) {
				getPlayer().playerChunks.clear();
				getPlayer().playerChunks.addAll(getChunkPosInRadiusOfPlayer(getPlayer(), Constants.LOAD_DISTANCE));
				getPlayer().chunkPos = chunkPos;
			}
			playerChunksNew.addAll(getPlayer().playerChunks);
		}

		for(Vector2i c : playerChunks)
			if(!playerChunksNew.contains(c))
				if(!unloadChunks(c)) GLogger.logerr("Chunks at "+c+" were attempted to be unloaded but there is no chunk there.", LogSource.SERVER);
		for(Vector2i c : playerChunksNew)
			if(!playerChunks.contains(c))
				if(!createOrLoadChunks(c)) GLogger.logerr("Chunks at "+c+" were tried to be loaded/created but it is not a valid chunk pos.", LogSource.SERVER);

		if(!isServer)
			if(chunkRequests.size() > 0){
				sendPacket(new PacketRequestChunks(chunkRequests));
				chunkRequests.clear();
			}

		playerChunks = (ArrayList<Vector2i>) playerChunksNew.clone();
	}

	private boolean unloadChunks(Vector2i pos){
		Vector3i vec = new Vector3i(pos.x,0,pos.y);
		Chunk c;
		boolean ret = true;
		for(int y = 0; y < Constants.worldLengthChunks; y++){
			c = activeChunks.get(vec.set(vec.x, y*16, vec.z));
			if(c == null) ret = false;
			activeChunks.remove(vec);
		}
		return ret;
	}

	private boolean createOrLoadChunks(Vector2i pos){
		Chunk c;
		Vector3i v;
		//GLogger.log(pos);
		//GLogger.log("cchunks "+pos);
		if(pos.x % 16 == 0 && pos.y % 16 == 0 && pos.x >= 0 && pos.y >= 0) {
			for (int y = 0; y < Constants.worldLengthChunks; y++) {
				v = new Vector3i(pos.x, y * 16, pos.y);
				if (isServer) {
					if (!activeChunks.containsKey(v)) {
						c = new Chunk(shader, waterShader, 1, new Vector3f(pos.x, y * 16, pos.y), this);
						c.populateChunk();
						activeChunks.put(new Vector3i(pos.x, y * 16, pos.y), c);
					}//else chunk is already loaded
				}else
					activeChunks.put(v, new Chunk(shader, waterShader, new Vector3f(pos.x, y * 16, pos.y), this));
			}
			if(!isServer)
				chunkRequests.add(pos);
		}
		else
			return false;
		return true;
	}
	
	/*private void loadUnload() {
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
			Chunk c = new Chunk(shader, waterShader, 1, cpos, this);
			activeChunks.put(new Vector3i(c.getPos()), c);
			c.rebuild();
			c.populateChunk();
			c.rebuild();
		}
	}*/

	/**
	 * @param player the player
	 * @param chunkRadius radius (in chunks)
	 * @return The 2D (x,z) positions of chunks within chunkRadius*CHUNKSIZE blocks of the player.
	 */
	public List<Vector2i> getChunkPosInRadiusOfPlayer(EntityPlayer player, int chunkRadius){
		int rad = chunkRadius*Constants.CHUNKSIZE, px = (int)player.getX(), pz = (int)player.getZ();
		ArrayList<Vector2i> ret = new ArrayList<Vector2i>();
		Vector2i vec = new Vector2i(0,0);
		for(int x = px-rad; x < px+rad; x+=16)
			for(int z = pz-rad; z < pz+rad; z+=16)
				if(x >= 0 && z >= 0)
					ret.add(posToChunkPos(vec.set(x,z)));
		return ret;
	}

	public void spawnEntity(Entity e){
		entityManager.add(e);
	}
	
	public void render(){
		if(doneGenerating && !this.isServer){
			toRender.clear();
			DebugTimer.startTimer("chunk_render");
			Spritesheet.atlas.bind();
			getEntityManager().getPlayer().applyTranslations();
			// Vector3f pos = getEntityManager().getPlayer().getPos();
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
				i.next().render(false, false);
			}
			
			DebugTimer.startTimer("entity_render");
			entityManager.render();
			DebugTimer.pauseTimer("entity_render");
			
			i = toRender.iterator();
			Chunk c;
			while(i.hasNext()){
				c = i.next();
				c.render(true, false);
				c.render(false, true);
			}
			DebugTimer.endTimer("chunk_render");
			
			float intensity = (getSkyLightIntensity()-0.1f)/0.85f;
			glClearColor(0.0f,0.749019608f*intensity,1.0f*intensity,0.0f);
		}
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
	
	public float getSkyLightIntensity(){
		return getSkyLightIntensity(false);
	}

	public float getSkyLightIntensity(boolean ignoreWeather){
		float ret;
		int mins = this.gameTime.getHours()*60+this.gameTime.getMinutes();
		if(this.gameTime.getHours() >= 4 && this.gameTime.getHours() <= 9)
			ret = ((mins-240)/360f)*0.85f+0.15f;
		else if(this.gameTime.getHours() >= 14 && this.gameTime.getHours() <= 19)
			ret = ((-(mins-840)+360)/360f)*0.85f+0.15f;
		else if(this.gameTime.getHours() < 4 || this.gameTime.getHours() > 19)
			ret = 0.15f;
		else
			ret = 1;
		return ret*(ignoreWeather ? 1 : getWeatherLight());
	}
	
	public void saveChunks(Save save) throws IOException{
		@SuppressWarnings("unchecked")
		ArrayList<Chunk> activeChunks = (ArrayList<Chunk>) new ArrayList<Chunk>(this.activeChunks.values()).clone();
		Iterator<Chunk> i = activeChunks.iterator();
		// int index = 0;
		File f = new File(save.getDirectory(), "region");
		f.mkdirs();
		HashMap<Vector2i,TagCompound> regions = new HashMap<Vector2i,TagCompound>();
		while(i.hasNext()){
			Chunk c = i.next();
			Vector2i reg = c.getRegion();
			if(!regions.containsKey(reg)){
				regions.put(reg, new TagCompound(reg.toString()));
			}
			c.save(regions.get(reg));
			//index++;
		}
		Iterator<Entry<Vector2i, TagCompound>> it = regions.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<Vector2i, TagCompound> next = it.next();
	    	FileOutputStream fos = new FileOutputStream(new File(f,"r"+next.getKey().toString().replace("[","").replace("]","").replace(',', '.')+".nbt"));
	    	NbtOutputStream s = new NbtOutputStream(fos);
	    	s.write(next.getValue());
	    	s.close();
	    	fos.close();
	    }
	}
	
	public void loadChunks(Save s) throws IOException{
		File regionsFile = new File(s.getDirectory(),"region");
		String[] files = regionsFile.list();
		for(String file : files){
			FileInputStream fis = new FileInputStream(new File(regionsFile,file));
			NbtInputStream in = new NbtInputStream(fis);
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
			in.close();
			fis.close();
		}
		
		Iterator<Chunk> i = this.activeChunks.values().iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			c.rebuildBase(true, false);
			c.rebuildBase(false, false);
			c.rebuildBase(false, true);
			c.rebuildTickTiles();
		}
			
	}
	
	public void reSunlight(Vector2i pos) {
		new Thread(()->{
			for(int x = pos.x; x < pos.x+Constants.CHUNKSIZE; x++)
				for(int z = pos.y; z < pos.y+Constants.CHUNKSIZE; z++){
					sunlightQueue.add(new Light(new Vector3i(x,Constants.worldLength-1,z)));
					setSunlight(x,Constants.worldLength-1,z,15,false);
				}
			relight();
		}).start();
	}

	public ArrayList<AABB> BlockAABBForEntity(EntitySolid entitySolid){
		ArrayList<AABB> arraylist = new ArrayList<AABB>();
		AABB mAABB = entitySolid.getAABB();
		for(int x = (int) (entitySolid.getX()-mAABB.getSize().x*3); x < entitySolid.getX()+mAABB.getSize().x; x++){
			for(int y = (int) (entitySolid.getY()-mAABB.getSize().y*3); y < entitySolid.getY()+mAABB.getSize().y+1; y++){
				for(int z = (int) (entitySolid.getZ()-mAABB.getSize().z*3); z < entitySolid.getZ()+mAABB.getSize().z; z++){
					if(!Tile.getTile((byte)getTileAtPos(x,y,z)).canPassThrough()){
						AABB aabb = Tile.getTile((byte)getTileAtPos(x,y,z)).getAABB(x,y,z,getMetaAtPos(x,y,z),this);
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
		if(c != null){
			c.setTileAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z, tile, meta, source, true);
			if(this.isServer){
				blockUpdate(x,y,z);
				blockUpdate(x+1,y,z);
				blockUpdate(x-1,y,z);
				blockUpdate(x,y+1,z);
				blockUpdate(x,y-1,z);
				blockUpdate(x,y,z+1);
				blockUpdate(x,y,z-1);
			}
			if(sendBlockPackets && source.sendPacket)
				this.sendPacket(new PacketBlockChange(x,y,z,tile,meta,source));
		}
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
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean sendPacket, boolean rebuild){
		setMetaAtPos(x,y,z,meta,rebuild,sendPacket,true,true);
	}
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean rebuild){
		setMetaAtPos(x,y,z,meta,rebuild,true,true,true);
	}
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean rebuild, boolean blockUpdate, boolean updateSelf){
		setMetaAtPos(x,y,z,meta,rebuild,true,blockUpdate,updateSelf);
	}
	
	public void setMetaAtPos(int x, int y, int z, byte meta, boolean rebuild, boolean sendPacket, boolean blockUpdate, boolean updateSelf){
		Chunk c = getChunk(x,y,z);
		c.setMetaAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z,meta, rebuild);
		if(this.isServer && blockUpdate){
			if(updateSelf) blockUpdate(x,y,z);
			blockUpdate(x+1,y,z);
			blockUpdate(x-1,y,z);
			blockUpdate(x,y+1,z);
			blockUpdate(x,y-1,z);
			blockUpdate(x,y,z+1);
			blockUpdate(x,y,z-1);
		}
		if(sendPacket && sendBlockPackets)
			this.sendPacket(new PacketBlockChange(x,y,z,meta));
	}
	
	public void blockUpdate(int x, int y, int z){
		Chunk c = getChunk(x,y,z);
		if(c != null){
			c.blockUpdate(x, y, z);
		}
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
		return activeChunks.get(posToChunkPos(pos));
	}

	//Clamps pos to the chunk it's in
	public Vector3i posToChunkPos(Vector3i pos){
		Vector3i vector3i = new Vector3i(pos);
		vector3i.x = (int) (Math.floor(vector3i.x/16f)*16);
		vector3i.y = (int) (Math.floor(vector3i.y/16f)*16);
		vector3i.z = (int) (Math.floor(vector3i.z/16f)*16);
		return vector3i;
	}

	//Clamps pos to the chunk it's in
	public Vector2i posToChunkPos(Vector2i pos){
		Vector2i vector2i = new Vector2i(pos);
		vector2i.x = (int) (Math.floor(vector2i.x/16f)*16);
		vector2i.y = (int) (Math.floor(vector2i.y/16f)*16);
		return vector2i;
	}

	public Vector2i posToChunkPos2i(Vector3f pos) {
		Vector2i vector2i = new Vector2i(pos.x, pos.z);
		vector2i.x = (int) (Math.floor(vector2i.x/16f)*16);
		vector2i.y = (int) (Math.floor(vector2i.y/16f)*16);
		return vector2i;
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
		this.sendBlockPackets = false;
		this.currentSave = s;
		//centralManager.initSplashText();
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int y = 0; y < Constants.worldLengthChunks; y++){
				for(int z = 0; z < Constants.worldLengthChunks; z++){
					Chunk c = new Chunk(shader, waterShader, new Vector3f(x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE), this);
					activeChunks.put(new Vector3i(c.getPos()), c);
				}
			}
		}
		this.worldTime = s.worldTime;
		this.gameTime = new GameTime(s.worldTime);
		boolean success = SaveManager.loadWorld(this, s);
		this.currentWeather = s.weatherState;
		this.weatherTransitionCountdown = 0;
		this.sendBlockPackets = true;
		if(success){
			int x = (int) (Constants.CHUNKSIZE*(Constants.worldLengthChunks/2f));
			int z = (int) (Constants.CHUNKSIZE*(Constants.worldLengthChunks/2f));
			int y = Constants.CHUNKSIZE*Constants.worldLengthChunks+1;
			while(Tile.getTile((byte) getTileAtPos(x, y-1, z)).canPassThrough() || Tile.getTile((byte)getTileAtPos(x, y-1, z)) == Tile.Void){y--;}
			centralManager.getServer().spawnPos = new Vector3f(x,y,z);
			this.doneGenerating = true;
			if(!GLCraft.getGLCraft().isServer()) this.centralManager.getServer().setBroadcast(s.dispName);
			long seed = currentSave.seed;
			elevationNoise = new OpenSimplexNoise(seed*2);
			roughnessNoise = new OpenSimplexNoise(seed);
			detailNoise = new OpenSimplexNoise(seed/2);
			return true;
		}else{
			GLogger.logerr("Error loading world", LogSource.SERVER);
			this.isServer = true;
			closeWorldNow("Error");
			return false;
		}
	}

	/**
	 * Saves the world. THE SAVING IS DONE IN ANOTHER THREAD, SO USE saveWorldBlocking if you wish to block until the world has been saved.
	 */
	public static boolean saveWorld() {
		if(cw != null){
			if(!cw.isSaving() && cw.doneGenerating){
				return SaveManager.saveWorld(cw, cw.currentSave);
			}
		}
		return false;
	}
	
	public static boolean saveWorldBlocking(){
		boolean b = saveWorld();
		if(b)
			while(cw.saving){try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}}
		return b;
	}

	public int getMetaAtPos(float x, float y, float z) {
		return getMetaAtPos((int)x,(int)y,(int)z);
	}

	public void setSaving(boolean saving) {
		this.saving = saving;
		if(isServer)
			if(saving)
				sendPacket(new PacketMessage("SAVE|start"));
			else
				sendPacket(new PacketMessage("SAVE|end"));
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
		    	saveWorld();
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
		float ret = ((float)getBlockLight(x, y, z)+(float)getSunlight(x, y, z)*getSkyLightIntensity())/15f;
		if(ret > 1)
			ret = 1;
		return ret;
	}

	public Chunk getChunk(Vector3f pos) {
		return getChunk(new Vector3i(pos));
	}

	public void relight(){
		queueRelight = true;
	}

	private synchronized void relightMain(){
		queueRelight = false;
		if(!this.isServer){
			lightRebuildQueue.clear();
			while (!lightRemovalQueue.isEmpty()) {
				LightRemoval next = lightRemovalQueue.poll();
				Vector3i npos = next.pos;
				Vector3i pos = new Vector3i(npos.x + 1, npos.y, npos.z);
				evalLightRemoval(npos, pos, next);
				pos = new Vector3i(npos.x - 1, npos.y, npos.z);
				evalLightRemoval(npos, pos, next);
				pos = new Vector3i(npos.x, npos.y + 1, npos.z);
				evalLightRemoval(npos, pos, next);
				pos = new Vector3i(npos.x, npos.y - 1, npos.z);
				evalLightRemoval(npos, pos, next);
				pos = new Vector3i(npos.x, npos.y, npos.z + 1);
				evalLightRemoval(npos, pos, next);
				pos = new Vector3i(npos.x, npos.y, npos.z - 1);
				evalLightRemoval(npos, pos, next);
			}
			while (!lightQueue.isEmpty()) {
				Light nextl = lightQueue.poll();
				Vector3i next = nextl.pos;
				Vector3i pos = new Vector3i(next.x + 1, next.y, next.z);
				evalLight(nextl, pos);
				pos = new Vector3i(next.x - 1, next.y, next.z);
				evalLight(nextl, pos);
				pos = new Vector3i(next.x, next.y + 1, next.z);
				evalLight(nextl, pos);
				pos = new Vector3i(next.x, next.y - 1, next.z);
				evalLight(nextl, pos);
				pos = new Vector3i(next.x, next.y, next.z + 1);
				evalLight(nextl, pos);
				pos = new Vector3i(next.x, next.y, next.z - 1);
				evalLight(nextl, pos);
			}
			while (!sunlightRemovalQueue.isEmpty()) {
				LightRemoval next = sunlightRemovalQueue.poll();
				Vector3i npos = next.pos;
				this.setSunlight(npos.x, npos.y, npos.z, 0, false);
				Vector3i pos = new Vector3i(npos.x + 1, npos.y, npos.z);
				evalSunlightRemoval(npos, pos, next, false);
				pos = new Vector3i(npos.x - 1, npos.y, npos.z);
				evalSunlightRemoval(npos, pos, next, false);
				pos = new Vector3i(npos.x, npos.y + 1, npos.z);
				evalSunlightRemoval(npos, pos, next, false);
				pos = new Vector3i(npos.x, npos.y - 1, npos.z);
				evalSunlightRemoval(npos, pos, next, true);
				pos = new Vector3i(npos.x, npos.y, npos.z + 1);
				evalSunlightRemoval(npos, pos, next, false);
				pos = new Vector3i(npos.x, npos.y, npos.z - 1);
				evalSunlightRemoval(npos, pos, next, false);
			}
			while (!sunlightQueue.isEmpty()) {
				Light nextl = sunlightQueue.poll();
				Vector3i next = nextl.pos;
				Vector3i pos = new Vector3i(next.x + 1, next.y, next.z);
				evalSunlight(nextl, pos, false);
				pos = new Vector3i(next.x - 1, next.y, next.z);
				evalSunlight(nextl, pos, false);
				pos = new Vector3i(next.x, next.y + 1, next.z);
				evalSunlight(nextl, pos, false);
				pos = new Vector3i(next.x, next.y - 1, next.z);
				evalSunlight(nextl, pos, true);
				pos = new Vector3i(next.x, next.y, next.z + 1);
				evalSunlight(nextl, pos, false);
				pos = new Vector3i(next.x, next.y, next.z - 1);
				evalSunlight(nextl, pos, false);
			}
			for(Chunk c : lightRebuildQueue){
				c.rebuild();
			}
			lightRebuildQueue.clear();
		}else{
			this.sunlightQueue.clear();
			this.sunlightRemovalQueue.clear();
			this.lightQueue.clear();
			this.lightRemovalQueue.clear();
		}
	}
	
	private void evalSunlight(Light next, Vector3i dest, boolean downwards) {
		int bl, dbl;
		if(next.chunk != null){
			bl = next.chunk.getSunlight(next.pos.x, next.pos.y, next.pos.z);
			Chunk c = getChunk(dest.x, dest.y, dest.z);
			if(c != null){
				dbl = c.getSunlight(dest.x, dest.y, dest.z);
				if(dbl < bl-1 || (downwards  && bl == 15)){
					sunlightQueue.add(new Light(dest, c));
					byte tra = Tile.getTile((byte)getTileAtPos(dest.x, dest.y, dest.z)).getTransparency();
					byte res = 0;
					if(bl == 15 && downwards && tra <= 1)
						tra -= 1;
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
			this.chunk = GLCraft.getGLCraft().getWorldManager(false).getChunk(pos);
		}
	}

	public void rebuildNextChunk() {
		ArrayList<Chunk> activeChunks = new ArrayList<Chunk>(this.activeChunks.values());
		if(activeChunks.size() > 0){
			if(currentRebuild >= activeChunks.size()) currentRebuild = 0;
			Chunk c = activeChunks.get(currentRebuild);
			while(!c.isVisible()){
				currentRebuild++;
				if(currentRebuild >= activeChunks.size()){
					currentRebuild = 0;
					break;
				}
				c = activeChunks.get(currentRebuild);
			}
			c.rebuildBase(true, false);
			c.rebuildBase(false, false);
			c.rebuildBase(false, true);
			currentRebuild++;
			currentRebuild%=activeChunks.size();
		}
	}
	
	public HashMap<Vector3i, Chunk> getActiveChunks(){
		return this.activeChunks;
	}
	
	public void sendPacket(Packet p){
		centralManager.sendPacket(p);
	}
	
	public void sendPacket(Packet p, EntityPlayerMP mp){
		centralManager.sendPacket(p,mp);
	}
	
	public EntityPlayer getPlayer() {
		return getEntityManager().getPlayer();
	}
	
	public Entity getEntity(int id){
		return getEntityManager().getEntity(id);
	}

	public void updateChunks(PacketSendChunks psc){
		final PacketSendChunks p = psc;
		// final WorldManager wm = this;
		boolean failed = false;
		for(int i = 0; i < p.pos.length && !failed; i++){
			final int j = i;
			final Chunk c = getChunk(p.pos[j]);
			if(c == null){
				//c = new Chunk(shader, waterShader, p.pos[j].toVector3f(), wm);
				//activeChunks.put(p.pos[j], c);
				GLogger.log("[WARN] A chunk at "+p.pos[j]+" was attempted to be loaded from a packet but did not exist.");
				failed = true;
			}else
				queueAction(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						c.updateTiles(p,j);
						if(c == getChunk(getPlayer().getPos())) getPlayer().shouldUpdate = true;
						return null;
					}
				});
		}
		if(!failed)
			queueAction(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					reSunlight(new Vector2i(p.pos[0].x, p.pos[0].z));
					return null;
				}
			});
	}

	public void setWorldTime(long worldTime) {
		this.worldTime = worldTime;
		if(this.isServer)
			sendPacket(new PacketWorldTime(worldTime));
	}

	public void closeWorld(final String reason, final boolean quit){
		this.actionQueue.add(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				if(cw != null)
					while(cw.saving){Thread.sleep(1);}
				closeWorldMain(reason);
				if(quit)
					System.exit(0);
				return null;
			}
		});
	}
	
	private void closeWorldMain(String reason){
		entityManager.removeAllNow();
		if(this.isServer){
			try {
				this.centralManager.getServer().close(reason);
			} catch (IOException e) {
				GLogger.logerr("Error closing server!", LogSource.SERVER);
				e.printStackTrace();
			}
		}else{
			getPlayer().shouldUpdate = false;
			getPlayer().setSelectedSlot(0);
			centralManager.getClient().close();
			this.doneGenerating = false;
			Constants.GAME_STATE = Constants.START_SCREEN;
			if(!isHost || kicked){
				if(kicked)
					GUIManager.getMainManager().showGUI(new GUIServerError("Kicked from server:\n", reason));
				else
					GUIManager.getMainManager().showGUI(new GUIServerError("Server closed:\n", reason));
			}else{
				GUIManager.getMainManager().clearGUIStack();
				GUIManager.getMainManager().showGUI(new GUIStartScreen());
			}
			if(isHost && kicked)
				GLCraft.getGLCraft().closeLocalServerNow("The host was kicked.");
			kicked = false;
			centralManager.getClient().reinit();
		}
		this.activeChunks.clear();
		System.gc();
	}

	public EntityPlayerMP getPlayer(String name) {
		for(Entity e : getEntityManager().getEntities(EntityPlayerMP.class))
			if(((EntityPlayerMP)e).getName().equalsIgnoreCase(name))
				return (EntityPlayerMP)e;
		return null;
	}

	public void closeWorldNow(String reason) {
		if(cw != null)
			while(cw.saving){try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}}
		closeWorldMain(reason);
	}

	public void setShaderUniform(String uniform, int val){
		GL20.glUniform1i(GL20.glGetUniformLocation(shader.getProgram(), uniform), val);
	}

	public void setShaderUniform(String uniform, float val){
		GL20.glUniform1f(GL20.glGetUniformLocation(shader.getProgram(), uniform), val);
	}

	public void setShaderUniform(String uniform, Vector3f val){
		GL20.glUniform3f(GL20.glGetUniformLocation(shader.getProgram(), uniform), val.x, val.y, val.z);
	}

	public void setShaderUniform(String uniform, Vector2f val){
		GL20.glUniform2f(GL20.glGetUniformLocation(shader.getProgram(), uniform), val.x, val.y);
	}

	public void setShaderUniform(String uniform, Vector4f val){
		GL20.glUniform4f(GL20.glGetUniformLocation(shader.getProgram(), uniform), val.x, val.y, val.z, val.w);
	}

	public float getCloudDensity(){
		return MathUtils.pointAlongValues(previousWeather.type.cloudDensity,currentWeather.type.cloudDensity, (-weatherTransitionCountdown+10)/10f);
	}

	public float getWeatherLight(){
		return MathUtils.pointAlongValues(previousWeather.type.lightMultiplier,currentWeather.type.lightMultiplier, (-weatherTransitionCountdown+10)/10f);
	}

	public float getCloudDarkness(){
		return MathUtils.pointAlongValues(previousWeather.type.cloudDarkness,currentWeather.type.cloudDarkness, (-weatherTransitionCountdown+10)/10f);
	}

	public float getRainOpacity(){
		return MathUtils.pointAlongValues(previousWeather.type.rainOpacity,currentWeather.type.rainOpacity, (-weatherTransitionCountdown+10)/10f);
	}

	public float getWaveSpeed(){
		//return MathUtils.pointAlongValues(previousWeather.type.waveSpeed,currentWeather.type.waveSpeed, (-weatherTransitionCountdown+10)/10f);
		return currentWeather.type.waveSpeed;
	}

	public float getWaveFrequency(){
		//return MathUtils.pointAlongValues(previousWeather.type.waveSpeed,currentWeather.type.waveSpeed, (-weatherTransitionCountdown+10)/10f);
		return currentWeather.type.waveFrequency;
	}

	public float getWaveMultiplier(){
		//return MathUtils.pointAlongValues(previousWeather.type.waveSpeed,currentWeather.type.waveSpeed, (-weatherTransitionCountdown+10)/10f);
		return currentWeather.type.waveMultiplier;
	}

	public void changeWeather(){
		if(isServer) {
			weatherTransitionCountdown = 10f;
			previousWeather = currentWeather;
			currentWeather = WeatherState.random(previousWeather.type, this);
			this.sendPacket(new PacketWeather(currentWeather));
		}
	}

	public void changeWeather(WeatherState weather) {
		previousWeather = currentWeather;
		currentWeather = weather;
		weatherTransitionCountdown = 10f;
		if(isServer)
			this.sendPacket(new PacketWeather(currentWeather));
	}

	public boolean openToSky(int x, int y, int z){
		for(; y < Constants.worldLength; y++)
			if(!Tile.getTile(getTileAtPos(x,y,z)).canPassThrough()) return false;
		return true;
	}

	public boolean openToSky(Vector3f pos){
		return openToSky((int)pos.x, (int)pos.y, (int)pos.z);
	}
	
}
