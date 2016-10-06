package net.codepixl.GLCraft.world.entity.mob;

import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.network.packet.PacketRespawn;
import net.codepixl.GLCraft.network.packet.PacketSetInventory;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.ItemStack;

public class EntityPlayerMP extends EntityPlayer{

	public boolean needsInventoryUpdate = false;

	public EntityPlayerMP(Vector3f pos, WorldManager w) {
		super(pos, w);
	}
	
	@Override
	public void render(){}
	
	@Override
	public void update(){
		Iterator<Entity> i = (Iterator<Entity>) worldManager.getEntityManager().getEntitiesInRadiusOfEntityOfType(this, EntityItem.class, 1f).iterator();
		while(i.hasNext()){
			needsInventoryUpdate = true;
			EntityItem e = (EntityItem) i.next();
			if(e.getCount() > 0) {
				e.setCount(this.addToInventory(e.getItemStack()));
				if(e.getCount() <= 0) {
					e.setDead(true);
				}
			}
		}
		if(needsInventoryUpdate){
			worldManager.sendPacket(new PacketSetInventory(this));
			this.needsInventoryUpdate = false;
		}
		if(this.isDead()){
			this.respawn();
		}
	}
	
	@Override
	public void updateMouse(){}
	
	@Override
	public void respawn(){
		worldManager.sendPacket(new PacketRespawn(),this);
	}
	
	@Override
	public void updateKeyboard(float a, float b){}

	public void dropHeldItem(boolean all) {
		ItemStack i = new ItemStack(this.getSelectedItemStack());
		if(!i.isNull()){
			if(!all)
				i.count = 1;
			EntityItem e = new EntityItem(i, pos.x, pos.y+this.eyeLevel, pos.z, worldManager);
			
			e.setVelocity(MathUtils.RotToVel(this.getRot(), 1f));
			worldManager.spawnEntity(e);
		}
		if(all)
			this.setSelectedItemStack(new ItemStack());
		else{
			this.getSelectedItemStack().count--;
			if(this.getSelectedItemStack().count <= 0)
				this.setSelectedItemStack(new ItemStack());
		}
		this.needsInventoryUpdate = true;
	}

}
