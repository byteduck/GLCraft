package net.codepixl.GLCraft.world;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.PerlinNoise;
import net.codepixl.GLCraft.util.logging.GLogger;

import java.util.ArrayList;

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
    	this.noise = PerlinNoise.generateSmoothNoise(PerlinNoise.generateWhiteNoise(Constants.worldLengthChunks*Constants.CHUNKSIZE, Constants.worldLengthChunks*Constants.CHUNKSIZE), 100);
		for(int x = 0; x < Constants.worldLengthChunks; x++){
			for(int z = 0; z < Constants.worldLengthChunks; z++){
				activeChunks.add(new GenChunk(this,CentralManager.MIXEDCHUNK,x,0,z));
				//w.saveChunk(activeChunks.get(activeChunks.size() - 1));
			}
		}
		done = true;
		GLogger.log("Chunks generated.", LogSource.SERVER);
	}

	public boolean isDone(){
		return done;
	}
	
	public ArrayList<GenChunk> getGenChunks(){
		return activeChunks;
	}

}
