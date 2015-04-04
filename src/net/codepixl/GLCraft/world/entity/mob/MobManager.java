package net.codepixl.GLCraft.world.entity.mob;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.util.ArrayList;

import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.world.entity.Camera;

public class MobManager implements GameObj{
	
	private ArrayList<Mob> mobs;
	private Player player;
	
	private int mobRenderID;
	
	public MobManager(){
		init();
	}
	
	public void init(){
		mobs = new ArrayList<Mob>();
		player = new Player(new Camera(10f,84f,10f,1f,90f,-90f,1),0);
		initGL();
	}
	
	private void initGL(){
		mobRenderID = glGenLists(1);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		for(int i = 0; i < mobs.size(); i++){
			if(mobs.get(i).isDead()){
				mobs.remove(i);
			}
			mobs.get(i).update();
		}
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		for(int i = 0; i < mobs.size(); i++){
			mobs.get(i).render();
		}
		glCallList(mobRenderID);
	}

	@Override
	public void dispose() {
		player.dispose();
		glDeleteLists(mobRenderID, 1);
	}
	
	public Player getPlayer(){
		return player;
	}

}
