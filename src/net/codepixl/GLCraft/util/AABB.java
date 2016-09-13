package net.codepixl.GLCraft.util;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.render.Shape;

public class AABB {
	public double center[];
	public double pos[];
	public double vel[];
	public double r[];

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

	public void update(final Vector3f position) {
		center[0] = position.getX();
		center[1] = position.getY() + r[1];
		center[2] = position.getZ();
		pos[0] = position.x;
		pos[1] = position.y;
		pos[2] = position.z;
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
		return new Vector3f((float)r[0] * 2, (float)r[1] * 2, (float)r[2] * 2);
	}

	public void render() {
		glPushMatrix();
		glBegin(GL_QUADS);
		Shape.createCube((float)pos[0] - (float)r[0]*2, (float)pos[1], (float)pos[2] - (float)r[2]*2, new Color4f(1.0f, 1.0f, 1.0f, 1.0f), new float[] { 0f, 0f }, (float)r[0]*2f);
		glEnd();
		glPopMatrix();
	}
}