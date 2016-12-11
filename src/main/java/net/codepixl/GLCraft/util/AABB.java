package net.codepixl.GLCraft.util;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.render.Shape;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class AABB {
	public double center[];
	public double pos[];
	public double vel[];
	public double r[];
	private Vector3f sizeVec = new Vector3f();

	public AABB(final float width, final float height, final float length) {
		center = new double[3];
		pos = new double[3];
		r = new double[3];
		r[0] = width * 0.5d;
		r[1] = height * 0.5d;
		r[2] = length * 0.5d;
		vel = new double[3];
	}

	public AABB(AABB aabb) {
		center = Arrays.copyOf(aabb.center, aabb.center.length);
		pos = Arrays.copyOf(aabb.pos, aabb.pos.length);
		r = Arrays.copyOf(aabb.r, aabb.r.length);
		vel = Arrays.copyOf(aabb.vel, aabb.vel.length);
	}

	public AABB update(final Vector3f position) {
		center[0] = position.getX();
		center[1] = position.getY() + r[1];
		center[2] = position.getZ();
		pos[0] = position.x;
		pos[1] = position.y;
		pos[2] = position.z;
		return this;
	}
	
	public void update(final Vector3f position, final Vector3f velocity) {
		center[0] = position.getX();
		center[1] = position.getY() + r[1];
		center[2] = position.getZ();
		pos[0] = position.x;
		pos[1] = position.y;
		pos[2] = position.z;
		vel[0] = velocity.x;
		vel[1] = velocity.y;
		vel[2] = velocity.z;
	}

	public static boolean testAABB(final AABB box1, final AABB box2) {
		if (Math.abs(box1.center[0] - box2.center[0]) > (box1.r[0] + box2.r[0]))
			return false;
		if (Math.abs(box1.center[1] - box2.center[1]) > (box1.r[1] + box2.r[1]))
			return false;
		if (Math.abs(box1.center[2] - box2.center[2]) > (box1.r[2] + box2.r[2]))
			return false;
		return true;
	}

	public Vector3f getSize() {
		sizeVec.x = (float)r[0] * 2;
		sizeVec.y = (float)r[1] * 2;
		sizeVec.z = (float)r[2] * 2;
		return sizeVec;
	}

	public void render() {
		glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glLineWidth(0.5f);
		glBegin(GL_QUADS);
		Shape.createTexturelessRect((float)pos[0]-(float)r[0], (float)pos[1], (float)pos[2]-(float)r[2], Color4f.WHITE, (float)r[0]*2, (float)r[1]*2, (float)r[2]*2);
		glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		glPopMatrix();
	}
}