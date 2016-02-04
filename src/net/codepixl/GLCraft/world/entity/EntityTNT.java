package net.codepixl.GLCraft.world.entity;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

public class EntityTNT extends EntitySolid{

	public EntityTNT(int x, int y, int z, WorldManager worldManager) {
		super(x+0.5f, y, z+0.5f, worldManager);
	}
	
	@Override
	public void render(){
		float col = 0f;
		if(timeAlive % 1000 <= 500){
			col = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0f, 0.3f, 0.5f);
		}else{
			col = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0.3f, -0.3f, 0.5f);
		}
		float size = 1f;
		float offset = 0f;
		if(this.timeAlive > 3500f){
			size += ((this.timeAlive - 3500f)/500f)/3f;
			offset = (size - 1f)/2f;
		}
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_ADD);
		TextureManager.bindTile(Tile.Tnt);
		glBegin(GL_QUADS);
		Shape.createCube(this.getX()-0.5f-offset, this.getY(), this.getZ()-0.5f-offset, new Color4f(col,col,col,1f), size);
		glEnd();
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.9f,0.9f,0.9f);
	}
	
	@Override
	public void update(){
		super.update();
		if (this.timeAlive > 4000f) {
			ArrayList<Chunk> affectedChunks = new ArrayList<Chunk>();
			for (int i = 0; i < 16; ++i) {
				for (int j = 0; j < 16; ++j) {
					for (int k = 0; k < 16; ++k) {
						if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
							float d0 = (float) ((float) i / 15.0F * 2.0F - 1.0F);
							float d1 = (float) ((float) j / 15.0F * 2.0F - 1.0F);
							float d2 = (float) ((float) k / 15.0F * 2.0F - 1.0F);
							float d3 = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
							d0 /= d3;
							d1 /= d3;
							d2 /= d3;
							float explosionStrength = 1.5f;
							float f = explosionStrength * (0.7F + Constants.rand.nextFloat() * 0.6F);
							float d4 = this.getX();
							float d6 = this.getY();
							float d8 = this.getZ();

							for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
								Vector3f blockpos = new Vector3f(d4, d6, d8);
								worldManager.setTileAtPos(blockpos, Tile.Air.getId(), false);
								if (!affectedChunks.contains(worldManager.getChunkAtCoords(blockpos))) {
									affectedChunks.add(worldManager.getChunkAtCoords(blockpos));
								}
								d4 += d0 * 0.30000001192092896D;
								d6 += d1 * 0.30000001192092896D;
								d8 += d2 * 0.30000001192092896D;
							}
						}
					}
				}
				this.setDead(true);
			}
			Iterator<Chunk> i = affectedChunks.iterator();
			while(i.hasNext()){
				i.next().rebuild();
			}
		}
	}
}
