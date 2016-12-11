package net.codepixl.GLCraft.world;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.util.vector.Vector3f;

public class GenChunk {
	
	Vector3f pos;
	byte [][][] tiles;
	private int sizeX;
	private int sizeY;
	private int sizeZ;
	int type;
	
	private Generator g;
	
	public GenChunk(Generator g,int type, float x, float y, float z){
		this.g = g;
		this.type = type;
		this.pos = new Vector3f(x,y,z);
		sizeX = (int)pos.getX()+Constants.CHUNKSIZE;
		sizeY = (int)pos.getY()+Constants.CHUNKSIZE;
		sizeZ = (int)pos.getZ()+Constants.CHUNKSIZE;
		this.tiles = new byte[sizeX][sizeY][sizeZ];
		createChunk();
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
			for(int x = (int) pos.getX(); x < sizeX; x++){
				for(int y = (int) pos.getY(); y < sizeY; y++){
					for(int z = (int) pos.getZ(); z < sizeZ; z++){
						if(y < g.noise[x][z]*Constants.CHUNKSIZE){
							tiles[x][y][z] = Tile.Stone.getId();
						}else if(y-1 <= g.noise[x][z]*Constants.CHUNKSIZE){
							tiles[x][y][z] = Tile.Grass.getId();
						}else{
							tiles[x][y][z] = Tile.Air.getId();
						}
					}
				}
			}
		}
	}
}
