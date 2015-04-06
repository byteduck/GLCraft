package net.codepixl.GLCraft.world;

import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.PerlinNoise;
import net.codepixl.GLCraft.world.tile.Tile;

import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;

public class Generator implements Runnable{
	
	private boolean done = false;
	private ArrayList<GenChunk> activeChunks;
	private WorldManager w;
	float[][] noise;
	
	public Generator(WorldManager w){
		this.w = w;
		activeChunks = new ArrayList<GenChunk>();
	}
	
	@Override
	public void run() {
    	this.noise = PerlinNoise.generateSmoothNoise(PerlinNoise.generateWhiteNoise(Constants.viewDistance*Constants.CHUNKSIZE, Constants.viewDistance*Constants.CHUNKSIZE), 100);
		for(int x = 0; x < Constants.viewDistance; x++){
			for(int y = 0; y < Constants.viewDistance; y++){
				for(int z = 0; z < Constants.viewDistance; z++){
					activeChunks.add(new GenChunk(this,World.MIXEDCHUNK,x,y,z));
					//w.saveChunk(activeChunks.get(activeChunks.size() - 1));
				}
			}
		}
		done = true;
		System.out.println("Chunks generated.");
	}

	public boolean isDone(){
		return done;
	}
	
	public ArrayList<GenChunk> getGenChunks(){
		return activeChunks;
	}

}
