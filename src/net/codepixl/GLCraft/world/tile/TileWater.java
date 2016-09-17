package net.codepixl.GLCraft.world.tile;

import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileWater extends Tile{

	@Override
	public String getName() {
		return "Water";
	}
	
	@Override
	public Material getMaterial(){
		return Material.LIQUID;
	}
	
	@Override
	public byte getId() {
		return 3;
	}

	@Override
	public Color4f getColor() {
		return Color4f.WHITE;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CUSTOM;
	}
	
	@Override
	public RenderType getCustomRenderType(){
		return RenderType.CUBE;
	}
	
	@Override
	public void customRender(float x, float y, float z, WorldManager w, Chunk c){
		float size = getHeight(w.getMetaAtPos(x,y,z));
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		/*
		 * bottom - 0,1 (0)
		 * top - 2,3 (1)
		 * front - 4,5 (2)
		 * back - 6,7 (3)
		 * left - 8,9 (4)
		 * right - 10,11 (5)
		 */
		float[] cornerHeight = new float[4];
		for(int i = 0; i < 4; i++)
			cornerHeight[i] = getCornerHeight(i,x,y,z,w);

		if(sideShouldRender(0,x,y,z,w)){
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]);
			GL11.glVertex3f(0, 0, 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]);
			GL11.glVertex3f(0, 0, 1);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, 0, 1);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, 0, 0);
			GL11.glEnd();
		}
		if(sideShouldRender(1,x,y,z,w)){
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]);
			GL11.glVertex3f(0, cornerHeight[0], 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]);
			GL11.glVertex3f(1, cornerHeight[1], 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, cornerHeight[2], 1);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(0, cornerHeight[3], 1);
			GL11.glEnd();
		}
		if(sideShouldRender(2,x,y,z,w)){
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]);
			GL11.glVertex3f(0, 0, 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]);
			GL11.glVertex3f(1, 0, 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, cornerHeight[1], 0);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(0, cornerHeight[0], 0);
			GL11.glEnd();
		}
		if(sideShouldRender(3,x,y,z,w)){
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]);
			GL11.glVertex3f(0, 0, 1);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]);
			GL11.glVertex3f(0, cornerHeight[3], 1);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, cornerHeight[2], 1);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, 0, 1);
			GL11.glEnd();
		}
		if(sideShouldRender(4,x,y,z,w)){
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]);
			GL11.glVertex3f(1, 0, 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]);
			GL11.glVertex3f(1, 0, 1);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, cornerHeight[2], 1);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(1, cornerHeight[1], 0);
			GL11.glEnd();
		}
		if(sideShouldRender(5,x,y,z,w)){
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]);
			GL11.glVertex3f(0, 0, 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]);
			GL11.glVertex3f(0, cornerHeight[0], 0);
			GL11.glTexCoord2f(this.getTexCoords()[0]+Spritesheet.atlas.uniformSize(), this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(0, cornerHeight[3], 1);
			GL11.glTexCoord2f(this.getTexCoords()[0], this.getTexCoords()[1]+Spritesheet.atlas.uniformSize());
			GL11.glVertex3f(0, 0, 1);
			GL11.glEnd();
		}
		
		GL11.glPopMatrix();
	}
	
	//0: 0,0
	//1: 1,0
	//2: 1,1
	//3: 0,1
	private float getCornerHeight(int corner, float x, float y, float z, WorldManager w){
		int num = 1;
		float total = 0;
		float ret = 1;
		switch(corner){
			case 0:
				if(w.getTileAtPos(x-1,y+1,z) == getId() || w.getTileAtPos(x,y+1,z-1) == getId() || w.getTileAtPos(x-1,y+1,z-1) == getId() || w.getTileAtPos(x,y+1,z) == getId()){
					ret = 1;
				}else{
					if(w.getTileAtPos(x-1,y,z) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x-1, y, z));
					}
					if(w.getTileAtPos(x,y,z-1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x, y, z-1));
					}
					if(w.getTileAtPos(x-1,y,z-1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x-1, y, z-1));
					}
					total+=getHeight(w.getMetaAtPos(x, y, z));
					ret = total/(float)num;
				}
				break;
			case 1:
				if(w.getTileAtPos(x+1,y+1,z) == getId() || w.getTileAtPos(x,y+1,z-1) == getId() || w.getTileAtPos(x+1,y+1,z-1) == getId() || w.getTileAtPos(x,y+1,z) == getId()){
					ret = 1;
				}else{
					if(w.getTileAtPos(x+1,y,z) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x+1, y, z));
					}
					if(w.getTileAtPos(x,y,z-1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x, y, z-1));
					}
					if(w.getTileAtPos(x+1,y,z-1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x+1, y, z-1));
					}
					total+=getHeight(w.getMetaAtPos(x, y, z));
					ret = total/(float)num;
				}
				break;
			case 2:
				if(w.getTileAtPos(x+1,y+1,z) == getId() || w.getTileAtPos(x,y+1,z+1) == getId() || w.getTileAtPos(x+1,y+1,z+1) == getId() || w.getTileAtPos(x,y+1,z) == getId()){
					ret = 1;
				}else{
					if(w.getTileAtPos(x+1,y,z) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x+1, y, z));
					}
					if(w.getTileAtPos(x,y,z+1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x, y, z+1));
					}
					if(w.getTileAtPos(x+1,y,z+1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x+1, y, z+1));
					}
					total+=getHeight(w.getMetaAtPos(x, y, z));
					ret = total/(float)num;
				}
				break;
			case 3:
				if(w.getTileAtPos(x-1,y+1,z) == getId() || w.getTileAtPos(x,y+1,z+1) == getId() || w.getTileAtPos(x-1,y+1,z+1) == getId() || w.getTileAtPos(x,y+1,z) == getId()){
					ret = 1;
				}else{
					if(w.getTileAtPos(x-1,y,z) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x-1, y, z));
					}
					if(w.getTileAtPos(x,y,z+1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x, y, z+1));
					}
					if(w.getTileAtPos(x-1,y,z+1) == getId()){
						num++;
						total+=getHeight(w.getMetaAtPos(x-1, y, z+1));
					}
					total+=getHeight(w.getMetaAtPos(x, y, z));
					ret = total/(float)num;
				}
				break;
		}
		
		return ret;
	}
	
	private boolean sideShouldRender(int f, float x, float y, float z, WorldManager w){
		byte meta = (byte) w.getMetaAtPos(x,y,z);
		if(meta == 8 || meta == 0)
			meta = 1;
		switch(f){
			case 0: //BOTTOM
				return (w.getTileAtPos(x,y-1,z) != getId());
			case 1: //TOP
				return (w.getTileAtPos(x,y+1,z) != getId());
			case 2: //FRONT
				return (w.getTileAtPos(x,y,z-1) != getId());
			case 3: //BACK
				return (w.getTileAtPos(x,y,z+1) != getId());
			case 4: //LEFT
				return (w.getTileAtPos(x+1,y,z) != getId());
			case 5:
				return (w.getTileAtPos(x-1,y,z) != getId());
			default:
				return false;
		}
	}
	
	private float getHeight(int i){
		float size;
		if(i == 0){
			size = 1f;
		}else if(i == 8){
			size = 1f;
		}else{
			size = (7f-((float)i-1f))/7f;
		}
		return size;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public boolean isTranslucent(){
		return true;
	}

	@Override
	public boolean canPassThrough() {
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
	public AABB getAABB(byte meta){
		float size = 0;
		if(meta == 0){
			size = 1f;
		}else if(meta == 8){
			size = 1f;
		}else{
			size = (7f-((float)meta-1f))/7f;
		}
		return new AABB(1,size,1);
	}
	
	@Override
	public void blockUpdate(int x, int y, int z, WorldManager w){
		//If water is not stationary
		byte meta = w.getMetaAtPos(x, y, z);
		if(meta != 0 && meta != 8){
			byte lmeta = 8;
			boolean noWater = true;
			byte m = 0;
			if(w.getTileAtPos(x+1, y, z) == Tile.Water.getId()){
				m = w.getMetaAtPos(x+1, y, z);
				m = (m == 8) ? 1 : m;
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x-1, y, z) == Tile.Water.getId()){
				m = w.getMetaAtPos(x-1, y, z);
				m = (m == 8) ? 1 : m;
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x, y, z-1) == Tile.Water.getId()){
				m = w.getMetaAtPos(x, y, z-1);
				m = (m == 8) ? 1 : m;
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(w.getTileAtPos(x, y, z+1) == Tile.Water.getId()){
				m = w.getMetaAtPos(x, y, z+1);
				m = (m == 8) ? 1 : m;
				if(m < lmeta) lmeta = m;
				noWater = false;
			}
			if(noWater == false){
				if(lmeta+1 != meta && lmeta < 8 && meta > 1) w.setMetaAtPos(x, y, z, (byte) (lmeta+1), true, true, true);
				if(lmeta+1 > 7){w.setTileAtPos(x,y,z,Tile.Air.getId(),true); w.setMetaAtPos(x, y, z, (byte)0, false);}
			}else{
				if(meta > 1){
					w.setTileAtPos(x, y, z, Tile.Air.getId(), true);
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
			}
		}else if(meta == 0){
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
		w.rebuildAtPos(x, y, z);
	}
	
	@Override
	public void onPlace(int x, int y, int z, WorldManager w){
		w.blockUpdate(x, y, z);
	}
	
	@Override
	public void tick(int x, int y, int z, WorldManager w){
		byte m = w.getMetaAtPos(x,y,z);
		if(m != 0 && m <= 7){
			if(m < 7 && ((!Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canPassThrough() && !Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()) || m <= 1)){
				if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x+1, y, z)).canBeDestroyedByLiquid()){
					w.setTileAtPos(x+1, y, z, getId(), true, (byte) (m+1));
				}
				if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x-1, y, z)).canBeDestroyedByLiquid()){
					w.setTileAtPos(x-1, y, z, getId(), true, (byte) (m+1));
				}
				if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z+1)).canBeDestroyedByLiquid()){
					w.setTileAtPos(x, y, z+1, getId(), true, (byte) (m+1));
				}
				if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z-1)).canBeDestroyedByLiquid()){
					w.setTileAtPos(x, y, z-1, getId(), true, (byte) (m+1));
				}
			}else if(Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()){
				w.setTileAtPos(x, y-1, z, getId(), true, (byte)8);
			}
		}else if(m > 7){
			if(w.getTileAtPos(x, y+1, z) != getId()){
				w.setTileAtPos(x, y, z, Tile.Air.getId(), true);
			}else{
				if(Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()){
					w.setTileAtPos(x, y-1, z, getId(), true, (byte)8);
				}
				if(!Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canPassThrough() && !Tile.getTile((byte) w.getTileAtPos(x, y-1, z)).canBeDestroyedByLiquid()){
					if(w.getTileAtPos(x+1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x+1, y, z)).canBeDestroyedByLiquid()){
						w.setTileAtPos(x+1, y, z, getId(), true, (byte) 2);
					}
					if(w.getTileAtPos(x-1, y, z) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x-1, y, z)).canBeDestroyedByLiquid()){
						w.setTileAtPos(x-1, y, z, getId(), true, (byte) 2);
					}
					if(w.getTileAtPos(x, y, z+1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z+1)).canBeDestroyedByLiquid()){
						w.setTileAtPos(x, y, z+1, getId(), true, (byte) 2);
					}
					if(w.getTileAtPos(x, y, z-1) == Tile.Air.getId() || Tile.getTile((byte) w.getTileAtPos(x, y, z-1)).canBeDestroyedByLiquid()){
						w.setTileAtPos(x, y, z-1, getId(), true, (byte) 2);
					}
				}
			}
		}
	}
	
	@Override
	public boolean canBePlacedOver() {
		return true;
	}
}
