package net.codepixl.GLCraft.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Frustum;
import net.codepixl.GLCraft.util.PerlinNoise;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.Texture;
import net.codepixl.GLCraft.world.entity.mob.MobManager;

import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class WorldManager {
	public static float[][] noise;
	public boolean doneGenerating = false;
	private MobManager mobManager;
	private ArrayList<Chunk> activeChunks;
	private ShaderProgram shader;
	public static Vector3f selectedBlock = new Vector3f(0,0,0);
	
	public WorldManager(){
		initGL();
		init();
		createWorld();
	}
	
	private void initGL(){
		Shader temp = new Shader("/shaders/chunk.vert","/shaders/chunk.frag");
		shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}
	
	private void init(){
		mobManager = new MobManager(this);
		activeChunks = new ArrayList<Chunk>();
	}
	
	private void createWorld(){
		System.out.println("Creating Chunks...");
		noise = PerlinNoise.generateSmoothNoise(PerlinNoise.generateWhiteNoise(Constants.viewDistance*Constants.CHUNKSIZE, Constants.viewDistance*Constants.CHUNKSIZE), 100);
		for(int x = 0; x < Constants.viewDistance; x++){
			for(int y = 0; y < Constants.viewDistance; y++){
				for(int z = 0; z < Constants.viewDistance; z++){
					activeChunks.add(new Chunk(shader, 1, x * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z * Constants.CHUNKSIZE, this));
					//saveChunk(activeChunks.get(activeChunks.size() - 1));
				}
			}
		}
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			i.next().populateChunk();
		}
		System.out.println("Done!");
		doneGenerating = true;
	}
	
	public void update(){
		mobManager.update();
		mobManager.getPlayer().update();
	}
	
	public void render(){
		Spritesheet.tiles.bind();
		getMobManager().getPlayer().getCamera().applyTranslations();
		Vector3f pos = getMobManager().getPlayer().getCamera().getPos();
		if(getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == 0 || getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == -1 || getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == 9 || getTileAtPos((int)pos.x,(int)pos.y,(int)pos.z) == 4){
			for(int i = 0; i < activeChunks.size(); i++){
				if(Frustum.getFrustum().cubeInFrustum(activeChunks.get(i).getPos().getX(), activeChunks.get(i).getPos().getY(), activeChunks.get(i).getPos().getZ(), activeChunks.get(i).getPos().getX() + Constants.CHUNKSIZE, activeChunks.get(i).getPos().getY() + Constants.CHUNKSIZE, activeChunks.get(i).getPos().getZ() + Constants.CHUNKSIZE)){
					float distance=(float) Math.sqrt(Math.pow(activeChunks.get(i).getCenter().getX()-mobManager.getPlayer().getX(),2) + Math.pow(activeChunks.get(i).getCenter().getY()-mobManager.getPlayer().getY(),2) + Math.pow(activeChunks.get(i).getCenter().getZ()-mobManager.getPlayer().getZ(),2));
					if(distance < 10*Constants.CHUNKSIZE){
						activeChunks.get(i).render();
					}
				}
			}
		}
		//System.out.println(Raytracer.getScreenCenterRay());
		mobManager.render();
	}
	
	public void saveChunk(Chunk c){
		try {
			File dir = new File("world/chunks/");
			dir.mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter("world/chunks/"+c.vcID+"chunk.dat"));
			bw.write(c.getPos().getX()+ " "+c.getPos().getY()+ " "+c.getPos().getZ()+ " ");
			for(int x = (int) c.getPos().getX(); x < c.getPos().getX() + Constants.CHUNKSIZE; x++){
				for(int y = (int) c.getPos().getX(); y < c.getPos().getY() + Constants.CHUNKSIZE; y++){
					for(int z = (int) c.getPos().getZ(); z < c.getPos().getZ() + Constants.CHUNKSIZE; z++){
						bw.write(String.valueOf(c.getTileID(x, y, z)));
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MobManager getMobManager(){
		return mobManager;
	}
	
	public int getTileAtPos(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			/**if(x < c.getPos().getX() || x > c.getPos().getX() + Constants.CHUNKSIZE || y < c.getPos().getY() || y > c.getPos().getY() + Constants.CHUNKSIZE || z < c.getPos().getZ() || z > c.getPos().getZ() + Constants.CHUNKSIZE){
				//return c.getTileAtCoord(x, y, z);
			}else{
				return c.getTileAtCoord(x, y, z);
			}**/
			if(c.getTileAtCoord(x, y, z) != -1){
				return c.getTileAtCoord(x, y, z);
			}
		}
		return -1;
	}
	
	public void setTileAtPos(int x, int y, int z, byte tile){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(c.setTileAtPos(x, y, z,tile)){
				c.rebuild();
				return;
			}
		}
	}
	
	public int getChunk(int x, int y, int z){
		Iterator<Chunk> i = activeChunks.iterator();
		while(i.hasNext()){
			Chunk c = i.next();
			if(x < c.getPos().getX() || x > c.getPos().getX() + Constants.CHUNKSIZE || y < c.getPos().getY() || y > c.getPos().getY() + Constants.CHUNKSIZE || z < c.getPos().getZ() || z > c.getPos().getZ() + Constants.CHUNKSIZE){
				//return c.getTileAtCoord(x, y, z);
			}else{
				return c.vcID;
			}
		}
		return -1;
	}
}
