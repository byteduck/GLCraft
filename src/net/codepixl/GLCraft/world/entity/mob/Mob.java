package net.codepixl.GLCraft.world.entity.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.network.packet.PacketHealth;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.AI.AI;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class Mob extends EntitySolid implements GameObj{
	
	private ItemStack[] inventory;
	public float health, hurtTimer, eyeLevel, airLevel, lHealth, lAirLevel;
	private float voidHurt = 0f;
	protected float fallDistance = 0f;
	protected float prevY = 0f;
	public long lastCollideX = 0;
	public long lastCollideY = 0;
	public long lastCollideZ = 0;
	public float Vx = 0;
	public float Vy = 0;
	public float Vz = 0;
	protected float speed;
	private HashMap<Class, AI> behaviors = new HashMap<Class, AI>();
	private boolean aiBusy = false;
	private DamageSource lastDamageSource = DamageSource.ENVIRONMENT;
	
	public boolean isAiBusy() {
		return aiBusy;
	}

	public void setAiBusy(boolean aiBusy) {
		this.aiBusy = aiBusy;
	}

	public Mob(Vector3f pos, WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.setInventory(new ItemStack[getInventorySize()]);
		for(int i = 0; i < getInventorySize(); i++){
			getInventory()[i] = new ItemStack();
		}
		this.health = 20f;
		this.hurtTimer = 0;
		this.eyeLevel = (float) (getAABB().r[1]*2f*0.94f);
		this.airLevel = 10f;
		this.speed = 0.1f;
	}
	
	public int getInventorySize(){
		return 9;
	}
	
	public void walkForward(){
		this.move(this.Vx,this.Vy,-this.Vz);
	}
	public void setSpeed(float speed){
		this.speed = speed;
	}
	public Vector3f getMovementVel(){
		return new Vector3f(this.Vx,this.Vy,this.Vz);
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		TagFloat healthTag = new TagFloat("healF",health);
		t.setTag(healthTag);
	}
	
	@Override
	public void move(float x, float y, float z){
		if(!isInWater()){
			if(moveMain(x,0,0)){ lastCollideX = System.currentTimeMillis(); this.getVel().x = 0;}
			if(moveMain(0,y,0)){ lastCollideY = System.currentTimeMillis(); this.getVel().y = 0;}
			if(moveMain(0,0,z)){ lastCollideZ = System.currentTimeMillis(); this.getVel().z = 0;}
		}else{
			if(moveMain(x*0.3f,0,0)){ lastCollideX = System.currentTimeMillis(); this.getVel().x = 0;}
			if(moveMain(0,y,0)){ lastCollideY = System.currentTimeMillis(); this.getVel().y = 0;}
			if(moveMain(0,0,z*0.3f)){ lastCollideZ = System.currentTimeMillis(); this.getVel().z = 0;}
		}
	}
	
	public void handleAI(){
		for(AI ai : behaviors.values()){
			ai.executeAI();
		}
	}
	
	public void addAI(AI behavior){
		behaviors.put(behavior.getClass(), behavior);
	}
	
	public boolean removeAI(AI behavior){
		return behaviors.remove(behavior.getClass(), behavior);
	}
	
	public AI getAI(Class ai){
		return behaviors.get(ai);
	}
	
	public void dropAllItems(){
		for(int i = 0; i < getInventory().length; i++){
			if(!getInventory()[i].isNull()){
				worldManager.spawnEntity(new EntityItem(getInventory()[i],this.pos,worldManager));
				getInventory()[i] = new ItemStack();
			}
		}
	}
	
	@Override
	protected void voidHurt(){
		if(this.pos.y < 0 && voidHurt >= 0.3f){
			this.hurt(2f,DamageSource.VOID);
			this.voidHurt = 0;
		}else if(this.pos.y < 0){
			this.voidHurt+=Time.getDelta();
		}else{
			this.voidHurt = 0;
		}
	}
	
	@Override
	public void update(){
		super.update();
		if(this.worldManager.isServer){ //This is so EntityPlayers won't calculate damage client-side
			if(this.onFire > 0f){
				this.hurt(0.5f,1,DamageSource.FIRE);
			}
			if(this.isInWater()){
				this.fallDistance = 0;
			}
			if(this.health<=0f){
				this.setDead(true);
			}
			if(this.hurtTimer>0){
				this.hurtTimer-=Time.getDelta();
			}else{
				this.hurtTimer = 0;
			}
			if(this.onGround){
				if(this.fallDistance > 0f){
					float damage = (fallDistance - 4f)*2;
					if(damage > 0f){
						this.hurt(damage,DamageSource.FALL);
					}
				}
				this.fallDistance = 0f;
			}else
				this.fallDistance+=this.prevY-this.getY(); 
			this.prevY = this.getY();
			if(!this.canBreathe())
				this.airLevel -= Time.getDelta();
			else
				this.airLevel = 10f;
			
			if(this.airLevel < 0){
				this.airLevel = 0;
				hurt(2f,1,DamageSource.DROWNING);
			}
			push();
			//getCamera().updateKeyboard(32, 2);
			//getCamera().updateMouse();
			DebugTimer.startTimer("ai_time");
			handleAI();
			DebugTimer.pauseTimer("ai_time");

			if(!this.tileAtEye().canPassThrough()){
				this.hurt(2.0f, 1.0f, DamageSource.ENVIRONMENT);
			}
			
			if(this.health != this.lHealth || this.airLevel != this.lAirLevel){
				worldManager.sendPacket(new PacketHealth(this));
				this.lAirLevel = airLevel;
				this.lHealth = health;
			}
		}
	}
	
	public void push(){
		float largSiz = this.getAABB().getSize().x;
		if(this.getAABB().getSize().y > largSiz)
			largSiz= this.getAABB().getSize().y;
		if(this.getAABB().getSize().z > largSiz)
			largSiz = this.getAABB().getSize().z;
		List<Entity> list = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, Mob.class, largSiz);
		Iterator<Entity> i = list.iterator();
		while(i.hasNext()){
			Mob m = (Mob)i.next();
			if(AABB.testAABB(getAABB(), m.getAABB())){
				if(this.getX() > m.getX())
					this.move((float) (Time.getDelta()*2f), 0, 0);
				else
					this.move((float) (Time.getDelta()*-2f), 0, 0);
				if(this.getZ() > m.getZ())
					this.move(0, 0, (float) (Time.getDelta()*2f));
				else
					this.move(0, 0, (float) (Time.getDelta()*-2f));
			}
		}
	}
	
	public void jump(){
		if(onGround)
			this.getVelocity().y = 0.9f;
		else if(isSubmerged()){
			this.getVelocity().y = 0.3f;
			if(System.currentTimeMillis() - this.lastCollideX <= 25 || System.currentTimeMillis() - this.lastCollideZ <= 25){
				this.getVelocity().y = 0.9f;
			}
		}
	}
	
	public ItemStack getInventory(int index){
		if(index < inventory.length)
			if(inventory[index] != null)
				return inventory[index];
			else
				return new ItemStack();
		else
			return new ItemStack(Tile.Void);
	}
	
	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
		this.needsDataUpdate();
	}
	
	public void setInventory(int i, ItemStack it){
		this.inventory[i] = it;
		this.needsDataUpdate();
	}

	public boolean canBreathe(){
		AABB eyeAABB = new AABB(getAABB().getSize().x,0.1f,getAABB().getSize().z);
		eyeAABB.update(new Vector3f(pos.getX(), pos.getY()+eyeLevel, pos.getZ()));
		for(AABB aabb : worldManager.BlockAABBForEntity(this, Tile.Water)){
			if(AABB.testAABB(aabb, eyeAABB))
				return false;
		}
		return true;
	}
	
	public void hurt(float amt, DamageSource source){
		this.health-=amt;
		this.setLastDamageSource(source);
	}
	
	public void hurt(float damage, float time, DamageSource source){
		if(this.hurtTimer<=0){
			this.hurt(damage, source);
			this.hurtTimer=time;
		}
	}
	
	public ItemStack[] getInventory(){
		return inventory;
	}
	
	public int addToInventory(ItemStack s){
		int c = s.count;
		ArrayList<Integer> blankSlots = new ArrayList<Integer>();
		ArrayList<Integer> compatibleSlots = new ArrayList<Integer>();
		for(int i = 0; i < getInventory().length; i++){
			if(getInventory()[i].isNull()){
				blankSlots.add(i);
			}else if(getInventory()[i].compatible(s)){
				compatibleSlots.add(i);
			}
		}
		
		for(int i = 0; i < compatibleSlots.size(); i++){
			int cr = getInventory()[compatibleSlots.get(i)].addToStack(c);
			c=cr;
			if(cr == 0){
				return 0;
			}
		}
		
		if(blankSlots.size() > 0){
			getInventory()[blankSlots.get(0)] = new ItemStack(s);
			return 0;
		}
		this.needsDataUpdate();
		return c;
	}
	
	@Override
	public void render() {
		
	}

	@Override
	public void dispose() {
		
	}

	public float getSpeed() {
		return speed;
	}

	public DamageSource getLastDamageSource() {
		return lastDamageSource;
	}

	public void setLastDamageSource(DamageSource lastDamageSource) {
		this.lastDamageSource = lastDamageSource;
	}
	
	public Tile tileAtEye(){
		return Tile.getTile((byte)worldManager.getTileAtPos(this.pos.x,this.pos.y+this.eyeLevel,this.pos.z));
	}

}
