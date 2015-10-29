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
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Color4f;
import com.nishu.utils.ShaderProgram;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.TileAndPos;
import net.codepixl.GLCraft.world.tile.Tile;


public class Chunk {
	private WorldManager worldManager;
	private Vector3f pos;
	private byte [][][] tiles;
	private int [][][] light;
	private byte [][][] meta;
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
							tiles[x][y][z] = Tile.Stone.getId();
						}else if(posY-1f <= noise){
							tiles[x][y][z] = Tile.Grass.getId();
						}else{
							tiles[x][y][z] = Tile.Air.getId();
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
	}
	
	void populateChunk(){
		worldManager.s.addCurrentChunk(1);
		int progress = (int)(((float)worldManager.s.currentChunk()/(float)worldManager.s.total)*33);
		worldManager.s.getSplash().setProgress(progress,"Populating chunks "+progress+"%");
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					meta[x][y][z] = 0;
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
							//worldManager.setTileAtPos(x+(int)pos.x, y+1+(int)pos.y, z+(int)pos.z, Tile.Light.getId(), false);
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
		meta = new byte[sizeX][sizeY][sizeZ];
		if(!bufChunk){
			createChunk();
		}else{
			createBufChunk();
		}
	}

	private void createBufChunk() {
		
	}

	public void init(){
		
	}
	
	public void render(){
		if(type != CentralManager.AIRCHUNK){
			shader.use();
			int texLoc = GL20.glGetUniformLocation(shader.getProgram(), "u_texture");
			int posLoc = GL20.glGetUniformLocation(shader.getProgram(), "lightpos");
			int enLoc = GL20.glGetUniformLocation(shader.getProgram(), "lightingEnabled");
			Vector3f pos = worldManager.getEntityManager().getPlayer().getPos();
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
			light();
			rebuild();
			needsRebuild = false;
		}
		for(int i = 0; i < 3; i++){
			int x = Constants.rand.nextInt(sizeX);
			int y = Constants.rand.nextInt(sizeY);
			int z = Constants.rand.nextInt(sizeZ);
			Tile.getTile(tiles[x][y][z]).randomTick(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, worldManager);
		}
	}
	
	public void tick(){
		for(int x = 0; x < sizeX; x++){
			for(int y = 0; y < sizeY; y++){
				for(int z = 0; z < sizeZ; z++){
					if(Tile.getTile(tiles[x][y][z]).needsConstantTick()){
						Tile.getTile(tiles[x][y][z]).tick(x+(int)pos.x, y+(int)pos.y, z+(int)pos.z, worldManager);
					}
				}
			}
		}
	}
	
	public void rebuild(){
		if(type != CentralManager.AIRCHUNK){
			glNewList(vcID, GL_COMPILE);
			for(int x = 0; x < sizeX; x++){
				for(int y = 0; y < sizeY; y++){
					for(int z = 0; z < sizeZ; z++){
						if(tiles[x][y][z] != 0 && !checkTileNotInView(x,y,z)){
							/**if(tiles[x][y][z] != Tile.TallGrass.getId()){
								//System.out.println(Tile.getTile(tiles[x][y][z]).getName());
								//System.out.println(pos);
								Shape.createCube(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
								//System.out.println("Creating "+Tile.getTile(tiles[x][y][z]).getName()+" at "+pos.x+x+","+pos.y+y+","+pos.z+z);
							}else{
								Shape.createCross(pos.x+x, pos.y+y, pos.z+z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
							}**/
							Tile t = Tile.getTile(tiles[x][y][z]);
								if(t.getRenderType() == RenderType.CUBE){
									glBegin(GL_QUADS);
									Shape.createCube(pos.x+x, pos.y+y, pos.z+z, t.getColor(), t.getTexCoords(), 1);
									glEnd();
								}else if(t.getRenderType() == RenderType.CROSS){
									glBegin(GL_QUADS);
									Shape.createCross(pos.x+x, pos.y+y, pos.z+z, t.getColor(), t.getTexCoords(), 1);
									glEnd();
								}else if(t.getRenderType() == RenderType.FLAT){
									glBegin(GL_QUADS);
									Shape.createFlat(pos.x+x, pos.y+y+0.01f, pos.z+z, t.getColor(), t.getTexCoords(), 1);
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
		if(x > pos.getX()){
			if(!Tile.getTile(tiles[x-1][y][z]).isTransparent()) facesHidden[0] = true;
			else facesHidden[0] = false;
		}else{
			facesHidden[0] = false;
		}
		if(x < sizeX - 1){
			if(!Tile.getTile(tiles[x+1][y][z]).isTransparent()) facesHidden[1] = true;
			else facesHidden[1] = false;
		}else{
			facesHidden[1] = false;
		}
		if(y > pos.getY()){
			if(!Tile.getTile(tiles[x][y-1][z]).isTransparent()) facesHidden[2] = true;
			else facesHidden[2] = false;
		}else{
			facesHidden[2] = false;
		}
		if(y < sizeY - 1){
			if(!Tile.getTile(tiles[x][y+1][z]).isTransparent()) facesHidden[3] = true;
			else facesHidden[3] = false;
		}else{
			facesHidden[3] = false;
		}
		if(z > pos.getZ()){
			if(!Tile.getTile(tiles[x][y][z-1]).isTransparent()) facesHidden[4] = true;
			else facesHidden[4] = false;
		}else{
			facesHidden[4] = false;
		}
		if(z < sizeZ - 1){
			if(!Tile.getTile(tiles[x][y][z+1]).isTransparent()) facesHidden[5] = true;
			else facesHidden[5] = false;
		}else{
			facesHidden[5] = false;
		}
		
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
			if(tiles[x][y][z] == Tile.Light.getId()){
				this.light[x][y][z] = 0;
			}
			tiles[x][y][z] = tile;
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
