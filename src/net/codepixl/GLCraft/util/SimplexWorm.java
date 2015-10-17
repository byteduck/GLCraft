package net.codepixl.GLCraft.util;

import org.lwjgl.util.vector.Vector3f;

public class SimplexWorm {
	private OpenSimplexNoise n = new OpenSimplexNoise();
	public Vector3f pos;
	public Vector3f nextWorm(){
		float x = ((float) n.eval(pos.x,pos.y,pos.z)+1f)/2f*360f;
		Vector3f rot = MathUtils.RotToVel(new Vector3f(0f,0f,x),1f);
		rot.x = Math.round(rot.x);
		rot.y = Math.round(rot.y);
		rot.z = Math.round(rot.z);
		System.out.println(x+","+rot); 
		this.pos = Vector3f.add(pos,rot,null);
		return Vector3f.add(pos,rot,null);
	}
	
	public SimplexWorm(Vector3f pos){
		this.pos = pos;
	}
	
}
