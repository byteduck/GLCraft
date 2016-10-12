package net.codepixl.GLCraft.world.entity.mob;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.network.packet.PacketRespawn;
import net.codepixl.GLCraft.network.packet.PacketSetInventory;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityPlayerMP extends EntityPlayer{

	public boolean needsInventoryUpdate = false;

	public EntityPlayerMP(Vector3f pos, WorldManager w) {
		super(pos, w);
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
		GL11.glRotatef(-rot.y, 0, 1, 0);
		GL11.glTranslatef(-(float)getAABB().r[0], 0, -(float)getAABB().r[2]);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createRect(0,0,0, new Color4f(light,light,light,1f), Tile.Bedrock.getTexCoords(), (float)getAABB().r[0]*2f, (float)getAABB().r[1]*2f, (float)getAABB().r[2]*2f);
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glTranslatef(pos.x, pos.y+(float)getAABB().r[0]*2f+1.75f, pos.z);
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glRotatef(worldManager.getPlayer().getRot().y, 0, 1, 0);
		GL11.glTranslatef(-Constants.FONT.getWidth(getName())/2*0.02f, 0, 0);
		GL11.glScalef(0.02f, 0.02f, 0.02f);
		TextureImpl.bindNone();
		Constants.FONT.drawString(0, 0, getName());
		TextureImpl.unbind();
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}
	
	@Override
	public void update(){
		if(!shouldUpdate)
			return;
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
	public void clientUpdate(){
		this.light = worldManager.getLightIntensity((int)this.pos.x, (int)this.pos.y, (int)this.pos.z);
	}
	
	@Override
	public void updateMouse(){}
	
	@Override
	public void respawn(){
		this.setDead(false);
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
