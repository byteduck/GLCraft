package net.codepixl.GLCraft.world.entity.mob;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.Camera;
import static org.lwjgl.opengl.GL11.*;

public class PlayerMP extends Mob{

	public PlayerMP(Camera camera, int id, int mobID) {
		super(camera, id, mobID);
	}
	
	public void update(){}
	
	public void render(){
		glPushMatrix();
		glTranslatef(getPos().x+0.5f, getPos().y+0.5f, getPos().z+0.5f);
		//glRotatef(getPitch(),1f,0,0);
		glRotatef(getYaw(),0,1f,0);
		//glRotatef(getRoll(),0,0,1f);
		glTranslatef(-getPos().x-0.5f, -getPos().y-0.5f, -getPos().z-0.5f);
		glBegin(GL_QUADS);
			Shape.createTexturelessCube(getPos().x, getPos().y, getPos().z, new Color4f(1.0f,0,0,1.0f), 1.0f);
		glEnd();
		glPopMatrix();
	}
}
