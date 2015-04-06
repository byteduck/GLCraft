package net.codepixl.GLCraft.util;

import org.lwjgl.util.vector.Vector3f;

public class AABB {
   public Vector3f center;
   public float r[];
   
   public AABB(final float width, final float height, final float length) {
      center = new Vector3f();
      r = new float[3];
      r[0] = width * 0.5f;
      r[1] = height * 0.5f;
      r[2] = length * 0.5f;
   }
   
   public void update(final Vector3f position) {
      center.x = position.getX();
      center.y = position.getY();
      center.z = position.getZ();
   }
   public static boolean testAABB(final AABB box1, final AABB box2) {
	   if (Math.abs(box1.center.x - box2.center.x) > (box1.r[0] + box2.r[0])) return false;
	   if (Math.abs(box1.center.y - box2.center.y) > (box1.r[1] + box2.r[1])) return false;
	   if (Math.abs(box1.center.z - box2.center.z) > (box1.r[2] + box2.r[2])) return false;
	   return true;
	}
}