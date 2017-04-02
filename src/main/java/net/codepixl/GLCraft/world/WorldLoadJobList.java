package net.codepixl.GLCraft.world;

import com.nishu.utils.ShaderProgram;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

public class WorldLoadJobList{
	Chunk cChunk;
	ArrayList<Vector3f> jobs = new ArrayList<Vector3f>();
	WorldManager w;
	ShaderProgram s,ws;
	public WorldLoadJobList(WorldManager w, ShaderProgram s, ShaderProgram ws){
		this.w = w;
		this.s = s;
		this.ws = ws;
	}
	public void add(Vector3f add){
		jobs.add(add);
	}
	public Chunk next(){
		if(jobs.size() > 0){
			Vector3f next = jobs.get(jobs.size()-1);
			cChunk = new Chunk(s,ws,CentralManager.MIXEDCHUNK,next,w);
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
		//Logger.log("CONTAINS "+next+" IS "+jobs.contains(next));
		return jobs.contains(next);
	}
}
