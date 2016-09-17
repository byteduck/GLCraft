package net.codepixl.GLCraft.world.tile;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;

public class TileBluestone extends Tile{
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Bluestone";
	}
	
	@Override
	public String getTextureName(){
		return "bluestone_center";
	}
	
	public TileBluestone(){
		super();
		TextureManager.addTexture("tiles.bluestone_side",TextureManager.TILES+"bluestone_side.png");
		TextureManager.addTexture("tiles.bluestone_line",TextureManager.TILES+"bluestone_line.png");
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 13;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	
	@Override
	public boolean canBeDestroyedByLiquid(){
		return true;
	}
	
	@Override
	public boolean customHitbox(){
		return true;
	}
	
	@Override
	public AABB getAABB(byte meta){
		return new AABB(1,0.1f,1);
	}
	
	@Override
	public float getHardness(){
		return 0f;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CUSTOM;
	}
	
	@Override
	public RenderType getCustomRenderType(){
		return RenderType.FLAT;
	}
	
	@Override
	public void customRender(float x, float y, float z, WorldManager w, Chunk c){
		float col = w.getMetaAtPos((int)x, (int)y, (int)z)/15f;
		glBegin(GL_QUADS);
		float[] texCoords = TextureManager.tile(this);
		Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), texCoords, 1);
		glEnd();
		if(w.getTileAtPos(x, y, z-1) == Tile.Bluestone.getId() || w.getTileAtPos(x, y-1, z-1) == Tile.Bluestone.getId() || w.getTileAtPos(x, y+1, z-1) == Tile.Bluestone.getId()){
			if(w.getTileAtPos(x, y-1, z-1) == Tile.Bluestone.getId()){
				glPushMatrix();
				glTranslatef(x,y,z-0.02f);
				glRotatef(90f,1f,0,0);
				glTranslatef(-x,-y,-z);
				glBegin(GL_QUADS);
				Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_line"), 1);
				glEnd();
				glPopMatrix();
			}
			glPushMatrix();
			glBegin(GL_QUADS);
			Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_side"), 1);
			glEnd();
			glPopMatrix();
		}
		if(w.getTileAtPos(x, y, z+1) == Tile.Bluestone.getId() || w.getTileAtPos(x, y-1, z+1) == Tile.Bluestone.getId() || w.getTileAtPos(x, y+1, z+1) == Tile.Bluestone.getId()){
			if(w.getTileAtPos(x, y-1, z+1) == Tile.Bluestone.getId()){
				glPushMatrix();
				glTranslatef(x,y-1f,z+1.02f);
				glRotatef(-90f,1f,0,0);
				glTranslatef(-x,-y,-z);
				glBegin(GL_QUADS);
				Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_line"), 1);
				glEnd();
				glPopMatrix();
			}
			glPushMatrix();
			glTranslatef(x+1f,y,z+1f);
			glRotatef(180f,0,1f,0);
			glTranslatef(-x,-y,-z);
			glBegin(GL_QUADS);
			Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_side"), 1);
			glEnd();
			glPopMatrix();
		}
		if(w.getTileAtPos(x+1, y, z) == Tile.Bluestone.getId() || w.getTileAtPos(x+1, y-1, z) == Tile.Bluestone.getId() || w.getTileAtPos(x+1, y+1, z) == Tile.Bluestone.getId()){
			if(w.getTileAtPos(x+1, y-1, z) == Tile.Bluestone.getId()){
				glPushMatrix();
				glTranslatef(x+1f,y-1f,z);
				glRotatef(-90f, 0f, 1f, 0f);
				glRotatef(-90f,1f,0f,0);
				glTranslatef(-x,-y,-z);
				glBegin(GL_QUADS);
				Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_line"), 1);
				glEnd();
				glPopMatrix();
			}
			glPushMatrix();
			glTranslatef(x+1f,y,z);
			glRotatef(-90f,0,1f,0);
			glTranslatef(-x,-y,-z);
			glBegin(GL_QUADS);
			Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_side"), 1);
			glEnd();
			glPopMatrix();
		}
		if(w.getTileAtPos(x-1, y, z) == Tile.Bluestone.getId() || w.getTileAtPos(x-1, y-1, z) == Tile.Bluestone.getId() || w.getTileAtPos(x-1, y+1, z) == Tile.Bluestone.getId()){
			if(w.getTileAtPos(x-1, y-1, z) == Tile.Bluestone.getId()){
				glPushMatrix();
				glTranslatef(x,y-1f,z+1f);
				glRotatef(90f, 0f, 1f, 0f);
				glRotatef(-90f,1f,0f,0);
				glTranslatef(-x,-y,-z);
				glBegin(GL_QUADS);
				Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_line"), 1);
				glEnd();
				glPopMatrix();
			}
			glPushMatrix();
			glTranslatef(x,y,z+1f);
			glRotatef(90f,0,1f,0);
			glTranslatef(-x,-y,-z);
			glBegin(GL_QUADS);
			Shape.createFlat(x, y+0.01f, z, new Color4f(col,col,col,1f), TextureManager.texture("tiles.bluestone_side"), 1);
			glEnd();
			glPopMatrix();
		}
	}
	
	@Override
	public void renderHitbox(Vector3f pos){
		glBegin(GL11.GL_QUADS);
		Shape.createTexturelessFlat((int) pos.x, (int) pos.y + 0.02f, (int) pos.z - 0.0005f, new Color4f(0, 0, 0, 1f), 1.001f);
		glEnd();
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void tick(int x, int y, int z, WorldManager w){
		byte prev = w.getMetaAtPos(x, y, z);
		byte most = 0;
		//POSITIVE X
		if(w.getTileAtPos(x+1, y, z) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x+1, y, z);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x+1, y-1, z) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x+1, y-1, z);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x+1, y+1, z) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x+1, y+1, z);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		//NEGATIVE X
		if(w.getTileAtPos(x-1, y, z) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x-1, y, z);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x-1, y-1, z) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x-1, y-1, z);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x-1, y+1, z) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x-1, y+1, z);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		//POSITIVE Z
		if(w.getTileAtPos(x, y, z+1) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x, y, z+1);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x, y-1, z+1) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x, y-1, z+1);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x, y+1, z+1) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x, y+1, z+1);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		//NEGATIVE Z
		if(w.getTileAtPos(x, y, z-1) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x, y, z-1);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x, y-1, z-1) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x, y-1, z-1);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		if(w.getTileAtPos(x, y+1, z-1) == Tile.Bluestone.getId()){
			byte b = w.getMetaAtPos(x, y+1, z-1);
			if(b-1 > most){
				most = (byte) (b-1);
			}
		}
		//END BLUESTONE DETECTION
		if(w.getTileAtPos(x+1, y, z) == Tile.Grass.getId()){
			most = 15;
		}
		w.setMetaAtPos(x, y, z, most, false);
		if(most != prev){
			w.rebuildAtPos(x, y, z);
		}
	}
	
	@Override
	public boolean needsConstantTick(){
		return true;
	}
	
	@Override
	public void onPlace(int x, int y, int z, WorldManager w){
		Vector3f one = new Vector3f(x+1,y,z);
		Vector3f two = new Vector3f(x-1,y,z);
		Vector3f three = new Vector3f(x,y,z+1);
		Vector3f four = new Vector3f(x,y,z-1);
		//System.out.println(w.getChunkAtCoords(one)+""+w.getChunkAtCoords(two)+""+w.getChunkAtCoords(three)+""+w.getChunkAtCoords(four));
		w.getChunk(one).queueRebuild();
		w.getChunk(two).queueRebuild();
		w.getChunk(three).queueRebuild();
		w.getChunk(four).queueRebuild();
	}
	
	@Override
	public void onBreak(int x, int y, int z, boolean drop, WorldManager w){
		super.onBreak(x,y,z,drop,w);
		onPlace(x,y,z,w);
	}
	
	@Override
	public boolean canPlace(int x, int y, int z, WorldManager w){
		if(!Tile.getTile((byte)w.getTileAtPos(x, y-1, z)).canPassThrough()){
			return true;
		}
		return false;
	}

}
