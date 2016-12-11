package net.codepixl.GLCraft.world.entity.particle;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.evilco.mc.nbt.tag.TagString;
import com.nishu.utils.Time;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntitySolid;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class Particle extends EntitySolid{
	
	private float texCoords[];
	private float size;
	private float lifetime;
	private String texName;
	
	public Particle(Vector3f pos, Vector3f vel, WorldManager w){
		super(pos,new Vector3f(0,0,0),new Vector3f(0,0,0),w);
		this.texCoords = Tile.Lava.getTexCoords();
		this.size = 0.2f;
		this.lifetime = 1.0f;
		this.setVel(vel);
		RegisterParicle();
		
	}
	
	public void setTexCoords(float[] tc){
		this.texCoords = tc;
	}
	
	public void setTexName(String name){
		this.texCoords = TextureManager.texture(name);
		this.texName = name;
	}
	
	public void setSize(float size){
		this.size = size;
	}
	
	@Override
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslatef(getX(), getY()+this.size, getZ());
		GL11.glRotatef(-this.rot.y, 0.0f, 1.0f, 0f);
		GL11.glRotatef(-this.rot.z, 1.0f, 0f, 0f);
		GL11.glRotatef(180, 0f, 0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createSprite(0,0,0, this.getColor(), this.texCoords, this.size);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	@Override
	public void update(){
		super.update();
		this.lifetime -= Time.getDelta();
		if(this.lifetime < 0){
			this.setDead(true);
		}
		List<Entity> e = worldManager.entityManager.getEntitiesInRadiusOfEntityOfType(this, EntityPlayer.class, 200f);
		if(e.size() != 0){
			EntityPlayer player = (EntityPlayer) e.get(0);
			Vector3f trot = player.getRot();
			this.setRot(trot);
		}
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.2f, 0.2f, 0.2f);
	}
	
	public void RegisterParicle(){
		
	}

	public void setLifeTime(float l) {
		this.lifetime = l;
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		TagFloat tSize = new TagFloat("size",size);
		TagFloat tLifetime = new TagFloat("lifetime",lifetime);
		String texName = this.texName == null ? "" : this.texName;
		TagString tTexName = new TagString("texName",texName);
		t.setTag(tSize);
		t.setTag(tLifetime);
		t.setTag(tTexName);
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		Particle p = new Particle(NBTUtil.vecFromList("Pos", t), NBTUtil.vecFromList("Vel", t), w);
		p.setTexName(t.getString("texName"));
		p.setSize(t.getFloat("size"));
		p.setLifeTime(t.getFloat("lifetime"));
		return p;
	}

	@Override
	public void renderShadow(){}
	
}
