package net.codepixl.GLCraft.world.entity;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.*;
import com.nishu.utils.Color4f;
import com.nishu.utils.Time;
import net.codepixl.GLCraft.network.packet.PacketPlayerPos;
import net.codepixl.GLCraft.network.packet.PacketUpdateEntity;
import net.codepixl.GLCraft.util.EnumFacing;
import net.codepixl.GLCraft.util.GameObj;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.TileFire;
import net.codepixl.GLCraft.world.tile.TileLava;
import net.codepixl.GLCraft.world.tile.TileWater;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Entity implements GameObj{
	protected Vector3f pos, rot;
	private Vector3f vel;
	public Vector3f lpos = new Vector3f(), lrot = new Vector3f(), lvel = new Vector3f();
	protected int id;
	public WorldManager worldManager;
	protected boolean dead = false;
	public long timeAlive = 0;
	public float onFire = 0;
	public float light = 0f;
	public boolean needsDataUpdate = false;
	private float posPacketTimer = 0;
	
	public Entity(float x, float y, float z, WorldManager worldManager){
		this.pos = new Vector3f(x,y,z);
		this.rot = new Vector3f(0,0,0);
		this.setVelocity(new Vector3f(0,0,0));
		this.id = worldManager.getEntityManager().getNewId();
		this.worldManager = worldManager;
	}
	
	public Entity(float x, float y, float z, float rx, float ry, float rz, WorldManager worldManager){
		this.pos = new Vector3f(x,y,z);
		this.rot = new Vector3f(rx,ry,rz);
		this.setVelocity(new Vector3f(0,0,0));
		this.id = worldManager.getEntityManager().getNewId();
		this.worldManager = worldManager;
	}
	
	public Entity(Vector3f pos, Vector3f rot, Vector3f vel, WorldManager worldManager){
		this.pos = pos;
		this.rot = rot;
		this.setVelocity(vel);
		this.id = worldManager.getEntityManager().getNewId();
		this.worldManager = worldManager;
	}
	
	public int getID(){
		return id;
	}
	
	public Vector3f getPos(){
		return pos;
	}
	
	public Vector3f getRot(){
		return rot;
	}
	
	public void setPos(Vector3f pos){
		this.pos.set(pos);
	}
	
	public void setPos(float x, float y, float z){
		this.pos.x = x;
		this.pos.y = y;
		this.pos.z = z;
	}
	
	public Color4f getColor(){
		return new Color4f(this.light, this.light, this.light, 1f);
	}
	
	public void setRot(Vector3f rot){
		this.rot = rot;
	}
	
	public float getX(){
		return pos.x;
	}
	
	public float getY(){
		return pos.y;
	}
	
	public void setRotX(float r){
		rot.x = r;
	}
	
	public void setRotY(float r){
		rot.y = r;
	}
	
	public void setRotZ(float r){
		rot.z = r;
	}
	
	public float getZ(){
		return pos.z;
	}
	
	public void setX(float x){
		pos.x = x;
	}
	
	public void setY(float y){
		pos.y = y;
	}
	
	public void setZ(float z){
		pos.z = z;
	}

	public Vector3f getVelocity() {
		return getVel();
	}

	public void setVelocity(Vector3f vel) {
		this.setVel(vel);
	}
	
	public final TagCompound mainWriteToNBT(){
		TagCompound t = new TagCompound("");
		TagList posList = new TagList("Pos");
		posList.addTag(new TagFloat("",this.pos.x));
		posList.addTag(new TagFloat("",this.pos.y));
		posList.addTag(new TagFloat("",this.pos.z));
		TagList rotList = new TagList("Rot");
		rotList.addTag(new TagFloat("",this.rot.x));
		rotList.addTag(new TagFloat("",this.rot.y));
		rotList.addTag(new TagFloat("",this.rot.z));
		TagList velList = new TagList("Vel");
		velList.addTag(new TagFloat("",this.getVel().x));
		velList.addTag(new TagFloat("",this.getVel().y));
		velList.addTag(new TagFloat("",this.getVel().z));
		TagLong timeTag = new TagLong("TimeAlive",timeAlive);
		TagString typeTag = new TagString("type",this.getClass().getSimpleName());
		t.setTag(posList);
		t.setTag(rotList);
		t.setTag(velList);
		t.setTag(timeTag);
		t.setTag(typeTag);
		writeToNBT(t);
		return t;
	}
	
	public void writeToNBT(TagCompound t){
		
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException {
		System.err.println("ENTITY "+t.getString("type")+" IS MISSING fromNBT METHOD!");
		return null;
	}

	@Override
	public void update(){
		if(this.needsDataUpdate && !(this instanceof EntityPlayer)){
			worldManager.sendPacket(new PacketUpdateEntity(this,PacketUpdateEntity.Type.UPDATENBT));
			this.needsDataUpdate = false;
		}
		if(this.onFire>0f){
			this.onFire-=Time.getDelta();
		}else{
			this.onFire = 0f;
		}
		Tile t = Tile.getTile((byte)worldManager.getTileAtPos(pos));
		t.onCollide((int)pos.x, (int)pos.y, (int)pos.z, worldManager, this);
		if(t instanceof TileFire){
			this.onFire = 7f;
		}else if(t instanceof TileLava){
			this.onFire = 7f;
		}else if(t instanceof TileWater){
			this.onFire = 0;
		}
		voidHurt();
		posPacketTimer += Time.getDelta();
		if(posPacketTimer > 0.05){
			boolean update = false;
			if(!MathUtils.equals(pos, lpos, 0.005f))
				update = true;
			if(!update && !MathUtils.equals(rot, lrot, 0.005f))
				update = true;
			if(!update && !MathUtils.equals(vel, lvel, 0.005f))
				update = true;
			if(update){
				if(!(this instanceof EntityPlayer))
					worldManager.sendPacket(new PacketUpdateEntity(this, PacketUpdateEntity.Type.POSITION));
				else
					worldManager.sendPacket(new PacketPlayerPos((EntityPlayer)this));
				posPacketTimer = 0;
			}
			lpos = new Vector3f(this.getPos());
			lvel = new Vector3f(this.getVel());
			lrot = new Vector3f(this.getRot());
		}
	}
	
	public void clientUpdate(){
		this.light = worldManager.getLightIntensity((int)this.pos.x, (int)this.pos.y, (int)this.pos.z);
		timeAlive+=(Time.getDelta()*1000f);
		MathUtils.modulus(this.rot, 360f);
	}

	@Override
	public void render() {
		
	}

	@Override
	public void dispose() {
		
	}

	public boolean isDead() {
		// TODO Auto-generated method stub
		return dead;
	}
	
	public void setDead(boolean isDead){
		this.dead = isDead;
		this.onFire = 0;
	}

	protected void voidHurt() {
		if(this.getY() < -5){
			this.setDead(true);
		}
	}

	public void setFire(float Time) {
		this.onFire = Time;
	}

	public Vector3f getVel() {
		return vel;
	}

	public void setVel(Vector3f vel) {
		this.vel = vel;
	}
	
	public void setVel(float x, float y, float z){
		this.vel.x = x;
		this.vel.y = y;
		this.vel.z = z;
	}
	
	public EnumFacing getEnumFacing(){
		if(this.getRot().z <= 0){ //up
			if(this.getRot().y >= 315 || this.getRot().y < 45)
				return EnumFacing.UPN;
			else if(this.getRot().y >= 45 && this.getRot().y < 135)
				return EnumFacing.UPW;
			else if(this.getRot().y >= 135 && this.getRot().y < 225)
				return EnumFacing.UPS;
			else
				return EnumFacing.UPE;
		}else{ //down
			if(this.getRot().y >= 315 || this.getRot().y < 45)
				return EnumFacing.DOWNN;
			else if(this.getRot().y >= 45 && this.getRot().y < 135)
				return EnumFacing.DOWNW;
			else if(this.getRot().y >= 135 && this.getRot().y < 225)
				return EnumFacing.DOWNS;
			else
				return EnumFacing.DOWNE;
		}
	}

	public void setId(int id) {
		worldManager.entityManager.changeEntityID(this.id, id);
		this.id = id;
	}

	public void setRot(float x, float y, float z) {
		this.rot.x = x;
		this.rot.y = y;
		this.rot.z = z;
	}
	
	public void needsDataUpdate(){
		this.needsDataUpdate  = true;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof Entity && ((Entity)obj).getID() == getID();
	}

	public float getShadowSize(){ return 0.3f; }

	public void renderShadow(){
		int i = 0;
		while(Tile.getTile(worldManager.getTileAtPos(pos.x, pos.y-i, pos.z)).isTransparent() && i < 5)
			i++;
		final float ypos = (float)Math.floor(pos.y-i+1)+0.01f;
		GL11.glColor4f(0,0,0,(MathUtils.distance(pos, new Vector3f(pos.x, ypos, pos.z))*-1+5f)/10f);
		GL11.glBegin(GL11.GL_POLYGON);
		float shadowSize = getShadowSize();
		for( int n = 30; n >= 0; n-- ) {
			float t = 2 * (float)Math.PI * (float)n /30f;
			GL11.glVertex3f(pos.x + (float)Math.sin(t) * shadowSize, ypos, pos.z + (float)Math.cos(t) * shadowSize);
		}
		GL11.glEnd();
	}
	
}
