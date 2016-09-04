package net.codepixl.GLCraft.util;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;

public class AABB {
	public Vector3f center;
	public Vector3f pos;
	public Vector3f vel;
	public float r[];

	public AABB(final float width, final float height, final float length) {
		center = new Vector3f();
		pos = new Vector3f();
		r = new float[3];
		r[0] = width * 0.5f;
		r[1] = height * 0.5f;
		r[2] = length * 0.5f;
		vel = new Vector3f();
	}

	public AABB(AABB aabb) {
		center = new Vector3f(aabb.center);
		pos = new Vector3f(aabb.pos);
		r = Arrays.copyOf(aabb.r, aabb.r.length);
		vel = new Vector3f(aabb.vel);
	}

	public void update(final Vector3f position) {
		center.x = position.getX();
		center.y = position.getY() + r[1];
		center.z = position.getZ();
		pos = new Vector3f(position);
	}
	
	public void update(final Vector3f position, final Vector3f vel) {
		center.x = position.getX();
		center.y = position.getY() + r[1];
		center.z = position.getZ();
		pos = new Vector3f(position);
		this.vel = new Vector3f(vel);
	}

	public static boolean testAABB(final AABB box1, final AABB box2) {
		if (Math.abs(box1.center.x - box2.center.x) > (box1.r[0] + box2.r[0]))
			return false;
		if (Math.abs(box1.center.y - box2.center.y) > (box1.r[1] + box2.r[1]))
			return false;
		if (Math.abs(box1.center.z - box2.center.z) > (box1.r[2] + box2.r[2]))
			return false;
		return true;
	}

	public Vector3f getSize() {
		return new Vector3f(r[0] * 2, r[1] * 2, r[2] * 2);
	}

	public void render() {
		glPushMatrix();
		glBegin(GL_QUADS);
		Shape.createCube(pos.x - r[0], pos.y, pos.z - r[0], new Color4f(1.0f, 1.0f, 1.0f, 1.0f), new float[] { 0f, 0f }, 1.0f);
		glEnd();
		glPopMatrix();
	}
}