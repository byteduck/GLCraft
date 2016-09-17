package net.codepixl.GLCraft.world.entity.mob.AI;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.entity.mob.Mob;
import net.codepixl.GLCraft.world.tile.Tile;

public class AIWander extends AIPathfind{
	private double minDelay,maxDelay,currentDelay,currentTime,maxTryTime,currentTryTime;
	private float maxRange;
	private boolean moving;
	public AIWander(Mob m, double minDelay, double maxDelay, double maxTryTime, float maxRange) {
		super(m);
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.maxRange = maxRange;
		this.maxTryTime = maxTryTime;
		this.currentTime = 0;
		this.currentTryTime = 0;
		this.moving = false;
	}
	
	@Override
	public void executeAI(){
		if(!mob.isAiBusy()){
			if(!moving){
				if(this.currentTime == 0)
					this.currentDelay = Constants.randDouble(minDelay, maxDelay);
				this.currentTime+=Time.getDelta();
				if(this.currentTime >= this.currentDelay){
					this.currentTime = 0;
					this.currentTryTime = 0;
					Vector3f randPos = new Vector3f(mob.getX()+Constants.randFloat(-maxRange, maxRange), mob.getY(), mob.getZ()+Constants.randFloat(-maxRange, maxRange));
					while(!Tile.getTile((byte) mob.worldManager.getTileAtPos(randPos)).canPassThrough()){
						randPos = new Vector3f(mob.getX()+Constants.randFloat(-maxRange, maxRange), mob.getY(), mob.getZ()+Constants.randFloat(-maxRange, maxRange));
					}
					this.loc = randPos;
					this.moving = true;
				}
			}else{
				super.executeAI();
				if(!(MathUtils.distance(mob.getPos(), loc) <= 0.5) && currentTryTime <= maxTryTime){
					currentTryTime+=Time.getDelta();
				}else{
					moving = false;
				}
			}
		}else{
			moving = false;
			this.currentTime = 0;
		}
	}
	
}
