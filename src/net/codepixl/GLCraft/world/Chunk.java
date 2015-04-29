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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.tile.Tile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.ShaderProgram;


public class Chunk {
	private WorldManager worldManager;
	private Vector3f pos;
	private byte [][][] tiles;
	private int [][][] light;
	private ShaderProgram shader;
	public int vcID;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	int type;
	private boolean isActive;
	private int randomUpdateTick = 0;
	private int randomUpdateInterval;
	private boolean needsRebuild = false;
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z,WorldManager w, boolean fromBuf){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL(fromBuf);
		init();
	}
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z,WorldManager w){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL(false);
		init();
	}
	
	public Chunk(ShaderProgram shader, int type, Vector3f pos, WorldManager w){
		this.worldManager = w;
		this.pos = pos;
		this.shader = shader;
		this.type = type;
		initGL(false);
		init();
	}
	
	private void createChunk(){
		worldManager.s.addCurrentChunk(1);
		int progress = (int)(((float)worldManager.s.currentChunk()/(float)worldManager.s.total)*33);
		worldManager.s.getSplash().setProgress(progress,"Generating chunks "+progress+"%");
		if(type == World.AIRCHUNK){
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
						if(posY < worldManager.noise[posX][posZ]*Constants.CHUNKSIZE*Constants.viewDistance){
							tiles[x][y][z] = Tile.Stone.getId();
						}else if(posY-1 <= worldManager.noise[posX][posZ]*Constants.CHUNKSIZE*Constants.viewDistance){
							tiles[x][y][z] = Tile.Grass.getId();
						}else{
							tiles[x][y][z] = Tile.Air.getId();
						}
					}
				}
			}
		}
	}
	
	void populateChunk(){
		worldManager.s.addCurrentChunk(1);
		int progress = (int)(((float)worldManager.s.currentChunk()/(float)worldManager.s.total)*33);
		worldManager.s.getSplash().setProgress(progress,"Populating chunks "+progress+"%");
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
							createTree(x,y+1,z);
						}
						if(rand > 1 && rand <= 11){
							worldManager.setTileAtPos(x+(int)pos.x, y+1+(int)pos.y, z+(int)pos.z, Tile.TallGrass.getId(), false);
						}
						if(rand > 11 && rand <= 12){
							worldManager.setTileAtPos(x+(int)pos.x, y+1+(int)pos.y, z+(int)pos.z, Tile.Light.getId(), false);
							worldManager.lights.add(new Vector3f(x+(int)pos.x, y+1+(int)pos.y, z+(int)pos.z));
						}
					}
				}
			}
		}
	}
	
	private void createTree(int x, int y, int z){
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
	
	public void initGL(boolean bufChunk){
		sizeX = Constants.CHUNKSIZE;
		sizeY = Constants.CHUNKSIZE;
		sizeZ = Constants.CHUNKSIZE;
		vcID = glGenLists(1);
		tiles = new byte[sizeX][sizeY][sizeZ];
		light = new int[sizeX][sizeY][sizeZ];
		if(!bufChunk){
			createChunk();
		}else{
			createBufChunk();
		}
	}
	
	private void createBufChunk() {
		worldManager.s.addCurrentChunk(1);
		worldManager.s.getSplash().setProgress((int)(((float)worldManager.s.currentChunk()/(float)Math.pow(Constants.viewDistance,3))*(float)100), "Transferring World...");
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					int posX = (int)pos.x+x;
					int posY = (int)pos.y+y;
					int posZ = (int)pos.z+z;
					tiles[x][y][z] = Constants.worldBuf[posX][posY][posZ];
					//System.out.println("Setting tile "+posX+","+posY+","+posZ+" tile: "+Tile.getTile(Constants.worldBuf[posX][posY][posZ]).getName());
				}
			}
		}
	}

	public void init(){
		
	}
	
	public void render(){
		if(type != World.AIRCHUNK){
			shader.use();
			int texLoc = GL20.glGetUniformLocation(shader.getProgram(), "u_texture");
			int posLoc = GL20.glGetUniformLocation(shader.getProgram(), "lightpos");
			int enLoc = GL20.glGetUniformLocation(shader.getProgram(), "lightingEnabled");
			Vector3f pos = worldManager.getMobManager().getPlayer().getPos();
			GL20.glUniform1i(texLoc, 0);
			GL20.glUniform3f(posLoc, pos.x, pos.y, pos.z);
			GL20.glUniform1i(enLoc, 1);
			GL11.glPolygonOffset(1.0f,1.0f);
			glCallList(vcID);
			shader.release();
		}
	}
	
	public void update(){
		if(needsRebuild){
			rebuild();
			needsRebuild = false;
		}
	}
	
	public void rebuild(){
		if(type != World.AIRCHUNK){
			glNewList(vcID, GL_COMPILE);
			glBegin(GL_QUADS);
			for(int x = 0; x < sizeX; x++){
				for(int y = 0; y < sizeY; y++){
					for(int z = 0; z < sizeZ; z++){
						if(tiles[x][y][z] != 0 && !checkTileNotInView(x,y,z)){
							if(tiles[x][y][z] != Tile.TallGrass.getId()){
								//System.out.println(Tile.getTile(tiles[x][y][z]).getName());
								//System.out.println(pos);
								Shape.createCube(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1, worldManager.lights);
								//System.out.println("Creating "+Tile.getTile(tiles[x][y][z]).getName()+" at "+pos.x+x+","+pos.y+y+","+pos.z+z);
							}else{
								Shape.createCross(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1, worldManager.lights);
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
	
	private boolean checkTileNotInView(int x, int y, int z){
		/*boolean facesHidden[] = new boolean[6];
		if(x > pos.getX()){
			if(tiles[x-1][y][z] != 0) facesHidden[0] = true;
			else facesHidden[0] = false;
		}else{
			facesHidden[0] = false;
		}
		if(x < sizeX - 1){
			if(tiles[x+1][y][z] != 0) facesHidden[1] = true;
			else facesHidden[1] = false;
		}else{
			facesHidden[1] = false;
		}
		if(y > pos.getY()){
			if(tiles[x][y-1][z] != 0) facesHidden[2] = true;
			else facesHidden[2] = false;
		}else{
			facesHidden[2] = false;
		}
		if(y < sizeY - 1){
			if(tiles[x][y+1][z] != 0) facesHidden[3] = true;
			else facesHidden[3] = false;
		}else{
			facesHidden[3] = false;
		}
		if(z > pos.getZ()){
			if(tiles[x][y][z-1] != 0) facesHidden[4] = true;
			else facesHidden[4] = false;
		}else{
			facesHidden[4] = false;
		}
		if(z < sizeZ - 1){
			if(tiles[x][y][z+1] != 0) facesHidden[5] = true;
			else facesHidden[5] = false;
		}else{
			facesHidden[5] = false;
		}
		
		return facesHidden[0] && facesHidden[1] && facesHidden[2] && facesHidden[3] && facesHidden[4] && facesHidden[5];*/
		return false;
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
	
	public void setTileAtPos(int x, int y, int z, byte tile, boolean rebuild){
		boolean inBoundsOne = (x >= 0) && (x < tiles.length);
		boolean inBoundsTwo = (y >= 0) && (y < tiles[0].length);
		boolean inBoundsThree = (z >= 0) && (z < tiles[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			if(tiles[x][y][z] == Tile.Light.getId()){
				Iterator<Vector3f> i = worldManager.lights.iterator();
				float ax = x+pos.x;
				float ay = y+pos.y;
				float az = z+pos.z;
				while(i.hasNext()){
					Vector3f lPos = i.next();
					if(MathUtils.compare(new Vector3f(ax,ay,az),lPos)){
						i.remove();
						return;
					}
				}
				if(rebuild){
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay, (int)az-7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay, (int)az+7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az+7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az+7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay, (int)az-7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay, (int)az-7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az+7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az+7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay+7, (int)az-7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax, (int)ay-7, (int)az-7)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay+7, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay+7, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax+7, (int)ay-7, (int)az)).queueRebuild();
					worldManager.getChunkAtCoords(MathUtils.coordsToChunkPos((int)ax-7, (int)ay-7, (int)az)).queueRebuild();
				}
			}
			tiles[x][y][z] = tile;
		}
		if(rebuild){
			rebuild();
		}
	}
	
	private void queueRebuild() {
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
			return light[(int)posi.x][(int)posi.y][(int)posi.z];
		}else{
			return light[(int)posi.x-(int)pos.x][(int)posi.y-(int)pos.y][(int)posi.z-(int)pos.z];
		}
	}

	public void setLight(Vector3f posi, int light, boolean ChunkPos) {
		if(ChunkPos){
			this.light[(int)posi.x][(int)posi.y][(int)posi.z] = light;
		}else{
			this.light[(int)posi.x-(int)pos.x][(int)posi.y-(int)pos.y][(int)posi.z-(int)pos.z] = light;
		}
	}

	public void light() {
		worldManager.s.addCurrentChunk(1);
		int progress = (int)(((float)worldManager.s.currentChunk()/(float)worldManager.s.total)*33);
		worldManager.s.getSplash().setProgress(progress,"Lighting chunks "+progress+"%");
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					if(tiles[x][y][z] == Tile.Light.getId()){
						worldManager.putLight(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, 7);
						worldManager.loop++;
						System.out.println(worldManager.loop);
					}
				}
			}
		}
	}
}
