package net.codepixl.GLCraft.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.Splash;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Frustum;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.PerlinNoise;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.mob.MobManager;
import net.codepixl.GLCraft.world.tile.Tile;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class WorldManager {
	public int loop = 0;
	public Splash s;
	public static float[][] noise;
	public boolean doneGenerating = false;
	public MobManager mobManager;
	private ArrayList<Chunk> activeChunks;
	private ShaderProgram shader;
	public static Vector3f selectedBlock = new Vector3f(0,0,0);
	public World world;
	ArrayList<Vector3f> lights = new ArrayList<Vector3f>(); 
	
	public WorldManager(World w){
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
		mobManager = new MobManager(this);
		activeChunks = new ArrayList<Chunk>();
	}
	
	public void createWorld(){
		System.out.println("Creating Chunks...");
		s = new Splash();
		noise = PerlinNoise.generateSmoothNoise(PerlinNoise.generateWhiteNoise(Constants.viewDistance*Constants.CHUNKSIZE, Constants.viewDistance*Constants.CHUNKSIZE), 101);
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
		/**
		while(i.hasNext()){
			i.next().light();
		}
		**/
		i = activeChunks.iterator();
		while(i.hasNext()){
			i.next().rebuild();
		}
		s.getSplash().splashOff();
		setPlayerPos();
		System.out.println("Done!");
		doneGenerating = true;
	}
	
	public void worldFromBuf(){
		s = new Splash();
		for(int x = 0; x < Constants.viewDistance; x++){
			for(int y = 0; y < Constants.viewDistance; y++){
				for(int z = 0; z < Constants.viewDistance; z++){
					Chunk c = new Chunk(shader, World.MIXEDCHUNK, x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE, this, true);
					activeChunks.add(c);
				}
			}
		}
		s.getSplash().splashOff();
		doneGenerating = true;
	}
	
	private void setPlayerPos(){
		int x = (int)mobManager.getPlayer().getPos().x;
		int y = (int)mobManager.getPlayer().getPos().y;
		int z = (int)mobManager.getPlayer().getPos().z;
		while(getTileAtPos(x,y,z) == 0 || getTileAtPos(x,y,z) == -1){
			y-=1;
		}
		mobManager.getPlayer().getCamera().setPos(new Vector3f(x,y+3,z));
	}
	
	public void update(){
		mobManager.update();
		mobManager.getPlayer().update();
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			i.next().update();
		}
	}
	
	public void render(){
		Spritesheet.tiles.bind();
		getMobManager().getPlayer().getCamera().applyTranslations();
		Vector3f pos = getMobManager().getPlayer().getCamera().getPos();
		if(getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == 0 || getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == -1 || getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == 9 || getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == 4){
			for(int i = 0; i < activeChunks.size(); i++){
				if(Frustum.getFrustum().cubeInFrustum(activeChunks.get(i).getPos().getX(), activeChunks.get(i).getPos().getY(), activeChunks.get(i).getPos().getZ(), activeChunks.get(i).getPos().getX() + Constants.CHUNKSIZE, activeChunks.get(i).getPos().getY() + Constants.CHUNKSIZE, activeChunks.get(i).getPos().getZ() + Constants.CHUNKSIZE)){
					float distance=(float) Math.sqrt(Math.pow(activeChunks.get(i).getCenter().getX()-mobManager.getPlayer().getX(),2) + Math.pow(activeChunks.get(i).getCenter().getY()-mobManager.getPlayer().getY(),2) + Math.pow(activeChunks.get(i).getCenter().getZ()-mobManager.getPlayer().getZ(),2));
					if(distance < 10*Constants.CHUNKSIZE){
						activeChunks.get(i).render();
					}
				}
			}
		}
		//System.out.println(Raytracer.getScreenCenterRay());
		mobManager.render();
	}
	
	public void saveChunk(Chunk c){
		try {
			File dir = new File("world/chunks/");
			dir.mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter("world/chunks/"+c.vcID+"chunk.dat"));
			bw.write(c.getPos().getX()+ " "+c.getPos().getY()+ " "+c.getPos().getZ()+ " ");
			for(int x = (int) c.getPos().getX(); x < c.getPos().getX() + Constants.CHUNKSIZE; x++){
				for(int y = (int) c.getPos().getX(); y < c.getPos().getY() + Constants.CHUNKSIZE; y++){
					for(int z = (int) c.getPos().getZ(); z < c.getPos().getZ() + Constants.CHUNKSIZE; z++){
						bw.write(String.valueOf(c.getTileID(x, y, z)));
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MobManager getMobManager(){
		return mobManager;
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
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x >= c.getPos().x && y >= c.getPos().y && z >= c.getPos().z && x <= c.getPos().x + 15 && y <= c.getPos().y + 15 && z <= c.getPos().z + 15){
				c.setTileAtPos(x-(int)c.getPos().x, y-(int)c.getPos().y, z-(int)c.getPos().z,tile, rebuild);
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
		if(pos.x % 16 == 0 && pos.y % 16 == 0 && pos.z % 16 == 0){
			Iterator<Chunk> i = activeChunks.iterator();
			while(i.hasNext()){
				Chunk c = i.next();
				if(c.getPos().equals(pos)){
					return c;
				}
			}
		}
		//System.err.println("Coordinates must be divisible by 15!");
		return activeChunks.get(0);
	}
	
	public int getLight(int x, int y, int z,boolean ChunkCoords){
		Vector3f posi = MathUtils.coordsToChunkPos(x, y, z);
		if(getChunkAtCoords(posi) != null)
		return getChunkAtCoords(posi).getLight(new Vector3f(x,y,z),ChunkCoords);
		return 0;
	}

	public void setLight(int x, int y, int z, int light, boolean ChunkCoords) {
		Vector3f posi = MathUtils.coordsToChunkPos(x, y, z);
		if(getChunkAtCoords(posi) != null)
		getChunkAtCoords(posi).setLight(new Vector3f(x,y,z),light,ChunkCoords);

	}
	
	public void putLight(int x, int y, int z, int light)
	 {
	     int xDecreasing = x - 1;
	     int xIncreasing = x + 1;
	     int yDecreasing = y - 1;
	     int yIncreasing = y + 1;
	     int zDecreasing = z - 1;
	     int zIncreasing = z + 1;
	     if (light > 0)
	     {
	         light--;
	         WorldManager world = this;
	         world.setLight(x, y, z, (int)light, false);
	         
	         if (world.getLight(x, yDecreasing, z, false) < light &&
	             Tile.getTile((byte)world.getTileAtPos(x, yDecreasing, z)).isTransparent())
	             putLight(x, yDecreasing, z, light);
	         if (world.getLight(x, yIncreasing, z, false) < light &&
	             Tile.getTile((byte)world.getTileAtPos(x, yIncreasing, z)).isTransparent())
	        	 putLight(x, yIncreasing, z, light);
	         if (world.getLight(xDecreasing, y, z , false) < light &&
	             Tile.getTile((byte)world.getTileAtPos(xDecreasing, y, z)).isTransparent())
	        	 putLight(xDecreasing, y, z, light);
	         if (world.getLight(xIncreasing, y, z, false) < light &&
	             Tile.getTile((byte) world.getTileAtPos(xIncreasing, y, z)).isTransparent())
	        	 putLight(xIncreasing, y, z, light);
	         if (world.getLight(x, y, zDecreasing, false) < light &&
	             Tile.getTile((byte) world.getTileAtPos(x, y, zDecreasing)).isTransparent())
	        	 putLight(x, y, zDecreasing, light);
	         if (world.getLight(x, y, zIncreasing, false) < light &&
	             Tile.getTile((byte)world.getTileAtPos(x, y, zIncreasing)).isTransparent())
	        	 putLight(x, y, zIncreasing, light);
	     }
	 }
	
}
