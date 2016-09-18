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
	
	public AIWander(Mob m){
		super(m);
		this.minDelay = 1;
		this.maxDelay = 5;
		this.maxRange = 5;
		this.maxTryTime = 4;
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
					Vector3f randPos = new Vector3f(mob.getX()+Constants.randFloat(-maxRange, maxRange), mob.getY()+Constants.randFloat(-maxRange, maxRange), mob.getZ()+Constants.randFloat(-maxRange, maxRange));
					int tries = 0;
					while(!Tile.getTile((byte) mob.worldManager.getTileAtPos(randPos)).canPassThrough() || Tile.getTile((byte) mob.worldManager.getTileAtPos(new Vector3f(randPos.x, randPos.y-1, randPos.z))).canPassThrough() && this.setLocation(randPos) && tries < 10){
						randPos = new Vector3f(mob.getX()+Constants.randFloat(-maxRange, maxRange), mob.getY(), mob.getZ()+Constants.randFloat(-maxRange, maxRange));
						if(!(Tile.getTile((byte) mob.worldManager.getTileAtPos(randPos.x, randPos.y-1, randPos.z)) == Tile.Air)) tries++;
					}
					this.setLocation(randPos);
					this.moving = true;
				}
			}else{
				super.executeAI();
				if(this.getLocation() != null){
					if(!(MathUtils.distance(mob.getPos(), this.getLocation()) <= 1) && currentTryTime <= maxTryTime){
						currentTryTime+=Time.getDelta();
					}else{
						moving = false;
					}
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
