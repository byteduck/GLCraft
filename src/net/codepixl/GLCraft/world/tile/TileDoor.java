package net.codepixl.GLCraft.world.tile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileDoor extends Tile{
	
	public TileDoor(){
		super();
		TextureManager.addTexture("tiles.door_top", TextureManager.TILES+"door_top.png");
		TextureManager.addTexture("tiles.door_bottom", TextureManager.TILES+"door_bottom.png");
	}
	
	@Override
	public String getName(){
		return "Door";
	}
	
	@Override
	public String getTextureName(){
		return "door_icon";
	}
	
	@Override
	public byte getId(){
		return 29;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CUSTOM;
	}
	
	@Override
	public RenderType getCustomRenderType(){
		return RenderType.CUSTOM;
	}
	
	@Override
	public Material getMaterial(){
		return Material.WOOD;
	}
	
	@Override
	public boolean isTransparent(){
		return true;
	}
	
	@Override
	public boolean onClick(int x, int y, int z, EntityPlayer p, WorldManager w){
		return true;
	}
	
	@Override
	public boolean customHitbox(){
		return true;
	}
	
	@Override
	public AABB getAABB(int x, int y, int z, byte meta, WorldManager w){
		return new AABB(0.0625f,1,1).update(new Vector3f(x,y,z+0.5f));
	}
	
	@Override
	public ItemStack getDrop(int x, int y, int z, BreakSource source, WorldManager w){
		return new ItemStack(this, 1, (byte)0);
	}
	
	@Override
	public boolean canPlace(int x, int y, int z, WorldManager w){
		return Tile.getTile((byte) w.getTileAtPos(x, y+1, z)) == Tile.Air;
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, BreakSource source, WorldManager worldManager){
		super.onBreak(x, y, z, drop, source, worldManager);
		if(source.type == BreakSource.Type.PLAYER){
			if(worldManager.getMetaAtPos(x, y, z) == 0)
				worldManager.setTileAtPos(x, y+1, z, Tile.Air.getId(), true);
			else if(worldManager.getMetaAtPos(x, y, z) == 1)
				worldManager.setTileAtPos(x, y-1, z, Tile.Air.getId(), true);
		}
	}
	
	@Override
	public void onPlace(int x, int y, int z, WorldManager w){
		if(w.getMetaAtPos(x,y,z) != 1)
			w.setTileAtPos(x, y+1, z, getId(), true, (byte)1);
	}
	
	@Override
	public void customRender(float x, float y, float z, WorldManager w, Chunk c){
		byte meta = (byte) w.getMetaAtPos(x,y,z);
		/*
		 * bottom - first top - second front - third back - fourth left -
		 * fifth right - sixth
		 */
		float[] texCoordsA = meta == 0 ? TextureManager.texture("tiles.door_bottom") : TextureManager.texture("tiles.door_top");
		float[] texCoords = new float[]{
			Tile.Wood.getTexCoords((byte) 0)[0], Tile.Wood.getTexCoords((byte) 0)[1],
			Tile.Wood.getTexCoords((byte) 0)[0], Tile.Wood.getTexCoords((byte) 0)[1],
			Tile.Wood.getTexCoords((byte) 0)[0], Tile.Wood.getTexCoords((byte) 0)[1],
			Tile.Wood.getTexCoords((byte) 0)[0], Tile.Wood.getTexCoords((byte) 0)[1],
			texCoordsA[0], texCoordsA[1],
			texCoordsA[0], texCoordsA[1]
		};
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createRect(x, y, z, Color4f.WHITE, texCoords, 0.0625f, 1f, 1f);
		GL11.glEnd();
	}
}
