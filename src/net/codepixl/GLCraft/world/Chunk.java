package net.codepixl.GLCraft.world;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.ShaderProgram;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;


public class Chunk {
	private WorldManager worldManager;
	private Vector3f pos;
	private volatile byte [][][] tiles;
	private volatile int [][][] light;
	private volatile byte [][][] meta;
	private ShaderProgram shader;
	public int vcID, transvcID;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	int type;
	private boolean isActive;
	private int randomUpdateTick = 0;
	private int randomUpdateInterval;
	private boolean needsRebuild = false;
	private ArrayList<Vector3f> tickTiles = new ArrayList<Vector3f>();
	private ArrayList<Vector3f> tempTickTiles = new ArrayList<Vector3f>();
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z, WorldManager w, boolean fromBuf){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL(fromBuf, false);
		init();
	}
	
	//new blank chunk
	public Chunk(ShaderProgram shader, float x, float y, float z, WorldManager w){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		this.type = CentralManager.MIXEDCHUNK;
		initGL(false, true);
		init();
	}
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z,WorldManager w){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL(false, false);
		init();
	}
	
	public Chunk(ShaderProgram shader, int type, Vector3f pos, WorldManager w){
		this.worldManager = w;
		this.pos = pos;
		this.shader = shader;
		this.type = type;
		initGL(false, false);
		init();
	}
	
	private void createChunk(){
		if(type == CentralManager.AIRCHUNK){
			for(int x = (int) pos.getX(); x < sizeX; x++){
				for(int y = (int) pos.getY(); y < sizeY; y++){
					for(int z = (int) pos.getZ(); z < sizeZ; z++){
						tiles[x][y][z] = Tile.Air.getId();
					}
				}
			}
		}else{
			for(int x = 0; x < sizeX; x++){
				for(int y = 0; y < sizeY; y++){
					for(int z = 0; z < sizeZ; z++){
						int posX = (int)pos.x+x;
						int posY = (int)pos.y+y;
						int posZ = (int)pos.z+z;
						//System.out.println(posX+","+posZ);
						float noise = ((float) worldManager.noise.eval((double)posX/50d, (double)posZ/50d) + 1f)*(float)Constants.CHUNKSIZE;
						if(posY < noise){
							if(posY == 0){
								tiles[x][y][z] = Tile.Bedrock.getId();
							}else{
								tiles[x][y][z] = Tile.Stone.getId();
							}
						}else if(posY-1f <= noise){
							if(posY <= Constants.seaLevel+1){
								tiles[x][y][z] = Tile.Sand.getId();
							}else{
								tiles[x][y][z] = Tile.Grass.getId();
							}
						}else{
							if(posY <= Constants.seaLevel){
								tiles[x][y][z] = Tile.Water.getId();
							}else{
								tiles[x][y][z] = Tile.Air.getId();
							}
						}
						/**noise = (float) worldManager.noise.eval((double)posX/2d, (double)posY/2d, (double)posZ/2d);
						if(noise > 0f){
							tiles[x][y][z] = Tile.Air.getId();
						}**/
					}
				}
			}
		}
	}
	
	public void save(String filename) throws IOException{
		NbtOutputStream out = new NbtOutputStream(new FileOutputStream(filename));
		TagCompound t = new TagCompound(""+pos.getX()+","+pos.getY()+","+pos.getZ());
		byte[] buf = new byte[this.sizeX*this.sizeY*this.sizeZ];
		int i = 0;
		for(int x = 0; x < this.sizeX; x++){
			for(int y = 0; y < this.sizeY; y++){
				for(int z = 0; z < this.sizeZ; z++){
					buf[i] = tiles[x][y][z];
					i++;
				}
			}
		}
		TagByteArray tiles = new TagByteArray("Tiles",buf);
		t.setTag(tiles);
		out.write(t);
		out.close();
	}
	
	public void load(String filename) throws IOException{
		NbtInputStream in = new NbtInputStream(new FileInputStream(filename));
		TagCompound t = (TagCompound) in.readTag();
		byte[] buf = t.getByteArray("Tiles");
		int i = 0;
		for(int x = 0; x < this.sizeX; x++){
			for(int y = 0; y < this.sizeY; y++){
				for(int z = 0; z < this.sizeZ; z++){
					tiles[x][y][z] = buf[i];
					i++;
				}
			}
		}
		rebuild();
		rebuildTickTiles();
	}
	
	void populateChunk(){
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					if(tiles[x][y][z] == Tile.Stone.getId()){
						int rand = Constants.rand.nextInt(10000);
						if(rand <= 100){
							tiles[x][y][z] = Tile.CoalOre.getId();
						}else if(rand > 100 && rand <= 150){
							tiles[x][y][z] = Tile.IronOre.getId();
						}else if(rand > 150 && rand <= 160){
							tiles[x][y][z] = Tile.GoldOre.getId();
						}
					}else if(tiles[x][y][z] == Tile.Grass.getId()){
						int rand = Constants.rand.nextInt(100);
						if(rand == 1){
							int randMeta = Constants.rand.nextInt(5);
							createCustomTree(x+(int)pos.x,y+1+(int)pos.y,z+(int)pos.z, Tile.Log, Tile.Leaf,(byte)randMeta,(byte)0,worldManager);
						}
						if(rand > 1 && rand <= 11){
							worldManager.setTileAtPos(x+(int)pos.x, y+1+(int)pos.y, z+(int)pos.z, Tile.TallGrass.getId(), false);
						}
						if(rand > 11 && rand <= 12){
							//worldManager.setTileAtPos(x+(int)pos.x, y+1+(int)pos.y, z+(int)pos.z, Tile.Light.getId(), false);
						}
					}
				}
			}
		}
	}
	
	public void createTree(int x, int y, int z){
		x+=(int)pos.x;
		y+=(int)pos.y;
		z+=(int)pos.z;
		for(int i = 0; i < 5; i++){
			worldManager.setTileAtPos(x, y+i, z, Tile.Log.getId(), false);
		}
		worldManager.setTileAtPos(x, y+3, z+1, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x+1, y+3, z+1, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x-1, y+3, z+1, Tile.Leaf.getId(), false);
		
		worldManager.setTileAtPos(x, y+3, z-1, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x+1, y+3, z-1, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x-1, y+3, z-1, Tile.Leaf.getId(), false);
		
		worldManager.setTileAtPos(x-1, y+3, z, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x+1, y+3, z, Tile.Leaf.getId(), false);
		
		worldManager.setTileAtPos(x, y+4, z+1, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x, y+4, z-1, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x+1, y+4, z, Tile.Leaf.getId(), false);
		worldManager.setTileAtPos(x-1, y+4, z, Tile.Leaf.getId(), false);
		
		worldManager.setTileAtPos(x, y+5, z, Tile.Leaf.getId(), false);
	}
	
	public static void createCustomTree(int x, int y, int z,Tile trunk,Tile leaf,WorldManager w){
		
		for(int i = 0; i < 5; i++){
			w.setTileAtPos(x, y+i, z, trunk.getId(), true);
		}
		w.setTileAtPos(x, y+3, z+1, leaf.getId(), true);
		w.setTileAtPos(x+1, y+3, z+1, leaf.getId(), true);
		w.setTileAtPos(x-1, y+3, z+1, leaf.getId(), true);
		
		w.setTileAtPos(x, y+3, z-1, leaf.getId(), true);
		w.setTileAtPos(x+1, y+3, z-1, leaf.getId(), true);
		w.setTileAtPos(x-1, y+3, z-1, leaf.getId(), true);
		
		w.setTileAtPos(x-1, y+3, z, leaf.getId(), true);
		w.setTileAtPos(x+1, y+3, z, leaf.getId(), true);
		
		w.setTileAtPos(x, y+4, z+1, leaf.getId(), true);
		w.setTileAtPos(x, y+4, z-1, leaf.getId(), true);
		w.setTileAtPos(x+1, y+4, z, leaf.getId(), true);
		w.setTileAtPos(x-1, y+4, z, leaf.getId(), true);
		
		w.setTileAtPos(x, y+5, z, leaf.getId(), true);
	}
	
