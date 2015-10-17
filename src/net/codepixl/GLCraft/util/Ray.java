package net.codepixl.GLCraft.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Ray {
	
	public Vector3f pos, dir, scaledDir, origPos;
	public float distance;
	public float scalar;
	
	public Ray(Vector3f pos, Vector3f dir, float scalar) {
		this.pos = pos;
		this.origPos = new Vector3f(pos);
		this.dir = dir;
		this.scalar = scalar;
		this.scaledDir = scale(dir, scalar);
	}
	
	public void next() {
		pos = Vector3f.add(pos, scaledDir, null);
		distance += scalar;
	}
	
	public void prev(){
		pos = Vector3f.sub(pos, scaledDir, null);
		distance -= scalar;
	}
	
	private Vector3f scale(Vector3f vec, float scalar) {
		Vector3f tmp = new Vector3f();
		tmp.x = vec.x * scalar;
		tmp.y = vec.y * scalar;
		tmp.z = vec.z * scalar;
		return tmp;
	}
	
	@Override
	public String toString() {
		return String.format("Ray: Pos = (%s) Dir = (%s)", pos, dir);
	}
	
	public void drawRay(){
		GL11.glLineWidth(0.2f);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(origPos.x, origPos.y, origPos.z);
			GL11.glVertex3f(pos.x, pos.y, pos.z);
		GL11.glEnd();
	}
	public void drawRayBlock(){
		GL11.glLineWidth(0.2f);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(origPos.x, origPos.y, origPos.z);
			GL11.glVertex3f(((int)pos.x)+0.5f, ((int)pos.y)+0.5f, ((int)pos.z)+0.5f);
		GL11.glEnd();
	}
	
}