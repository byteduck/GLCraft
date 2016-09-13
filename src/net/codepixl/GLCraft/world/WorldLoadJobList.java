package net.codepixl.GLCraft.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.ShaderProgram;

public class WorldLoadJobList{
	Chunk cChunk;
	ArrayList<Vector3f> jobs = new ArrayList<Vector3f>();
	WorldManager w;
	ShaderProgram s;
	public WorldLoadJobList(WorldManager w, ShaderProgram s){
		this.w = w;
		this.s = s;
	}
	public void add(Vector3f add){
		jobs.add(add);
	}
	public Chunk next(){
		if(jobs.size() > 0){
			Vector3f next = jobs.get(jobs.size()-1);
			cChunk = new Chunk(s,CentralManager.MIXEDCHUNK,next,w);
			//w.activeChunks.add(cChunk);
			//cChunk.populateChunk();
			cChunk.rebuild();
			jobs.remove(jobs.size()-1);
			return cChunk;
		}else{
			return null;
		}
	}
	public boolean contains(Vector3f next) {
		//System.out.println("CONTAINS "+next+" IS "+jobs.contains(next));
		return jobs.contains(next);
	}
}
