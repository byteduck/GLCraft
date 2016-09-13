package net.codepixl.GLCraft.world.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class EntityFallingBlock extends EntitySolid{
	
	private Tile tile = Tile.Sand;
	boolean doneFalling = false;
	
	public Tile getTile(){
		return tile;
	}
	
	public void setTile(Tile t){
		tile = t;
	}
	
	public EntityFallingBlock(float x, float y, float z, WorldManager worldManager) {
		super(x+0.5f, y, z+0.5f, worldManager);
	}
	
	@Override
	public void render(){
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCube(pos.x-0.5f, pos.y, pos.z-0.5f, Color4f.WHITE, tile.getTexCoords(), 1f);
		GL11.glEnd();
	}
	
	@Override
	public AABB getDefaultAABB() {
		return new AABB(0.95f,0.95f,0.95f);
	}
	
	@Override
	public void update(){
		super.update();
		if(onGround && !doneFalling){
			doneFalling = true;
			if(Tile.getTile((byte) worldManager.getTileAtPos(pos)) != Tile.Air){
				if(Tile.getTile((byte) worldManager.getTileAtPos(pos)) == Tile.Water){
					worldManager.setTileAtPos(pos, tile.getId(), true);
				}else{
					worldManager.spawnEntity(new EntityItem(new ItemStack(tile),pos,worldManager));
				}
			}else{
				worldManager.setTileAtPos(pos, tile.getId(), true);
			}
			setDead(true);
		}
	}
	
	@Override
	public void writeToNBT(TagCompound t){
		TagByte b = new TagByte("tile", tile.getId());
		t.setTag(b);
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		Vector3f pos = NBTUtil.vecFromList("Pos",t);
		return new EntityFallingBlock(pos.x, pos.y, pos.z, w);
	}

}
