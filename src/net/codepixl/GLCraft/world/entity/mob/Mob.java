package net.codepixl.GLCraft.world.entity.mob;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.DebugTimer;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.Ray;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.mob.animal.EntityTestAnimal;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class Mob extends EntitySolid implements GameObj{
	
	private ItemStack[] inventory;
	public float health, hurtTimer, eyeLevel, airLevel;
	private float voidHurt = 0f;
	protected float fallDistance = 0f;
	protected float prevY = 0f;
	
	public Mob(Vector3f pos, WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.setInventory(new ItemStack[9]);
		for(int i = 0; i < 9; i++){
			getInventory()[i] = new ItemStack();
		}
		this.health = 20f;
		this.hurtTimer = 0;
		this.eyeLevel = (float) (getAABB().r[1]*2f*0.94f);
		this.airLevel = 10f;
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		TagFloat healthTag = new TagFloat("healF",health);
		t.setTag(healthTag);
	}
	
	@Override
	public void move(float x, float y, float z){
		if(!isInWater()){
			moveMain(x,0,0);
			moveMain(0,y,0);
			moveMain(0,0,z);
		}else{
			moveMain(x*0.3f,0,0);
			moveMain(0,y,0);
			moveMain(0,0,z*0.3f);
		}
	}
	
	public void handleAI(){
		
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
			this.hurt(2f);
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
		if(this.onFire > 0f){
			this.hurt(0.5f,1);
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
				float damage = (fallDistance - 3f)*2;
				if(damage > 0f){
					this.hurt(damage);
				}
			}
			this.fallDistance = 0f;
		}else{
			this.fallDistance+=this.prevY-this.getY();
		}
		this.prevY = this.getY();
		if(!this.canBreathe())
			this.airLevel -= Time.getDelta();
		else
			this.airLevel = 10f;
		
		if(this.airLevel < 0){
			this.airLevel = 0;
			hurt(2f,1);
		}
		
		//getCamera().updateKeyboard(32, 2);
		//getCamera().updateMouse();
		DebugTimer.startTimer("ai_time");
		handleAI();
		DebugTimer.pauseTimer("ai_time");
	}
	
	public void jump(){
		if(onGround)
			this.getVelocity().y = 0.9f;
		else if(isInWater()){
			this.getVelocity().y = 0.3f;
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
	}
	
	public void setInventory(int i, ItemStack it){
		this.inventory[i] = it;
	}

	public boolean canBreathe(){
		return !(Tile.getTile((byte) worldManager.getTileAtPos(pos.x,pos.y+eyeLevel,pos.z)) == Tile.Water);
	}
	
	public void hurt(float amt){
		this.health-=amt;
	}
	
	public void hurt(float damage,float time){
		if(this.hurtTimer<=0){
			this.hurt(damage);
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
		return c;
	}
	
	@Override
	public void render() {
		
	}

	@Override
	public void dispose() {
		
	}

}
