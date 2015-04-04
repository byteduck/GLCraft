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

import java.util.Random;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.tile.Tile;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.ShaderProgram;


public class Chunk {
	private WorldManager worldManager;
	private Vector3f pos;
	private byte [][][] tiles;
	private ShaderProgram shader;
	public int vcID;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	private int type;
	private boolean isActive;
	
	public Chunk(ShaderProgram shader, int type, float x, float y, float z,WorldManager w){
		this.pos = new Vector3f(x,y,z);
		this.shader = shader;
		this.type = type;
		this.worldManager = w;
		initGL();
		init();
	}
	
	public Chunk(ShaderProgram shader, int type, Vector3f pos, WorldManager w){
		this.worldManager = w;
		this.pos = pos;
		this.shader = shader;
		this.type = type;
		initGL();
		init();
	}
	
	private void createChunk(){
		if(type == World.AIRCHUNK){
			for(int x = (int) pos.getX(); x < sizeX; x++){
				for(int y = (int) pos.getY(); y < sizeY; y++){
					for(int z = (int) pos.getZ(); z < sizeZ; z++){
						tiles[x][y][z] = Tile.Air.getId();
					}
				}
			}
		}else{
			Random r = new Random();
			for(int x = (int) pos.getX(); x < sizeX; x++){
				for(int y = (int) pos.getY(); y < sizeY; y++){
					for(int z = (int) pos.getZ(); z < sizeZ; z++){
						if(y < worldManager.noise[x][z]*Constants.CHUNKSIZE*Constants.viewDistance){
							tiles[x][y][z] = Tile.Stone.getId();
						}else if(y-1 <= worldManager.noise[x][z]*Constants.CHUNKSIZE*Constants.viewDistance){
							tiles[x][y][z] = Tile.Grass.getId();
						}else{
							tiles[x][y][z] = Tile.Air.getId();
						}
					}
				}
			}
		}
	}
	
	public void initGL(){
		sizeX = (int)pos.getX()+Constants.CHUNKSIZE;
		sizeY = (int)pos.getY()+Constants.CHUNKSIZE;
		sizeZ = (int)pos.getZ()+Constants.CHUNKSIZE;
		vcID = glGenLists(1);
		
		tiles = new byte[sizeX][sizeY][sizeZ];
		
		createChunk();
		rebuild();
	}
	
	public void init(){
		
	}
	
	public void render(){
		if(type != World.AIRCHUNK){
			shader.use();
			int texLoc = GL20.glGetUniformLocation(shader.getProgram(), "u_texture");
			GL20.glUniform1i(texLoc, 0);
			glCallList(vcID);
			shader.release();
		}
	}
	
	public void update(){
		
	}
	
	public void rebuild(){
		if(type != World.AIRCHUNK){
			glNewList(vcID, GL_COMPILE);
			glBegin(GL_QUADS);
			for(int x = (int) pos.getX(); x < sizeX; x++){
				for(int y = (int) pos.getY(); y < sizeY; y++){
					for(int z = (int) pos.getZ(); z < sizeZ; z++){
						if(tiles[x][y][z] != 0 && !checkTileNotInView(x,y,z) && worldManager.selectedBlock != new Vector3f((int)x, (int)y, (int)z)){
							Shape.createCube(x, y, z, Tile.getTile(tiles[x][y][z]).getColor(), Tile.getTile(tiles[x][y][z]).getTexCoords(), 1);
						}
					}
				}
			}
			glEnd();
			glEndList();
		}
	}
	
	private boolean checkTileNotInView(int x, int y, int z){
		boolean facesHidden[] = new boolean[6];
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
	
		if(x < pos.getX() || x > pos.getX() + Constants.CHUNKSIZE || y < pos.getY() || y > pos.getY() + Constants.CHUNKSIZE || z < pos.getZ() || z > pos.getZ() + Constants.CHUNKSIZE || !inBounds){
			return -1;
		}
		
		//System.out.println(x+","+y+","+z+" is at "+tX+","+tY+","+tZ+" and is "+Tile.getTile(tiles[x][y][z]).getName()+" in chunk "+vcID);
		return tiles[x][y][z];
	}
	
	public boolean setTileAtPos(int x, int y, int z, byte tile){
		boolean inBoundsOne = (x >= 0) && (x < tiles.length);
		boolean inBoundsTwo = (y >= 0) && (y < tiles[0].length);
		boolean inBoundsThree = (z >= 0) && (z < tiles[0][0].length);
		boolean inBounds = inBoundsOne && inBoundsTwo && inBoundsThree;
		if(inBounds){
			tiles[x][y][z] = tile;
			return true;
		}else{
			return false;
		}
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
}
