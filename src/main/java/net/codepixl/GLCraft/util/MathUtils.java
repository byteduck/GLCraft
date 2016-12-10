package net.codepixl.GLCraft.util;

import java.util.Random;

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

public class MathUtils {
	public static float distance(Vector3f a, Vector3f b){
		float xd = b.getX()-a.getX();
		float yd = b.getY()-a.getY();
		float zd = b.getZ()-a.getZ();
		return (float)Math.sqrt((double)(xd*xd + yd*yd + zd*zd));
	}
	
	public static Vector3f round(Vector3f in){
		return new Vector3f(Math.round(in.x),Math.round(in.y),Math.round(in.z));
	}
	
	public static Vector3f add(Vector3f a, Vector3f b){
		return new Vector3f(a.x+b.x,a.y+b.y,a.z+b.z);
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
		Vector3f pos = new Vector3f((int)Math.floor(x/16) * 16,0,(int)Math.floor(z/16) * 16);
		return pos;
	}
	
    public static int floor_double(double d){
        int i = (int)d;
        return d < (double)i ? i - 1 : i;
    }
    
    public static Vector3f RotToVel(Vector3f rot, float speed){
    	Vector3f ret = new Vector3f();
    	ret.x += speed * (float)Math.sin(Math.toRadians(rot.y));
        ret.z -= speed * (float)Math.cos(Math.toRadians(rot.y));
        ret.y -= speed * (float)Math.sin(Math.toRadians(rot.z));
        return ret;
    }
    
    public static float towardsZero(float f, float amount){
    	if(f < 0){
    		if(f+amount > 0f){
    			return 0f;
    		}else{
    			return f+amount;
    		}
    	}else{
    		if(f-amount < 0f){
    			return 0f;
    		}else{
    			return f-amount;
    		}
    	}
    }
    
    public static float towardsValue(float f, float amount, float value){
    	if(f < value){
    		if(f+amount > value){
    			return value;
    		}else{
    			return f+amount;
    		}
    	}else{
    		if(f-amount < value){
    			return value;
    		}else{
    			return f-amount;
    		}
    	}
    }
    
    //Gives point along a line where the line is a to b. U is the percent along the line.
    public static void PointAlongLine(Vector3f a, Vector3f b, Vector3f dest, float u){
    	dest.x = (1-u)*a.x+u*b.x;
    	dest.y = (1-u)*a.y+u*b.y;
    	dest.z = (1-u)*a.z+u*b.z;
    }

    //A value between a and b at u% along the way.
    public static float pointAlongValues(float a, float b, float u){
		return (1-u)*a+u*b;
	}
    
    public static float easeInOutQuad(float t, float b, float c, float d) {
    	t = t%d;
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}
    
    public static float noEasing(float t, float b, float c, float d) {
    	t = t%d;
    	return c*t/d + b;
    }
    
    public static void modulus(Vector3f f, float mod){
    	f.x%=mod;
    	f.y%=mod;
    	f.z%=mod;
    }
    
    public static Vector3f randomSpherePoint(Random r, float size){
		double z = Constants.randDouble(r, -1, 1);
		double rxy = Math.sqrt(1f - z*z);
		double phi = Constants.randDouble(r, 0f, 2*Math.PI);
		double x = rxy * Math.cos(phi);
		double y = rxy * Math.sin(phi);
		return new Vector3f((float)x*size,(float)y*size,(float)z*size);
    }
    
    public static boolean equals(Vector3f a, Vector3f b, float range){
    	boolean flag = true;
    	if(Math.abs(a.x-b.x) > range)
    		flag = false;
    	if(Math.abs(a.y-b.y) > range)
    		flag = false;
    	if(Math.abs(a.z-b.z) > range)
    		flag = false;
    	return flag;
    }

	public static Vector3f subtract(Vector3f left, Vector3f right){
		return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
	}
}
