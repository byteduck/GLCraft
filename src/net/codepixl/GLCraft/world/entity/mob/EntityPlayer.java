package net.codepixl.GLCraft.world.entity.mob;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRANSFORM_BIT;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;
import com.nishu.utils.Time;

import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.sound.SoundManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Ray;
import net.codepixl.GLCraft.util.Raytracer;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.item.tool.Tool;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.material.Material;

public class EntityPlayer extends Mob {
	
	private float breakCooldown, buildCooldown, breakProgress, walkAccel;
	public byte currentTile;
	public ItemStack mouseItem;
	private final float maxU, maxD, speed;
	private int selectedSlot;
	private boolean qPressed, wasBreaking, wasRightClick;
	private Vector3f prevSelect;
	private boolean shouldPlaceTile;
	private Tile tileToPlace;
	private byte metaToPlace;
	public GUISlot hoverSlot;
	private String name;
	
	public EntityPlayer(Vector3f pos, WorldManager w) {
		super(pos, w);
		speed = 1;
		walkAccel = 0;
		maxU = 90;
		maxD = -90;
		breakCooldown = 0;
		buildCooldown = 0;
		breakProgress = 0;
		selectedSlot = 0;
		mouseItem = new ItemStack();
		qPressed = false;
		prevSelect = new Vector3f(-1, -1, -1);
		eyeLevel = 1.6f;
		this.name = "Player"+Constants.randInt(0, 10000);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public AABB getDefaultAABB() {
		return new AABB(0.6f, 1.8f, 0.6f);
	}
	
	@Override
	public int getInventorySize(){
		return 36;
	}
	
	public void update() {
		super.update();
		SoundManager.getMainManager().setPosAndRot(pos, rot);
		this.rot.x = MathUtils.towardsZero(this.rot.x, (float) (Time.getDelta()*30f));
		if(this.isDead()){
			worldManager.showMessage(5.0, DeathMessage.getMessage("Player", getLastDamageSource()));
			this.setDead(false);
			this.dropAllItems();
			this.respawn();
		}
		if(GUIManager.getMainManager().sendPlayerInput()){
			updateMouse();
			updateKeyboard(32, 0.25f);
		}
		updateBreakCooldown();
		updateBuildCooldown();
		Iterator<Entity> i = (Iterator<Entity>) worldManager.getEntityManager().getEntitiesInRadiusOfEntityOfType(this, EntityItem.class, 1f).iterator();
		while(i.hasNext()){
			EntityItem e = (EntityItem) i.next();
			if(e.getCount() > 0) {
				e.setCount(this.addToInventory(e.getItemStack()));
				if(e.getCount() <= 0) {
					e.setDead(true);
				}
			}
		}
		
		if(!wasBreaking) {
			this.breakProgress = 0;
			this.prevSelect.x = -1f;
			this.prevSelect.y = -1f;
			this.prevSelect.z = -1f;
		}else{
			float multiplier = 1;
			if(this.getSelectedItemStack().isItem()){
				Item selItem = this.getSelectedItemStack().getItem();
				if(selItem instanceof Tool){
					multiplier = ((Tool)selItem).getStrengthForMaterial(Tile.getTile(currentTile).getMaterial());
				}
			}
			this.breakProgress += Time.getDelta() * multiplier;
		}
	}
	
	public void respawn(){
		int x = (int) (Constants.CHUNKSIZE*(Constants.worldLengthChunks/2f));
		int z = (int) (Constants.CHUNKSIZE*(Constants.worldLengthChunks/2f));
		int y = Constants.CHUNKSIZE*Constants.worldLengthChunks+1;
		while(Tile.getTile((byte) worldManager.getTileAtPos(x, y-1, z)).canPassThrough() || Tile.getTile((byte) worldManager.getTileAtPos(x, y-1, z)) == Tile.Void){y--;}
		this.setPos(new Vector3f(x,y,z));
		this.health = 20f;
		this.fallDistance = 0;
		this.onFire = 0;
	}
	
	@Override
	public void push(){}
	
	@Override
	public void hurt(float damage, DamageSource source){
			super.hurt(damage, source);
			this.rot.x = 5f;
	}
	
	public void updateMouse() {
		if(Mouse.isGrabbed()) {
			float dx = Mouse.getDX() * speed * 0.16f;
			float dy = Mouse.getDY() * speed * 0.16f;
			int dWheel = Mouse.getDWheel();
			if(Mouse.hasWheel()) {
				for(int i = 0; i < dWheel / 120; i++){
					this.selectedSlot += 1;
					if(this.selectedSlot == 9) {
						this.selectedSlot = 0;
					}
				}
				for(int i = 0; i > dWheel / 120; i--){
					this.selectedSlot -= 1;
					if(this.selectedSlot == -1) {
						this.selectedSlot = 8;
					}
				}
			}
			
			if(getRot().y + dx >= 360) {
				setRotY(getRot().y + dx - 360);
			}else if(getRot().y + dx < 0) {
				setRotY(360 - getRot().y + dx);
			}else{
				setRotY(getRot().y + dx);
			}
			
			if(getRot().z - dy >= maxD && getRot().z - dy <= maxU) {
				setRotZ(getRot().z + -dy);
			}else if(getRot().z - dy < maxD) {
				setRotZ(maxD);
			}else if(getRot().z - dy > maxU) {
				setRotZ(maxU);
			}
		}
		
	}
	public void dropItem(ItemStack item,int amount,Vector3f pos, Vector3f direction, WorldManager w){
		EntityItem e = new EntityItem(new ItemStack(item, amount), pos.x, pos.y, pos.z, w);
		e.setVelocity(MathUtils.RotToVel(direction, 1f));
		w.spawnEntity(e);
	}
	public void dropItem(ItemStack item){
		if(!item.isNull()){
			EntityItem e = new EntityItem(item, pos.x, pos.y+this.eyeLevel, pos.z, worldManager);
			e.setVelocity(MathUtils.RotToVel(this.getRot(), 1f));
			worldManager.spawnEntity(e);
		}
	}
	public void updateKeyboard(float delay, float speed) {
		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
		boolean q = Keyboard.isKeyDown(Keyboard.KEY_Q);
		if(q && !qPressed && !getInventory()[selectedSlot].isNull()) {
			EntityItem e = new EntityItem(new ItemStack(getInventory()[selectedSlot], 1), getPos().x, getPos().y + 1.5f, getPos().z, worldManager);
			e.setVelocity(MathUtils.RotToVel(this.getRot(), 1f));
			if(ctrl) {
				e.setCount(getInventory()[selectedSlot].count);
				getInventory()[selectedSlot] = new ItemStack();
			}else{
				int sub = getInventory()[selectedSlot].subFromStack(1);
				if(sub > 0) {
					getInventory()[selectedSlot] = new ItemStack();
				}
			}
			worldManager.spawnEntity(e);
		}
		qPressed = q;
		if(keyUp && keyRight && !keyLeft && !keyDown) {
			Pmove(speed * delay * (float) Time.getDelta(), 0, -speed * delay * (float) Time.getDelta());
		}
		if(keyUp && keyLeft && !keyRight && !keyDown) {
			Pmove(-speed * delay * (float) Time.getDelta(), 0, -speed * delay * (float) Time.getDelta());
		}
		if(keyUp && !keyLeft && !keyRight && !keyDown) {
			Pmove(0, 0, -speed * delay * (float) Time.getDelta());
		}
		if(keyDown && keyLeft && !keyRight && !keyUp) {
			Pmove(-speed * delay * (float) Time.getDelta(), 0, speed * delay * (float) Time.getDelta());
		}
		if(keyDown && keyRight && !keyLeft && !keyUp) {
			Pmove(speed * delay * (float) Time.getDelta(), 0, speed * delay * (float) Time.getDelta());
		}
		if(keyDown && !keyUp && !keyLeft && !keyRight) {
			Pmove(0, 0, speed * delay * (float) Time.getDelta());
		}
		if(keyLeft && !keyRight && !keyUp && !keyDown) {
			Pmove(-speed * delay * (float) Time.getDelta(), 0, 0);
		}
		if(keyRight && !keyLeft && !keyUp && !keyDown) {
			Pmove(speed * delay * (float) Time.getDelta(), 0, 0);
		}
		if(!keyRight && !keyLeft && !keyUp && !keyDown){
			walkAccel = MathUtils.towardsZero(walkAccel, (float)Time.getDelta()*3f);
		}else{
			walkAccel = MathUtils.towardsValue(walkAccel, (float)Time.getDelta()*3f, 1);
		}
		if(space) {
			this.jump();
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_1)) {
			this.selectedSlot = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_2)) {
			this.selectedSlot = 1;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_3)) {
			this.selectedSlot = 2;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_4)) {
			this.selectedSlot = 3;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_5)) {
			this.selectedSlot = 4;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_6)) {
			this.selectedSlot = 5;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_7)) {
			this.selectedSlot = 6;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_8)) {
			this.selectedSlot = 7;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_9)) {
			this.selectedSlot = 8;
		}
	}
	
	public void Pmove(float x, float y, float z){
		float toZ = walkAccel * (float) ((x * (float) Math.cos(Math.toRadians(getRot().y - 90)) + z * Math.cos(Math.toRadians(getRot().y))));
		float toX = walkAccel * (float) (-(x * (float) Math.sin(Math.toRadians(getRot().y - 90)) + z * Math.sin(Math.toRadians(getRot().y))));
		float toY = 0;
		if(toZ != 0 && toX != 0){
			//SoundManager.getMainManager().quickPlayOnce("walk.grass");
		}
		move(toX, toY, toZ);
	}
	
	public void render() {
		
	}
	
	public void dispose() {
	
	}
	
	public float getBreakCooldown() {
		return breakCooldown;
	}
	
	private void updateBreakCooldown() {
		if(breakCooldown - Time.getDelta() > 0) {
			breakCooldown -= Time.getDelta();
		}else{
			breakCooldown = 0;
		}
	}
	
	private void updateBuildCooldown() {
		if(buildCooldown - Time.getDelta() > 0) {
			buildCooldown -= Time.getDelta();
		}else{
			buildCooldown = 0;
		}
	}
	
	public void setBreakCooldown(float cooldown) {
		breakCooldown = cooldown;
	}
	
	public void setBuildCooldown(float cooldown) {
		buildCooldown = cooldown;
	}
	
	public void applyTranslations() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(getRot().z, 1, 0, 0);
		glRotatef(getRot().y, 0, 1, 0);
		glRotatef(getRot().x, 0, 0, 1);
		glTranslatef(-getX(), -(getY() + 1.6f), -getZ());
		glPopAttrib();
	}
	
	public void reverseTranslations() {
		glRotatef(getRot().z, -1, 0, 0);
		glRotatef(getRot().y, 0, -1, 0);
		glRotatef(getRot().x, 0, 0, -1);
	}
	
	public float[] getTexCoordsForHealthIndex(int i){
		if(this.health-((float)i*2f) >= 0){
			if(this.health-((float)i*2f) <= 1){
				return TextureManager.texture("gui.heart_half");
			}
			return TextureManager.texture("gui.heart");
		}
		return TextureManager.texture("gui.heart_empty");
	}
	
	public float getBuildCooldown() {
		return buildCooldown;
	}
	
	public int getSelectedSlot() {
		return selectedSlot;
	}
	
	public void setSelectedSlot(int slot) {
		selectedSlot = slot;
	}
	
	public ItemStack getSelectedItemStack() {
		return getInventory()[selectedSlot];
	}
	
	public void setSelectedItemStack(ItemStack stack) {
		getInventory()[selectedSlot] = stack;
	}
	
	public Vector3f getLookRayPos(){
		Ray r = Raytracer.getScreenCenterRay();
		boolean loop = true;
		while(loop && r.distance < 10){
			if(worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z) == -1 || worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z) == 0) {
				r.next();
			}else{
				r.prev();
				loop = false;
			}
			r.next();
		}
		return r.pos;
	}
	
	public int raycast() {
		Ray r = Raytracer.getScreenCenterRay();
		int tile = -1;
		while(r.distance < 10){
			if(worldManager.doneGenerating) {
				if(worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z) == -1 || worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z) == 0 || Tile.getTile((byte) worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z)).getMaterial() == Material.LIQUID) {
					r.next();
				}else{
					tile = worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z);
					Vector3f tpos = new Vector3f(r.pos);
					if(Tile.getTile((byte)tile).customHitbox()){
						AABB ray = new AABB(0,0,0);
						ray.update(r.pos);
						AABB block = Tile.getTile((byte)tile).getAABB((int)tpos.x, (int)tpos.y, (int)tpos.z, worldManager.getMetaAtPos(tpos), worldManager);
						if(!AABB.testAABB(block, ray)){
							r.next();
							continue;
						}
					}
					// System.out.println(worldManager.getTileAtPos((int)r.pos.x,
					// (int)r.pos.y, (int)r.pos.z));
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					GL11.glLineWidth(0.5f);
					if(Tile.getTile((byte) tile).getRenderType() == RenderType.CUBE) {
						glBegin(GL11.GL_QUADS);
						Shape.createTexturelessCube((int) r.pos.x - 0.0005f, (int) r.pos.y - 0.0005f, (int) r.pos.z - 0.0005f, new Color4f(0, 0, 0, 1f), 1.001f);
						glEnd();
					}else if(Tile.getTile((byte) tile).getRenderType() == RenderType.CROSS){
						glBegin(GL11.GL_QUADS);
						Shape.createTexturelessCross((int) r.pos.x - 0.0005f, (int) r.pos.y - 0.0005f, (int) r.pos.z - 0.0005f, new Color4f(0, 0, 0, 1f), 1.001f);
						glEnd();
					}else if(Tile.getTile((byte) tile).getRenderType() == RenderType.FLAT){
						glBegin(GL11.GL_QUADS);
						Shape.createTexturelessFlat((int) r.pos.x - 0.0005f, (int) r.pos.y + 0.1f, (int) r.pos.z - 0.0005f, new Color4f(0, 0, 0, 1f), 1.001f);
						glEnd();
					}else if(Tile.getTile((byte)tile).getRenderType() == RenderType.CUSTOM){
						Tile.getTile((byte)tile).renderHitbox(r.pos);
					}
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
					if(!this.prevSelect.equals(new Vector3f((int) r.pos.x, (int) r.pos.y, (int) r.pos.z))) {
						this.breakProgress = 0f;
					}
					this.prevSelect = new Vector3f((int) r.pos.x, (int) r.pos.y, (int) r.pos.z);
					if(this.wasBreaking) {
						float percent = this.breakProgress / Tile.getTile((byte) tile).getHardness();
						Tile t = Tile.getTile((byte) tile);
						if(t.getRenderType() == RenderType.CUBE) {
							glBegin(GL_QUADS);
							Shape.createCube((int) r.pos.x - 0.001f, (int) r.pos.y - 0.001f, (int) r.pos.z - 0.001f, new Color4f(1, 1, 1, 1f), breakingTexCoords(percent), 1.002f);
							glEnd();
						}else if(t.getRenderType() == RenderType.CROSS){
							glBegin(GL_QUADS);
							Shape.createCross((int) r.pos.x - 0.001f, (int) r.pos.y - 0.001f, (int) r.pos.z - 0.001f, new Color4f(1, 1, 1, 1f), breakingTexCoords(percent), 1.001f);
							glEnd();
						}else if(t.getRenderType() == RenderType.FLAT){
							glBegin(GL_QUADS);
							Shape.createFlat((int) r.pos.x, (int) r.pos.y + 0.001f, (int) r.pos.z, new Color4f(1, 1, 1, 1f), breakingTexCoords(percent), 1f);
							glEnd();
						}else{
							if(t.getCustomRenderType() == RenderType.CUBE) {
								glBegin(GL_QUADS);
								Shape.createCube((int) r.pos.x - 0.001f, (int) r.pos.y - 0.001f, (int) r.pos.z - 0.001f, new Color4f(1, 1, 1, 1f), breakingTexCoords(percent), 1.002f);
								glEnd();
							}else if(t.getCustomRenderType() == RenderType.CROSS){
								glBegin(GL_QUADS);
								Shape.createCross((int) r.pos.x - 0.001f, (int) r.pos.y - 0.001f, (int) r.pos.z - 0.001f, new Color4f(1, 1, 1, 1f), breakingTexCoords(percent), 1.001f);
								glEnd();
							}else if(t.getCustomRenderType() == RenderType.FLAT){
								glBegin(GL_QUADS);
								Shape.createFlat((int) r.pos.x, (int) r.pos.y + 0.001f, (int) r.pos.z, new Color4f(1, 1, 1, 1f), breakingTexCoords(percent), 1f);
								glEnd();
							}
						}
					}
					if(Mouse.isButtonDown(0) && getBreakCooldown() == 0f && GUIManager.getMainManager().sendPlayerInput()) {
						this.wasBreaking = true;
						if(Tile.getTile((byte) worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z)).getHardness() <= this.breakProgress) {
							Tile.getTile((byte) worldManager.getTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z)).onBreak((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, true, new BreakSource(this), worldManager);
							worldManager.setTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, (byte) 0, new BreakSource(this), true);
							setBreakCooldown(0.05f);
							this.breakProgress = 0f;
						}
					}else{
						this.wasBreaking = false;
					}
					r.prev();
					//BUILD
					if(Mouse.isButtonDown(1) && GUIManager.getMainManager().sendPlayerInput() && worldManager.getEntityManager().getPlayer().getBuildCooldown() == 0f/** && worldManager.getTileAtPos(r.pos) == 0**/) {
						if(Tile.getTile((byte) tile).onClick((int)tpos.x, (int)tpos.y, (int)tpos.z, this, worldManager)){
							setBuildCooldown(0.2f);
						}else{
							if(!worldManager.getEntityManager().getPlayer().getSelectedItemStack().isNull() && worldManager.getEntityManager().getPlayer().getSelectedItemStack().isTile()){
								AABB blockaabb = getSelectedItemStack().getTile().getAABB((int)r.pos.x, (int)r.pos.y, (int)r.pos.z, getSelectedItemStack().getMeta(), worldManager);
								if(!AABB.testAABB(blockaabb, getAABB()) && worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().canPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, worldManager)) {
									while((!Tile.getTile((byte) worldManager.getTileAtPos(r.pos)).canBePlacedOver() || AABB.testAABB(blockaabb, getAABB()) && worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().canPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, worldManager)) && !r.pos.equals(r.origPos)){
										r.prev();
										blockaabb = getSelectedItemStack().getTile().getAABB((int)r.pos.x, (int)r.pos.y, (int)r.pos.z, getSelectedItemStack().getMeta(), worldManager);
									}
									if(!r.origPos.equals(r.pos) && worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().canPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, worldManager)){
										worldManager.setTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, this.getSelectedItemStack().getTile().getId(), true, this.getSelectedItemStack().getMeta());
										worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().onPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, this.getEnumFacing(), worldManager);
										int sub = worldManager.getEntityManager().getPlayer().getSelectedItemStack().subFromStack(1);
										if(sub > 0) {
											worldManager.getEntityManager().getPlayer().getInventory()[worldManager.getEntityManager().getPlayer().getSelectedSlot()] = new ItemStack();
										}
										setBuildCooldown(0.2f);
										r.next();
									}
									
								}
							}else if(!worldManager.getEntityManager().getPlayer().getSelectedItemStack().isNull() && worldManager.getEntityManager().getPlayer().getSelectedItemStack().isItem() && !wasRightClick){
								worldManager.getEntityManager().getPlayer().getSelectedItemStack().getItem().onClick(this);
							}
						}
					}else if(shouldPlaceTile){
						AABB blockaabb = tileToPlace.getAABB((int)r.pos.x, (int)r.pos.y, (int)r.pos.z, metaToPlace, worldManager);
						if(!AABB.testAABB(blockaabb, getAABB()) && worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().canPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, worldManager)) {
							while((worldManager.getTileAtPos(r.pos) != Tile.Air.getId() || AABB.testAABB(blockaabb, getAABB()) && worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().canPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, worldManager)) && !r.pos.equals(r.origPos)){
								r.prev();
								blockaabb.update(new Vector3f(((int) r.pos.x) + 0.5f, (int) r.pos.y, ((int) r.pos.z) + 0.5f));
							}
							if(!r.origPos.equals(r.pos)){
								worldManager.setTileAtPos((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, tileToPlace.getId(), true, metaToPlace);
								worldManager.getEntityManager().getPlayer().getSelectedItemStack().getTile().onPlace((int) r.pos.x, (int) r.pos.y, (int) r.pos.z, this.getEnumFacing(), worldManager);
							}
							setBuildCooldown(0.2f);
							r.next();
						}
						shouldPlaceTile = false;
					}
					r.distance = 11;
				}
				r.next();
			}else{
				return -1;
			}
		}
		this.currentTile = (byte)tile;
		wasRightClick = Mouse.isButtonDown(1);
		return tile;
	}
	
	public Tile tileAtEye(){
		return Tile.getTile((byte)worldManager.getTileAtPos(this.pos.x,this.pos.y+1.52f,this.pos.z));
	}
	
	private static float[] breakingTexCoords(float percent) {
		float[] coords = TextureManager.texture("misc.break_"+Math.round((percent*100)/12.5f));
		if(coords == null){
			coords = TextureManager.texture("misc.break_7");
		}
		return coords;
	}

	public void placeTile(Tile tile, byte meta) {
		shouldPlaceTile = true;
		tileToPlace = tile;
		metaToPlace = meta;
	}

	public float[] getTexCoordsForAirIndex(int i) {
		if(this.airLevel-((float)i) > 0){
			return TextureManager.texture("gui.bubble");
		}
		return TextureManager.texture("misc.nothing");
	}

	public String getName() {
		return this.name;
	}
	
}
