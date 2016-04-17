package net.codepixl.GLCraft.world.tile;

import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
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
		GL11.glBegin(GL11.GL_QUADS);
		Shape.createCube(x, y, z, Color4f.WHITE, getTexCoords(), size);
		GL11.glEnd();
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
		if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId()){
			w.setMetaAtPos(x, y, z, (byte)1, false, false);
		}else if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId()){
			w.setMetaAtPos(x, y, z, (byte)1, false, false);
		}else if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId()){
			w.setMetaAtPos(x, y, z, (byte)1, false, false);
		}else if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId()){
			w.setMetaAtPos(x, y, z, (byte)1, false, false);
		}else if(w.getTileAtPos(x, y-1, z) == Tile.Air.getId()){
			w.setMetaAtPos(x, y, z, (byte)1, false, false);
		}else{
			if(w.getMetaAtPos(x, y, z) == 1){
				w.setMetaAtPos(x, y, z, (byte)0, false, false);
			}
		}
	}
	
	@Override
	public void tick(int x, int y, int z, WorldManager w){
		//If water is not stationary
		if(w.getMetaAtPos(x,y,z) != 0){
			if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId()){
				w.setTileAtPos(x+1, y, z, getId(), true);
			}
			if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId()){
				w.setTileAtPos(x-1, y, z, getId(), true);
			}
			if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId()){
				w.setTileAtPos(x, y, z+1, getId(), true);
			}
			if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId()){
				w.setTileAtPos(x, y, z-1, getId(), true);
			}
			if(w.getTileAtPos(x, y-1, z) == Tile.Air.getId()){
				w.setTileAtPos(x, y-1, z, getId(), true);
			}
		}
	}
}
