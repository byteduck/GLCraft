package net.codepixl.GLCraft.world.entity.mob;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;

public class EntityPlayerMP extends EntityPlayer{

	public EntityPlayerMP(Vector3f pos, WorldManager w) {
		super(pos, w);
	}
	
	@Override
	public void render(){}
	
	@Override
	public void update(){}
	
	@Override
	public void updateMouse(){}
	
	@Override
	public void updateKeyboard(float a, float b){}

}
