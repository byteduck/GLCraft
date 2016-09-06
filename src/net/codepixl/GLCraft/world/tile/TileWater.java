package net.codepixl.GLCraft.world.tile;

import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.EnumFacing;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileWater extends Tile{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Water";
	}
	
	@Override
	public Material getMaterial(){
		return Material.LIQUID;
	}
	
	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CUSTOM;
	}
	
	@Override
	public void customRender(float x, float y, float z, WorldManager w, Chunk c){
		float size;
		byte meta = w.getMetaAtPos((int)x, (int)y, (int)z);
		if(meta == 0){
			size = 1f;
		}else{
			size = (15f-((float)meta-1f))/15f;
		}
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		GL11.glScalef(1, size, 1);
		GL11.glBegin(GL11.GL_QUADS);
		/*
		 * bottom - 0,1 (0)
		 * top - 2,3 (1)
		 * front - 4,5 (2)
		 * back - 6,7 (3)
		 * left - 8,9 (4)
		 * right - 10,11 (5)
		 */
		float tempTexCoords[] = new float[12];
		for(int i = 0; i < 6; i++){
			if(sideShouldRender(i,x,y,z,w)){
				tempTexCoords[i*2] = getTexCoords()[0];
				tempTexCoords[i*2+1] = getTexCoords()[1];
			}else{
				tempTexCoords[i*2] = TextureManager.texture("misc.nothing")[0];
				tempTexCoords[i*2+1] = TextureManager.texture("misc.nothing")[1];
			}
		}
		Shape.createCube(0, 0, 0, Color4f.WHITE, tempTexCoords, 1f);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	private boolean sideShouldRender(int f, float x, float y, float z, WorldManager w){
		switch(f){
			case 0: //BOTTOM
				return (w.getTileAtPos(x,y-1,z) != Tile.Water.getId()) || (w.getTileAtPos(x,y-1,z) == Tile.Water.getId() && w.getMetaAtPos(x, y-1, z) > w.getMetaAtPos(x, y, z));
			case 1: //TOP
				return (w.getTileAtPos(x,y+1,z) != Tile.Water.getId()) || (w.getTileAtPos(x,y+1,z) == Tile.Water.getId() && w.getMetaAtPos(x, y+1, z) > w.getMetaAtPos(x, y, z));
			case 2: //FRONT
				return (w.getTileAtPos(x,y,z-1) != Tile.Water.getId()) || (w.getTileAtPos(x,y,z-1) == Tile.Water.getId() && w.getMetaAtPos(x, y, z-1) > w.getMetaAtPos(x, y, z));
			case 3: //BACK
				return (w.getTileAtPos(x,y,z+1) != Tile.Water.getId()) || (w.getTileAtPos(x,y,z+1) == Tile.Water.getId() && w.getMetaAtPos(x, y, z+1) > w.getMetaAtPos(x, y, z));
			case 4: //LEFT
				return (w.getTileAtPos(x+1,y,z) != Tile.Water.getId()) || (w.getTileAtPos(x+1,y,z) == Tile.Water.getId() && w.getMetaAtPos(x+1, y, z) > w.getMetaAtPos(x, y, z));
			case 5:
				return (w.getTileAtPos(x-1,y,z) != Tile.Water.getId()) || (w.getTileAtPos(x-1,y,z) == Tile.Water.getId() && w.getMetaAtPos(x-1, y, z) > w.getMetaAtPos(x, y, z));
			default:
				return false;
		}
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isTranslucent(){
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public float getHardness(){
		return 0.1f;
	}
	
	@Override
	public int tickRate(){
		return 5;
	}
	
	@Override
	public boolean needsConstantTick(){
		return true;
	}
	
	@Override
	public void blockUpdate(int x, int y, int z, WorldManager w){
		//If water is not stationary
		byte meta = w.getMetaAtPos(x, y, z);
		if(meta != 0){
			byte lmeta = 15;
			boolean noWater = true;
			byte m = 0;
			if(w.getTileAtPos(x+1, y, z) == Tile.Water.getId()){
				m = w.getMetaAtPos(x+1, y, z);
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x-1, y, z) == Tile.Water.getId()){
				m = w.getMetaAtPos(x-1, y, z);
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x, y, z-1) == Tile.Water.getId()){
				m = w.getMetaAtPos(x, y, z-1);
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x, y, z+1) == Tile.Water.getId()){
				m = w.getMetaAtPos(x, y, z+1);
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x,y+1,z) == Tile.Water.getId()){
				lmeta = 0;
				noWater = false;
			}
			if(noWater == false){
				if(lmeta+1 != meta && lmeta < 16 && meta > 1) w.setMetaAtPos(x, y, z, (byte) (lmeta+1), true, true, true);
				if(lmeta+1 > 15){ w.setTileAtPos(x,y,z,Tile.Air.getId(),true); w.setMetaAtPos(x, y, z, (byte)0, false);}
			}else{
				if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x+1, y, z)).canBeDestroyedByLiquid()){
					w.setMetaAtPos(x, y, z, (byte)1, false, true, false);
				}else if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x-1, y, z)).canBeDestroyedByLiquid()){
					w.setMetaAtPos(x, y, z, (byte)1, false, true, false);
				}else if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z+1)).canBeDestroyedByLiquid()){
					w.setMetaAtPos(x, y, z, (byte)1, false, true, false);
				}else if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z-1)).canBeDestroyedByLiquid()){
					w.setMetaAtPos(x, y, z, (byte)1, false, true, false);
				}else if(w.getTileAtPos(x, y-1, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()){
					w.setMetaAtPos(x, y, z, (byte)1, false, true, false);
				}else{
					w.setMetaAtPos(x, y, z, (byte)0, false, true, false);
				}
			}
		}else{
			if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x+1, y, z)).canBeDestroyedByLiquid()){
				w.setMetaAtPos(x, y, z, (byte)1, false);
			}else if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x-1, y, z)).canBeDestroyedByLiquid()){
				w.setMetaAtPos(x, y, z, (byte)1, false);
			}else if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z+1)).canBeDestroyedByLiquid()){
				w.setMetaAtPos(x, y, z, (byte)1, false);
			}else if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z-1)).canBeDestroyedByLiquid()){
				w.setMetaAtPos(x, y, z, (byte)1, false);
			}else if(w.getTileAtPos(x, y-1, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()){
				w.setMetaAtPos(x, y, z, (byte)1, false);
			}
		}
	}
	
	@Override
	public void onPlace(int x, int y, int z, WorldManager w){
		this.blockUpdate(x, y, z, w);
	}
	
	@Override
	public void tick(int x, int y, int z, WorldManager w){
		if(w.getMetaAtPos(x,y,z) != 0 && w.getMetaAtPos(x, y, z) < 15){
			if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x+1, y, z)).canBeDestroyedByLiquid()){
				w.setTileAtPos(x+1, y, z, getId(), true, (byte)2);
			}
			if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x-1, y, z)).canBeDestroyedByLiquid()){
				w.setTileAtPos(x-1, y, z, getId(), true, (byte)2);
			}
			if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z+1)).canBeDestroyedByLiquid()){
				w.setTileAtPos(x, y, z+1, getId(), true, (byte)2);
			}
			if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z+1)).canBeDestroyedByLiquid()){
				w.setTileAtPos(x, y, z-1, getId(), true, (byte)2);
			}
			if(w.getTileAtPos(x, y-1, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()){
				w.setTileAtPos(x, y-1, z, getId(), true, (byte)2);
			}
		}
	}
	
	@Override
	public boolean canBePlacedOver() {
		return true;
	}
}
