package net.codepixl.GLCraft.world;

import java.util.ArrayList;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.PerlinNoise;

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
