package net.codepixl.GLCraft.world.entity.mob;

import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.network.packet.PacketSetInventory;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;

public class EntityPlayerMP extends EntityPlayer{

	public EntityPlayerMP(Vector3f pos, WorldManager w) {
		super(pos, w);
	}
	
	@Override
	public void render(){}
	
	@Override
	public void update(){
		boolean needsInventoryUpdate = false;
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
		if(needsInventoryUpdate)
			worldManager.sendPacket(new PacketSetInventory(this));
	}
	
	@Override
	public void updateMouse(){}
	
	@Override
	public void updateKeyboard(float a, float b){}

}
