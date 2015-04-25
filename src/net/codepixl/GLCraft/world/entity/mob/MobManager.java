package net.codepixl.GLCraft.world.entity.mob;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.util.ArrayList;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Camera;

public class MobManager implements GameObj{
	
	private ArrayList<Mob> mobs;
	private Player player;
	private PlayerMP otherPlayer;
	private WorldManager w;
	
	private int mobRenderID;
	
	public MobManager(WorldManager w){
		this.w = w;
		init();
	}
	
	public void init(){
		mobs = new ArrayList<Mob>();
		player = new Player(new Camera(10f,84f,10f,1f,90f,-90f,0,w),0);
		if(Constants.isMultiplayer){
			System.out.println("<---IT IS MULTIPLAYER--->");
			otherPlayer = new PlayerMP(new Camera(0,0,0,0,0,0,1,w),1,1);
			mobs.add(otherPlayer);
		}
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

	public PlayerMP getPlayerMP() {
		// TODO Auto-generated method stub
		return this.otherPlayer;
	}

}
