package net.codepixl.GLCraft.world.entity.mob;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.Ray;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class Mob extends EntitySolid implements GameObj{
	
	protected ItemStack[] inventory;
	public float health, hurtTimer;
	private float voidHurt = 0f;
	protected float fallDistance = 0f;
	protected float prevY = 0f;
	
	public Mob(Vector3f pos, WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.inventory = new ItemStack[9];
		for(int i = 0; i < 9; i++){
			inventory[i] = new ItemStack();
		}
		this.health = 20f;
		this.hurtTimer = 0;
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		TagFloat healthTag = new TagFloat("healF",health);
		t.setTag(healthTag);
	}
	
	public void handleAI(){
		
	}
	
	public void dropAllItems(){
		for(int i = 0; i < inventory.length; i++){
			if(!inventory[i].isNull()){
				worldManager.spawnEntity(new EntityItem(inventory[i],this.pos,worldManager));
				inventory[i] = new ItemStack();
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
		//getCamera().updateKeyboard(32, 2);
		//getCamera().updateMouse();
		handleAI();
	}
	
	public void jump(){
		if(onGround) this.getVelocity().y = 0.9f;
	}
	
	public ItemStack getInventory(int index){
		if(index < inventory.length)
			return inventory[index];
		else
			return new ItemStack(Tile.Void);
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
		for(int i = 0; i < inventory.length; i++){
			if(inventory[i].isNull()){
				blankSlots.add(i);
			}else if(inventory[i].compatible(s)){
				compatibleSlots.add(i);
			}
		}
		
		for(int i = 0; i < compatibleSlots.size(); i++){
			int cr = inventory[compatibleSlots.get(i)].addToStack(c);
			c=cr;
			if(cr == 0){
				return 0;
			}
		}
		
		if(blankSlots.size() > 0){
			inventory[blankSlots.get(0)] = new ItemStack(s);
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
