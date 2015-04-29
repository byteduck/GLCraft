package net.codepixl.GLCraft.util;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

public class MathUtils {
	public static float distance(Vector3f a, Vector3f b){
		
		float xd = b.getX()-a.getX();
		float yd = b.getY()-a.getY();
		float zd = b.getZ()-a.getZ();
		return (float)Math.sqrt((double)(xd*xd + yd*yd + zd*zd));
	}
	
	public static Color4f mult(Color4f a, Color4f b){
		Color4f res = Color4f.WHITE;
		res.r = a.r * b.r;
		res.g = a.g * b.g;
		res.b = a.b * b.b;
		res.a = a.a * b.a;
		return res;
	}
	
	public static boolean compare(Vector3f a, Vector3f b){
		return Math.floor(a.x) == Math.floor(b.x) && Math.floor(a.y) == Math.floor(b.y) && Math.floor(a.z) == Math.floor(b.z);
	}
	
	public static Vector3f coordsToChunkPos(int x, int y, int z){
		Vector3f pos = new Vector3f((int)Math.floor(x/16) * 16,(int)Math.floor(y/16) * 16,(int)Math.floor(z/16) * 16);
		return pos;
	}
}
