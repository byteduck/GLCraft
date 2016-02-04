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
import net.codepixl.GLCraft.render.Spritesheet;
import net.codepixl.GLCraft.world.WorldManager;

public class PlayerMP extends Mob{
	
	public PlayerMP(Vector3f pos, WorldManager w) {
		super(pos, w);
	}
	
	@Override
	public void update(){}
	
	public void render(){
		/*glPushMatrix();
		glTranslatef(getPos().x, getPos().y-1, getPos().z);
		glRotatef(-this.getRot().y+180,0,1f,0);
		glRotatef(this.getRot().z,1f,0,0);
		glTranslatef(-getPos().x, -getPos().y+1, -getPos().z);
		glBegin(GL_QUADS);
			Shape.createCube(getPos().x-0.5f, getPos().y-2f, getPos().z-0.5f, new Color4f(1.0f,1.0f,1.0f,1.0f), this.bottomCoords, 1.0f);
			Shape.createCube(getPos().x-0.5f, getPos().y-1f, getPos().z-0.5f, new Color4f(1.0f,1.0f,1.0f,1.0f), this.headCoords, 1.0f);
		glEnd();
		glPopMatrix();*/
	}
}
