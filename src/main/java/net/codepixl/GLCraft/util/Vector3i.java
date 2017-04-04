package net.codepixl.GLCraft.util;

import org.lwjgl.util.vector.Vector3f;

import java.io.Serializable;

public class Vector3i implements Serializable{
	public int x,y,z;
	private Vector3f v3f = new Vector3f();
	public Vector3i(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3i(float x, float y, float z){
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}
	
	public Vector3i(Vector3f f){
		this.x = (int)f.x;
		this.y = (int)f.y;
		this.z = (int)f.z;
	}
	
	public Vector3i(Vector3i v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	@Override
	public String toString(){
		return "["+x+","+y+","+z+"]";
	}
	
	@Override
	public int hashCode() {
	    int hash = (((new Integer(x).hashCode())*31+new Integer(y).hashCode())*31+new Integer(z));
	    return hash;
	}
	
	@Override
	public boolean equals(Object o){
		return (o instanceof Vector3i) && (((Vector3i)o).x == x) && (((Vector3i)o).y == y) && (((Vector3i)o).z == z);
	}

	public Vector3f toVector3f() {
		v3f.set(x,y,z);
		return v3f;
	}

	public Vector3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
}
