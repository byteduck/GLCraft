package net.codepixl.GLCraft.world.entity;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.item.Item;
import net.codepixl.GLCraft.item.ItemStack;
import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityItem extends EntitySolid{
	private ItemStack itemstack;
	private float yPos;
	private final float size = 0.3f;
	
	public EntityItem(ItemStack s, float x, float y, float z, WorldManager worldManager) {
		super(x,y,z, worldManager);
		itemstack = s;
		yPos = 0;
	}
	
	public EntityItem(ItemStack s, Vector3f pos, WorldManager worldManager) {
		super(pos.x,pos.y,pos.z,worldManager);
		itemstack = s;
		yPos = 0;
	}
	
	public EntityItem(Vector3f pos, Vector3f rot, Vector3f vel, TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		super(pos.x,pos.y,pos.z,w);
		this.rot = rot;
		this.vel = vel;
		yPos = 0;
		if(t.getByte("isItem") > 0){
			itemstack = new ItemStack(Item.getItem(t.getByte("id")),(int)t.getByte("count"));
		}else{
			itemstack = new ItemStack(Tile.getTile(t.getByte("id")),(int)t.getByte("count"));
		}
	}

	@Override
	public void render(){
		int count = this.itemstack.count/16;
		if(count == 0){
			count = 1;
		}
		if(this.itemstack.count > 1){
			count+=1;
		}
		for(int i = 0; i < count; i++){
			GL11.glPushMatrix();
			GL11.glTranslatef(getX(), getY(), getZ());
			GL11.glRotatef(this.getRot().y, 0f, 1.0f, 0f);
			if(itemstack.isTile()){
				GL11.glTranslatef(-size/2, 0f, -size/2f);
			}else{
				GL11.glTranslatef(-size/2, 0f, 0f);
			}
			float[] texCoords = TextureManager.itemStack(itemstack);
			GL11.glBegin(GL11.GL_QUADS);
			if(itemstack.isTile()){
				GL11.glRotatef(this.getRot().y, 0f, 1.0f, 0f);
				Tile tile = itemstack.getTile();
				if(tile.getRenderType() == RenderType.CUBE){
					Shape.createCube(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), tile.getColor(), texCoords, size);
				}else if(tile.getRenderType() == RenderType.CROSS){
					Shape.createCross(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), tile.getColor(), texCoords, size);
				}else if(tile.getRenderType() == RenderType.FLAT || tile.getRenderType() == RenderType.CUSTOM){
					Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), tile.getColor(), texCoords, size);
				}
			}else{
				Item item = itemstack.getItem();
				Shape.createPlane(0+((float)i*0.05f), yPos+((float)i*0.05f), 0+((float)i*0.05f), item.getColor(), texCoords, size);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
		}
	}
	
	@Override
	public void update(){
		super.update();
		if(this.getCount() <= 0){
			this.setDead(true);
		}
		if(!Tile.getTile((byte)worldManager.getTileAtPos(this.getPos())).canPassThrough()){
			this.setY((float) (this.getY()+Time.getDelta()*2));
		}
		if(timeAlive % 2000 <=1000){
			yPos = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0f, 0.3f, 1f);
		}else{
			yPos = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0.3f, -0.3f, 1f);
		}
		Iterator<Entity> i = worldManager.getEntityManager().getEntitiesInRadiusOfEntityOfType(this, EntityItem.class, 1.5f).iterator();
		while(i.hasNext()){
			EntityItem e = (EntityItem)i.next();
			if(e.itemstack.count+this.itemstack.count <= 64 && e.itemstack.compatible(this.itemstack) && e.timeAlive <= this.timeAlive){
				e.setDead(true);
				this.itemstack.count+=e.itemstack.count;
			}
		}
		this.setRotY((float) (this.getRot().y+Time.getDelta()*50));
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.3f,0.3f,0.3f);
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		t.setTag(new TagByte("isItem",(byte) (this.itemstack.isItem() ? 1 : 0 )));
		t.setTag(new TagByte("id",this.itemstack.getId()));
		t.setTag(new TagByte("count",(byte)this.itemstack.count));
	}
	
	public ItemStack getItemStack(){
		return this.itemstack;
	}
	
	public int getCount(){
		return this.itemstack.count;
	}
	
	public void setCount(int i){
		this.itemstack.count = i;
	}
	
	public void addCount(int i){
		this.itemstack.count+=i;
	}
	
}
