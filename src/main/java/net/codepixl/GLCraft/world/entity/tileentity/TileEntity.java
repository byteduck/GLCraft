package net.codepixl.GLCraft.world.entity.tileentity;

import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import org.lwjgl.util.vector.Vector3f;

public class TileEntity extends Entity{
	
	private Vector3i blockpos;
	
	public TileEntity(int x, int y, int z, WorldManager worldManager) {
		super(x, y, z, worldManager);
		this.blockpos = new Vector3i(x,y,z);
	}
	
	@Override
	public void setPos(Vector3f pos){
		super.setPos(pos);
		this.blockpos = new Vector3i(pos);
	}
	
	@Override
	public void setPos(float x, float y, float z){
		super.setPos(x, y, z);
		this.blockpos = new Vector3i(pos);
	}
	
	@Override
	public void setX(float x){
		super.setX(x);
		this.blockpos.x = (int) x;
	}
	
	@Override
	public void setY(float y){
		super.setX(y);
		this.blockpos.y = (int) y;
	}
	
	@Override
	public void setZ(float z){
		super.setZ(z);
		this.blockpos.z = (int) z;
	}
	
	public Vector3i getBlockpos(){
		return blockpos;
	}

}
