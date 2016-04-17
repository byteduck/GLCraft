package net.codepixl.GLCraft.world.entity.mob;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.WorldManager;

public class EntityPlayerMP extends EntityPlayer{
	public EntityPlayerMP(Vector3f pos, WorldManager w) {
		super(pos, w);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(){
		
	}
}
