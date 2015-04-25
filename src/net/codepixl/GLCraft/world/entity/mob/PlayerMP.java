package net.codepixl.GLCraft.world.entity.mob;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.entity.Camera;

import com.nishu.utils.Color4f;

public class PlayerMP extends Mob{
	
	private float[] headCoords = {
		0, Spritesheet.tiles.uniformSize()*2,
		0, Spritesheet.tiles.uniformSize()*2,
		0, Spritesheet.tiles.uniformSize()*2,
		Spritesheet.tiles.uniformSize()*15, 0,
		0, Spritesheet.tiles.uniformSize()*2,
		0, Spritesheet.tiles.uniformSize()*2
	};
	
	private float[] bottomCoords = {
			0, Spritesheet.tiles.uniformSize()
		};
	
	public PlayerMP(Camera camera, int id, int mobID) {
		super(camera, id, mobID);
	}
	
	public void update(){}
	
	public void render(){
		glPushMatrix();
		glTranslatef(getPos().x, getPos().y-1, getPos().z);
		glRotatef(-getYaw()+180,0,1f,0);
		glRotatef(getPitch(),1f,0,0);
		glTranslatef(-getPos().x, -getPos().y+1, -getPos().z);
		glBegin(GL_QUADS);
			Shape.createCube(getPos().x-0.5f, getPos().y-2f, getPos().z-0.5f, new Color4f(1.0f,1.0f,1.0f,1.0f), this.bottomCoords, 1.0f);
			Shape.createCube(getPos().x-0.5f, getPos().y-1f, getPos().z-0.5f, new Color4f(1.0f,1.0f,1.0f,1.0f), this.headCoords, 1.0f);
		glEnd();
		glPopMatrix();
	}
}