public static void createCustomTree(int x, int y, int z,Tile trunk,Tile leaf, byte trunkMeta, byte leafMeta, WorldManager w){
		
		for(int i = 0; i < 5; i++){
			w.setTileAtPos(x, y+i, z, trunk.getId(), false, trunkMeta);
		}
		w.setTileAtPos(x, y+3, z+1, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x+1, y+3, z+1, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x-1, y+3, z+1, leaf.getId(), false, leafMeta);
		
		w.setTileAtPos(x, y+3, z-1, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x+1, y+3, z-1, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x-1, y+3, z-1, leaf.getId(), false, leafMeta);
		
		w.setTileAtPos(x-1, y+3, z, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x+1, y+3, z, leaf.getId(), false, leafMeta);
		
		w.setTileAtPos(x, y+4, z+1, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x, y+4, z-1, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x+1, y+4, z, leaf.getId(), false, leafMeta);
		w.setTileAtPos(x-1, y+4, z, leaf.getId(), false, leafMeta);
		
		w.setTileAtPos(x, y+5, z, leaf.getId(), false);
	}
	
	public void initGL(boolean bufChunk, boolean blank){
		sizeX = Constants.CHUNKSIZE;
		sizeY = Constants.CHUNKSIZE;
		sizeZ = Constants.CHUNKSIZE;
		int tmp = glGenLists(1);
		vcID = tmp*2;
		transvcID = vcID+1;
		tiles = new byte[sizeX][sizeY][sizeZ];
		light = new int[sizeX][sizeY][sizeZ];
		meta = new byte[sizeX][sizeY][sizeZ];
		if(!bufChunk && !blank){
			createChunk();
		}else if(!blank && bufChunk){
			createBufChunk();
		}
	}

	private void createBufChunk() {
		
	}

	public void init(){
		
	}
	
	public void render(boolean translucent){
		if(type != CentralManager.AIRCHUNK){
			shader.use();
			GL11.glPolygonOffset(1.0f,1.0f);
			if(translucent)
				glCallList(transvcID);
			else
				glCallList(vcID);
			shader.release();
		}
	}
	
	public void update(){
		if(needsRebuild){
			light();
			rebuild();
			needsRebuild = false;
		}
		Iterator<Vector3f> it = tempTickTiles.iterator();
		while(it.hasNext()){
			Vector3f v = it.next();
			it.remove();
			tickTiles.remove(v);
			Tile t = Tile.getTile(tiles[(int) v.x][(int) v.y][(int) v.z]);
			if(t.needsConstantTick()){
				tickTiles.add(v);
			}
		}
		for(int i = 0; i < 3; i++){
			int x = Constants.rand.nextInt(sizeX);
			int y = Constants.rand.nextInt(sizeY);
			int z = Constants.rand.nextInt(sizeZ);
			Tile.getTile(tiles[x][y][z]).randomTick(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, worldManager);
		}
	}
	
	public void tick(){
		Iterator<Vector3f> i = tickTiles.iterator();
		while(i.hasNext()){
			Vector3f next = i.next();
			float x = next.x;
			float y = next.y;
			float z = next.z;
			Tile t = Tile.getTile(tiles[(int) next.x][(int) next.y][(int) next.z]);
			if(t.tickRate() > 1){
				if(Tile.tickMap.get(t) == null){
					Tile.tickMap.put(t, new TickHelper(t.tickRate()));
				}
				if(Tile.tickMap.get(t).needsTick()){
					t.tick((int)(x+pos.x), (int)(y+pos.y), (int)(z+pos.z), worldManager);
				}
				Tile.tickMap.get(t).cycle();
			}else{
				t.tick((int)(x+pos.x), (int)(y+pos.y), (int)(z+pos.z), worldManager);
			}
		}
	}
	
	public void rebuildTickTiles(){
		tickTiles.clear();
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					if(Tile.getTile(tiles[x][y][z]).needsConstantTick()){
						queueTickTileUpdate(x,y,z);
					}
				}
			}
		}
	}
	
	public boolean isTickTile(int x, int y, int z){
		return tickTiles.contains(new Vector3f(x,y,z));
	}
	
	public void rebuild(){
		rebuildBase(false);
		rebuildBase(true);
	}
	
	private void rebuildBase(boolean translucent){
		if(type != CentralManager.AIRCHUNK){
			if(translucent)
				glNewList(transvcID, GL_COMPILE);
			else
				glNewList(vcID, GL_COMPILE);
			for(int x = 0; x < sizeX; x++){
				for(int y = 0; y < sizeY; y++){
					for(int z = 0; z < sizeZ; z++){
						Tile t = Tile.getTile(tiles[x][y][z]);
						boolean shouldRender = translucent ? t.isTranslucent() : !t.isTranslucent();
						if(shouldRender && t != Tile.Air && !checkTileNotInView(x+(int)pos.getX(),y+(int)pos.getY(),z+(int)pos.getZ())){
							/**if(tiles[x][y][z] != Tile.TallGrass.getId()){
								//System.out.println(Tile.getTile(tiles[x][y][z]).getName());
								//System.out.println(pos);
								Shape.createCube(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
								//System.out.println("Creating "+Tile.getTile(tiles[x][y][z]).getName()+" at "+pos.x+x+","+pos.y+y+","+pos.z+z);
							}else{
								Shape.createCross(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
							}**/
							if(t.getRenderType() == RenderType.CUBE){
								glBegin(GL_QUADS);
								if(t.hasMetaTextures()){
									Shape.createCube(pos.x+x, pos.y+y, pos.z+z, t.getColor(), t.getTexCoords(meta[x][y][z]), 1);
								}else{
									Shape.createCube(pos.x+x, pos.y+y, pos.z+z, t.getColor(), t.getTexCoords(), 1);
								}
								glEnd();
							}else if(t.getRenderType() == RenderType.CROSS){
								glBegin(GL_QUADS);
								if(t.hasMetaTextures()){
									Shape.createCross(pos.x+x, pos.y+y, pos.z+z, t.getColor(), t.getTexCoords(meta[x][y][z]), 1);
								}else{
									Shape.createCross(pos.x+x, pos.y+y, pos.z+z, t.getColor(), t.getTexCoords(), 1);
								}
								glEnd();
							}else if(t.getRenderType() == RenderType.FLAT){
								glBegin(GL_QUADS);
								if(t.hasMetaTextures()){
									Shape.createFlat(pos.x+x, pos.y+y+0.01f, pos.z+z, t.getColor(), t.getTexCoords(meta[x][y][z]), 1);
								}else{
									Shape.createFlat(pos.x+x, pos.y+y+0.01f, pos.z+z, t.getColor(), t.getTexCoords(), 1);
								}
								glEnd();
							}else if(t.getRenderType() == RenderType.CUSTOM){
								t.customRender(pos.x+x, pos.y+y, pos.z+z, worldManager, this);
							}
						}else{
							/**int posX = (int)pos.x+x;
							int posY = (int)pos.y+y;
							int posZ = (int)pos.z+z;
							
							System.out.println("AIR "+posX+","+posY+","+posZ);**/
						}
					}
				}
			}
			glEndList();
		}
	}
	
	private boolean checkTileNotInView(int x, int y, int z){
		boolean facesHidden[] = new boolean[6];
		if(!Tile.getTile((byte)worldManager.getTileAtPos(x-1,y,z)).isTransparent()) facesHidden[0] = true;
		else facesHidden[0] = false;
		if(!Tile.getTile((byte)worldManager.getTileAtPos(x+1,y,z)).isTransparent()) facesHidden[1] = true;
		else facesHidden[1] = false;
		if(!Tile.getTile((byte)worldManager.getTileAtPos(x,y-1,z)).isTransparent()) facesHidden[2] = true;
		else facesHidden[2] = false;
		if(!Tile.getTile((byte)worldManager.getTileAtPos(x,y+1,z)).isTransparent()) facesHidden[3] = true;
		else facesHidden[3] = false;
		if(!Tile.getTile((byte)worldManager.getTileAtPos(x,y,z-1)).isTransparent()) facesHidden[4] = true;
		else facesHidden[4] = false;
		if(!Tile.getTile((byte)worldManager.getTileAtPos(x,y,z+1)).isTransparent()) facesHidden[5] = true;
		else facesHidden[5] = false;
		
		return facesHidden[0] && facesHidden[1] && facesHidden[2] && facesHidden[3] && facesHidden[4] && facesHidden[5];
	}
	
	public byte getTileID(int x, int y, int z){
		if(x < pos.getX() || x > pos.getX() + Constants.CHUNKSIZE || y < pos.getY() || y > pos.getY() + Constants.CHUNKSIZE || z < pos.getZ() || z > pos.getZ() + Constants.CHUNKSIZE){
			return -1;
		}
		return tiles[x][y][z];
	}
	
	public byte getTileAtCoord(int x, int y, int z){
		boolean inBoundsOne = (x >= 0) && (x < tiles.length);
		boolean inBoundsTwo = (y >= 0) && (y < tiles[0].length);
		boolean inBoundsThree = (z >= 0) && (z < tiles[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			return tiles[x][y][z];
		}
		return -1;
	}
	
	public byte getMetaAtCoord(int x, int y, int z){
		boolean inBoundsOne = (x >= 0) && (x < meta.length);
		boolean inBoundsTwo = (y >= 0) && (y < meta[0].length);
		boolean inBoundsThree = (z >= 0) && (z < meta[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			return meta[x][y][z];
		}
		return -1;
	}
	
	public void setMetaAtPos(int x, int y, int z, byte met, boolean rebuild){
		boolean inBoundsOne = (x >= 0) && (x < meta.length);
		boolean inBoundsTwo = (y >= 0) && (y < meta[0].length);
		boolean inBoundsThree = (z >= 0) && (z < meta[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			meta[x][y][z] = met;
			if(rebuild){
				queueLight();
				/**worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay+7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay+7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay-7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay-7, (int)az)).queueLight();**/
				HashSet<Chunk> toRebuild = new HashSet<Chunk>();
				if(x == 0){
					toRebuild.add(worldManager.getChunk(new Vector3f(pos.x-16,pos.y,pos.z)));
				}
				if(y == 0){
					toRebuild.add(worldManager.getChunk(new Vector3f(pos.x,pos.y-16,pos.z)));
				}
				if(z == 0){
					toRebuild.add(worldManager.getChunk(new Vector3f(pos.x,pos.y,pos.z-16)));
				}
				if(x == Constants.CHUNKSIZE-1){
					toRebuild.add( worldManager.getChunk(new Vector3f(pos.x+16,pos.y,pos.z)));
				}
				if(y == Constants.CHUNKSIZE-1){
					toRebuild.add(worldManager.getChunk(new Vector3f(pos.x,pos.y+16,pos.z)));
				}
				if(z == Constants.CHUNKSIZE-1){
					toRebuild.add(worldManager.getChunk(new Vector3f(pos.x,pos.y,pos.z+16)));
				}
				Iterator<Chunk> i = toRebuild.iterator();
				while(i.hasNext()){
					i.next().rebuild();
				}
			}
		}
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild){
		boolean inBoundsOne = (x >= 0) && (x < tiles.length);
		boolean inBoundsTwo = (y >= 0) && (y < tiles[0].length);
		boolean inBoundsThree = (z >= 0) && (z < tiles[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			float ax = x+pos.x;
			float ay = y+pos.y;
			float az = z+pos.z;
			Tile.getTile(tiles[x][y][z]).onBreak((int)ax, (int)ay, (int)az, false, worldManager);
			if(tiles[x][y][z] == Tile.Lamp.getId()){
				this.light[x][y][z] = 0;
			}
			if(tickTiles.contains(new Vector3f(x,y,z)) || Tile.getTile(tile).needsConstantTick()){
				queueTickTileUpdate(x,y,z);
			}
			tiles[x][y][z] = tile;
			Tile.getTile(tile).onPlace((int)ax, (int)ay, (int)az, worldManager);
			//ALWAYS assume that the rebuild argument will be false (except in special cases) because the setting of the meta rebuilds the chunk.
			if(rebuild){
				queueLight();
				/**worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az+7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az-7)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay+7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay+7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay-7, (int)az)).queueLight();
				worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay-7, (int)az)).queueLight();**/
			}
		}
	}
	
	private void queueTickTileUpdate(int x, int y, int z) {
		tempTickTiles.add(new Vector3f(x,y,z));
	}

	private void queueLight() {
		this.light = new int[Constants.CHUNKSIZE][Constants.CHUNKSIZE][Constants.CHUNKSIZE];
		queueRebuild();
	}

	public void queueRebuild() {
		needsRebuild = true;
	}

	public void dispose(){
		shader.dispose();
		glDeleteLists(vcID,1);
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	public void setActive(boolean active){
		this.isActive = active;
	}
	
	public Vector3f getCenter(){
		return new Vector3f(pos.getX() - (Constants.CHUNKSIZE/2), pos.getY() - (Constants.CHUNKSIZE/2), pos.getZ() - (Constants.CHUNKSIZE/2));
	}
	
	public int getType(){
		return type;
	}
	
	public Vector3f getPos(){
		return pos;
	}

	public int getLight(Vector3f posi, boolean ChunkPos) {
		if(ChunkPos){
			if(light.length > posi.x && light[0].length > posi.y && light[0][0].length > posi.z)
				return light[(int)posi.x][(int)posi.y][(int)posi.z];
		}else{
			if(light.length > posi.x-(int)pos.x && light[0].length > posi.y-(int)pos.y && light[0][0].length > posi.z-(int)pos.z)
				return light[(int)posi.x-(int)pos.x][(int)posi.y-(int)pos.y][(int)posi.z-(int)pos.z];
		}
		return 0;
	}

	public void setLight(Vector3f posi, int light, boolean ChunkPos) {
		if(ChunkPos){
			if(this.light.length > posi.x && this.light[0].length > posi.y && this.light[0][0].length > posi.z)
				this.light[(int)posi.x][(int)posi.y][(int)posi.z] = light;
		}else{
			if(this.light.length > posi.x-(int)pos.x && this.light[0].length > posi.y-(int)pos.y && this.light[0][0].length > posi.z-(int)pos.z)
				this.light[(int)posi.x-(int)pos.x][(int)posi.y-(int)pos.y][(int)posi.z-(int)pos.z] = light;
		}
	}

	public void light() {
		/**worldManager.s.addCurrentChunk(1);
		int progress = (int)(((float)worldManager.s.currentChunk()/(float)worldManager.s.total)*33);
		worldManager.s.getSplash().setProgress(progress,"Lighting chunks "+progress+"%");
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					if(tiles[x][y][z] == Tile.Light.getId()){
						worldManager.putLight(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, 10);
					}
				}
			}
		}**/
	}
}
