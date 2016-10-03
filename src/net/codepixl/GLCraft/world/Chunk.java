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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.nishu.utils.Color4f;
import com.nishu.utils.ShaderProgram;

import net.codepixl.GLCraft.network.packet.PacketSendChunk;
import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.EnumFacing;
import net.codepixl.GLCraft.util.Utils;
import net.codepixl.GLCraft.util.Vector2i;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager.Light;
import net.codepixl.GLCraft.world.WorldManager.LightRemoval;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.ore.TileOre;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;


public class Chunk {
	private WorldManager worldManager;
	private Vector3f pos;
	private volatile byte [][][] tiles;
	private volatile byte [][][] light;
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
	private ArrayList<Vector3f> scheduledBlockUpdates = new ArrayList<Vector3f>();
	private boolean visible = false;
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z, WorldManager w, boolean fromBuf){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL(fromBuf, false);
		init(false, false);
	}
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z,WorldManager w){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL(false, false);
		init(false, false);
	}
	
	public Chunk(ShaderProgram shader, int type, Vector3f pos, WorldManager w){
		this.worldManager = w;
		this.pos = pos;
		this.shader = shader;
		this.type = type;
		initGL(false, false);
		init(false, false);
	}
	
	public Chunk(ShaderProgram shader, Vector3f pos, WorldManager w){
		this.worldManager = w;
		this.pos = pos;
		this.shader = shader;
		this.type = CentralManager.MIXEDCHUNK;
		initGL(false, true);
		init(false, true);
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
				for(int z = 0; z < sizeZ; z++){
					int posZ = (int)pos.z+z;
					int posX = (int)pos.x+x;
					float elevation = (float) worldManager.elevationNoise.eval((double)posX/300d, (double)posZ/300d);
					float roughness = (float) worldManager.roughnessNoise.eval((double)posX/300d, (double)posZ/300d);
					float detail = (float) worldManager.detailNoise.eval((double)posX/75d, (double)posZ/75d);
					for(int y = 0; y < sizeY; y++){
						int posY = (int)pos.y+y;
						//System.out.println(posX+","+posZ);
						float noise = (elevation + (roughness*detail))*60f+64f;
						if(posY < noise){
							if(posY == 0){
								tiles[x][y][z] = Tile.Bedrock.getId();
							}else{
								tiles[x][y][z] = Tile.Stone.getId();
							}
						}else if(posY-3f <= noise){
							if(posY <= Constants.seaLevel+1){
								tiles[x][y][z] = Tile.Sand.getId();
							}else{
								if(posY-2f <=noise){
									tiles[x][y][z] = Tile.Dirt.getId();
								}else{
									tiles[x][y][z] = Tile.Grass.getId();
								}
							}
						}else{
							if(posY <= Constants.seaLevel){
								tiles[x][y][z] = Tile.Water.getId();
							}else{
								tiles[x][y][z] = Tile.Air.getId();
							}
						}
						/*noise = (float) worldManager.noise.eval((double)posX/20d, (double)posY/20d, (double)posZ/20d);
						if(noise > 0f && y != 0){
							tiles[x][y][z] = Tile.Air.getId();
						}*/
					}
				}
			}
		}
	}
	
	public void save(TagCompound reg) throws IOException{
		TagCompound t = new TagCompound("chunk"+this.pos.toString().replace("Vector3f", ""));
		byte[] tbuf = new byte[this.sizeX*this.sizeY*this.sizeZ];
		byte[] mbuf = new byte[this.sizeX*this.sizeY*this.sizeZ];
		int i = 0;
		for(int x = 0; x < this.sizeX; x++){
			for(int y = 0; y < this.sizeY; y++){
				for(int z = 0; z < this.sizeZ; z++){
					tbuf[i] = tiles[x][y][z];
					mbuf[i] = meta[x][y][z];
					i++;
				}
			}
		}
		TagByteArray tiles = new TagByteArray("tiles",tbuf);
		TagByteArray meta = new TagByteArray("meta",mbuf);
		TagCompound pos = new TagCompound("pos");
		TagFloat x = new TagFloat("x",this.pos.x);
		TagFloat y = new TagFloat("y",this.pos.y);
		TagFloat z = new TagFloat("z",this.pos.z);
		pos.setTag(x);
		pos.setTag(y);
		pos.setTag(z);
		t.setTag(pos);
		t.setTag(tiles);
		t.setTag(meta);
		reg.setTag(t);
	}
	
	public void load(TagCompound t) throws IOException{
		TagCompound pos = t.getCompound("pos");
		this.pos.x = pos.getFloat("x");
		this.pos.y = pos.getFloat("y");
		this.pos.z = pos.getFloat("z");
		byte[] tbuf = t.getByteArray("tiles");
		byte[] mbuf = t.getByteArray("meta");
		int i = 0;
		for(int x = 0; x < this.sizeX; x++){
			for(int y = 0; y < this.sizeY; y++){
				for(int z = 0; z < this.sizeZ; z++){
					tiles[x][y][z] = tbuf[i];
					meta[x][y][z] = mbuf[i];
					if(Tile.getTile(tiles[x][y][z]).getLightLevel(meta[x][y][z]) > 0){
						worldManager.lightQueue.add(new Light(new Vector3i(x+(int)this.pos.x,y+(int)this.pos.y,z+(int)this.pos.z),this));
						this.light[x][y][z] = Tile.getTile(tiles[x][y][z]).getLightLevel(meta[x][y][z]);
					}
					i++;
				}
			}
		}
	}
	
	public Vector2i getRegion(){
		return new Vector2i((int)Math.floor(this.pos.x/16f/32f),(int)Math.floor(this.pos.z/16f/32f));
	}
	
	void populateChunk(){
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					if(tiles[x][y][z] == Tile.Stone.getId()){
						TileOre o = TileOre.ores.get(Constants.rand.nextInt(TileOre.ores.size()-1)+1);
						float rand = Constants.randFloat(0, 1);
						if(rand <= o.getRareness() && y+(int)pos.y <= o.getMaxHeight() && y+(int)pos.y >= o.getMinHeight()){
							o.spawnVein(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, worldManager);
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
		if(!this.worldManager.isServer){
			int tmp = glGenLists(1);
			vcID = tmp*2;
			transvcID = vcID+1;
		}
	}

	private void createBufChunk() {
		
	}

	public void init(boolean bufChunk, boolean blank){
		sizeX = Constants.CHUNKSIZE;
		sizeY = Constants.CHUNKSIZE;
		sizeZ = Constants.CHUNKSIZE;
		tiles = new byte[sizeX][sizeY][sizeZ];
		light = new byte[sizeX][sizeY][sizeZ];
		meta = new byte[sizeX][sizeY][sizeZ];
		if(!bufChunk && !blank){
			createChunk();
		}else if(!blank && bufChunk){
			createBufChunk();
		}
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
			worldManager.relight();
			rebuildBase(true);
			rebuildBase(false);
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
	}
	
	
	
	private boolean inBounds(int x, int y, int z){
		return x < Constants.CHUNKSIZE && x >= 0 && y < Constants.CHUNKSIZE && y >= 0 && z < Constants.CHUNKSIZE && z >= 0;
	}
	
	public void tick(){
		Iterator<Vector3f> it = tickTiles.iterator();
		while(it.hasNext()){
			Vector3f next = it.next();
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
		
		for(int i = 0; i < 3; i++){
			int x = Constants.rand.nextInt(sizeX);
			int y = Constants.rand.nextInt(sizeY);
			int z = Constants.rand.nextInt(sizeZ);
			Tile.getTile(tiles[x][y][z]).randomTick(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, worldManager);
		}
		
		it = new ArrayList<Vector3f>(scheduledBlockUpdates).iterator();
		while(it.hasNext()){
			Vector3f tmp = it.next();
			Vector3i v = new Vector3i(tmp);
			scheduledBlockUpdates.remove(tmp);
			Vector3i local = new Vector3i(v.x-getPos().x, v.y-getPos().y, v.z-getPos().z);
			Tile.getTile(tiles[local.x][local.y][local.z]).blockUpdate(v.x, v.y, v.z, worldManager);
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
		queueRebuild();
	}
	
	protected void rebuildBase(boolean translucent){
		if(type != CentralManager.AIRCHUNK){
							
			if(translucent)
				glNewList(transvcID, GL_COMPILE);
			else{
				glNewList(vcID, GL_COMPILE);
				visible = false;
			}
			glBegin(GL_QUADS);
			for(int x = 0; x < sizeX; x++){
				for(int y = 0; y < sizeY; y++){
					for(int z = 0; z < sizeZ; z++){
						if(!Tile.tileMap.containsKey(tiles[x][y][z]))
							tiles[x][y][z] = Tile.Air.getId();
						Tile t = Tile.getTile(tiles[x][y][z]);
						boolean shouldRender = translucent ? t.isTranslucent() : !t.isTranslucent();
						if(shouldRender && t != Tile.Air && !checkTileNotInView(x+(int)pos.getX(),y+(int)pos.getY(),z+(int)pos.getZ())){
							if(!translucent)
								visible = true;
							float[] light = new float[]{
								getLightIntensity(x,y-1,z),
								getLightIntensity(x,y+1,z),
								getLightIntensity(x,y,z-1),
								getLightIntensity(x,y,z+1),
								getLightIntensity(x+1,y,z),
								getLightIntensity(x-1,y,z)
							};
							Color4f[] col = new Color4f[]{
									new Color4f(t.getColor().r*light[0],t.getColor().g*light[0],t.getColor().b*light[0],t.getColor().a),
									new Color4f(t.getColor().r*light[1],t.getColor().g*light[1],t.getColor().b*light[1],t.getColor().a),
									new Color4f(t.getColor().r*light[2],t.getColor().g*light[2],t.getColor().b*light[2],t.getColor().a),
									new Color4f(t.getColor().r*light[3],t.getColor().g*light[3],t.getColor().b*light[3],t.getColor().a),
									new Color4f(t.getColor().r*light[4],t.getColor().g*light[4],t.getColor().b*light[4],t.getColor().a),
									new Color4f(t.getColor().r*light[5],t.getColor().g*light[5],t.getColor().b*light[5],t.getColor().a),
							};
							/**if(tiles[x][y][z] != Tile.TallGrass.getId()){
								//System.out.println(Tile.getTile(tiles[x][y][z]).getName());
								//System.out.println(pos);
								Shape.createCube(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
								//System.out.println("Creating "+Tile.getTile(tiles[x][y][z]).getName()+" at "+pos.x+x+","+pos.y+y+","+pos.z+z);
							}else{
								Shape.createCross(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
							}**/
							if(t.getRenderType() == RenderType.CUBE){
								float[] texCoords;
								if(t.hasMetaTextures())
									texCoords = t.getTexCoords(meta[x][y][z]);
								else
									texCoords = t.getTexCoords();
								if(t.metaRotate()){
									switch(meta[x][y][z]){
										case 0:
											break;
										case 1:
											texCoords = new float[]{texCoords[0],texCoords[1],texCoords[2],texCoords[3],texCoords[10],texCoords[11],texCoords[4],texCoords[5],texCoords[6],texCoords[7],texCoords[8],texCoords[9]};
											break;
										case 2:
											texCoords = new float[]{texCoords[0],texCoords[1],texCoords[2],texCoords[3],texCoords[6],texCoords[7],texCoords[8],texCoords[9],texCoords[10],texCoords[11],texCoords[4],texCoords[5]};
											break;
										case 3:
											texCoords = new float[]{texCoords[0],texCoords[1],texCoords[2],texCoords[3],texCoords[8],texCoords[9],texCoords[10],texCoords[11],texCoords[4],texCoords[5],texCoords[6],texCoords[7]};
											break;
									}
								}
								Shape.createCube(pos.x+x, pos.y+y, pos.z+z, col, texCoords, 1);
							}else if(t.getRenderType() == RenderType.CROSS){
								if(t.hasMetaTextures()){
									Shape.createCross(pos.x+x, pos.y+y, pos.z+z, col[1], t.getTexCoords(meta[x][y][z]), 1);
								}else{
									Shape.createCross(pos.x+x, pos.y+y, pos.z+z, col[1], t.getTexCoords(), 1);
								}
							}else if(t.getRenderType() == RenderType.FLAT){
								if(t.hasMetaTextures()){
									Shape.createFlat(pos.x+x, pos.y+y+0.01f, pos.z+z, col[1], t.getTexCoords(meta[x][y][z]), 1);
								}else{
									Shape.createFlat(pos.x+x, pos.y+y+0.01f, pos.z+z, col[1], t.getTexCoords(), 1);
								}
							}else if(t.getRenderType() == RenderType.CUSTOM){
								glEnd();
								t.customRender(pos.x+x, pos.y+y, pos.z+z, col, worldManager, this);
								glBegin(GL_QUADS);
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
			glEnd();
			glEndList();
		}
	}
	
	private float getLightIntensity(int x, int y, int z){
		Vector3i pos = new Vector3i(x+(int)this.pos.x,y+(int)this.pos.y,z+(int)this.pos.z);
		float ret = ((float)worldManager.getBlockLight(pos.x, pos.y, pos.z)+(float)worldManager.getSunlight(pos.x, pos.y, pos.z)*worldManager.getSkyLightIntensity())/15f;
		if(ret > 1)
			ret = 1;
		return ret;
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
		if(inBounds)
			return meta[x][y][z];
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
					Chunk c = i.next();
					if(c != null)
						c.rebuild();
				}
			}
		}
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile, byte meta, BreakSource source, boolean rebuild){
		boolean inBoundsOne = (x >= 0) && (x < tiles.length);
		boolean inBoundsTwo = (y >= 0) && (y < tiles[0].length);
		boolean inBoundsThree = (z >= 0) && (z < tiles[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			float ax = x+pos.x;
			float ay = y+pos.y;
			float az = z+pos.z;
			Tile.getTile(tiles[x][y][z]).onBreak((int)ax, (int)ay, (int)az, false, source, worldManager);
			if(tickTiles.contains(new Vector3f(x,y,z)) || Tile.getTile(tile).needsConstantTick()){
				queueTickTileUpdate(x,y,z);
			}
			if(Tile.getTile(tiles[x][y][z]).getLightLevel(this.meta[x][y][z]) > 0){
				byte level = (byte) getBlockLight(x+(int)pos.x,y+(int)pos.y,z+(int)pos.z);
				setBlockLight(x+(int)pos.x,y+(int)pos.y,z+(int)pos.z,0,false);
				removeBlockLight(x+(int)pos.x,y+(int)pos.y,z+(int)pos.z,level);
			}
			tiles[x][y][z] = tile;
			setMetaAtPos(x,y,z,meta,rebuild);
			Vector3i pos = new Vector3i(this.pos.x+x, this.pos.y+y, this.pos.z+z);
			if(Tile.getTile(tile).getLightLevel(meta) > 0)
				setBlockLight(pos.x,pos.y,pos.z,Tile.getTile(tile).getLightLevel(meta),true);
			else if(Tile.getTile(tile).getTransparency() < 15){
				worldManager.lightQueue.add(new Light(new Vector3i(pos.x+1, pos.y, pos.z)));
				worldManager.lightQueue.add(new Light(new Vector3i(pos.x-1, pos.y, pos.z)));
				worldManager.lightQueue.add(new Light(new Vector3i(pos.x, pos.y+1, pos.z)));
				worldManager.lightQueue.add(new Light(new Vector3i(pos.x, pos.y-1, pos.z)));
				worldManager.lightQueue.add(new Light(new Vector3i(pos.x, pos.y, pos.z+1)));
				worldManager.lightQueue.add(new Light(new Vector3i(pos.x, pos.y, pos.z-1)));
				worldManager.sunlightQueue.add(new Light(new Vector3i(pos.x+1, pos.y, pos.z)));
				worldManager.sunlightQueue.add(new Light(new Vector3i(pos.x-1, pos.y, pos.z)));
				worldManager.sunlightQueue.add(new Light(new Vector3i(pos.x, pos.y+1, pos.z)));
				worldManager.sunlightQueue.add(new Light(new Vector3i(pos.x, pos.y-1, pos.z)));
				worldManager.sunlightQueue.add(new Light(new Vector3i(pos.x, pos.y, pos.z+1)));
				worldManager.sunlightQueue.add(new Light(new Vector3i(pos.x, pos.y, pos.z-1)));
			}else{
				this.setSunlight(pos.x, pos.y, pos.z, 0, false);
				this.setBlockLight(pos.x, pos.y, pos.z, 0, false);
				worldManager.lightRemovalQueue.add(new LightRemoval(pos, (byte)15, this));
				worldManager.sunlightRemovalQueue.add(new LightRemoval(pos, (byte)15, this));
			}
			//Tile.getTile(tile).onPlace((int)ax, (int)ay, (int)az, EnumFacing.NORTH, worldManager);
			//ALWAYS assume that the rebuild argument will be false (except in special cases) because the setting of the meta rebuilds the chunk.
			if(rebuild){
				rebuild();
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

	/**
	 * Position is in WORLD coordinates.
	 */
	public void blockUpdate(int x, int y, int z) {
		boolean inBoundsOne = (x-getPos().x >= 0) && (x-getPos().x < tiles.length);
		boolean inBoundsTwo = (y-getPos().y >= 0) && (y-getPos().y < tiles[0].length);
		boolean inBoundsThree = (z-getPos().z >= 0) && (z-getPos().z < tiles[0][0].length);
		if(inBoundsOne && inBoundsTwo && inBoundsThree)
			scheduledBlockUpdates.add(new Vector3f(x,y,z));
	}
	
	// Get the bits XXXX0000
	public int getSunlight(int x, int y, int z) {
	    return (light[x-(int)this.pos.x][y-(int)this.pos.y][z-(int)this.pos.z] >> 4) & 0xF;
	}

	// Set the bits XXXX0000
	public void setSunlight(int x, int y, int z, int val, boolean relight) {
		light[x-(int)this.pos.x][y-(int)this.pos.y][z-(int)this.pos.z] = (byte) ((light[x-(int)this.pos.x][y-(int)this.pos.y][z-(int)this.pos.z] & 0xF) | (val << 4));
	}

	// Get the bits 0000XXXX
	public int getBlockLight(int x, int y, int z) {
	    return light[x-(int)this.pos.x][y-(int)this.pos.y][z-(int)this.pos.z] & 0xF;
	}
	
	// Set the bits 0000XXXX
	public void setBlockLight(int x, int y, int z, int val, boolean relight) {
		light[x-(int)this.pos.x][y-(int)this.pos.y][z-(int)this.pos.z] = (byte) ((light[x-(int)this.pos.x][y-(int)this.pos.y][z-(int)this.pos.z] & 0xF0) | val);
		if(relight){
			worldManager.lightQueue.add(new Light(new Vector3i(x,y,z), this));
		}
	}
	
	private void removeBlockLight(int x, int y, int z, byte level){
		worldManager.lightRemovalQueue.add(new LightRemoval(new Vector3i(x,y,z), level, this));
	}
	
	public boolean isVisible(){
		return visible;
	}

	public byte[][][] getTiles(){
		return tiles;
	}
	
	public byte[][][] getMeta(){
		return meta;
	}

	public void updateTiles(PacketSendChunk c) {
		this.tiles = Arrays.copyOf(c.tiles, c.tiles.length);
		this.meta = Arrays.copyOf(c.meta, c.meta.length);
		this.rebuild();
	}

	public byte[][][] getLight() {
		return light;
	}
	
}
