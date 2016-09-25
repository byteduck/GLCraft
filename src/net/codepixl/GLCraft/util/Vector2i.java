package net.codepixl.GLCraft.util;

import org.lwjgl.util.vector.Vector3f;

public class Vector2i {
	public int x,y;
	public Vector2i(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2i(float x, float y){
		this.x = (int) x;
		this.y = (int) y;
	}

	@Override
	public String toString(){
		return "["+x+","+y+"]";
	}
	
	@Override
	public int hashCode() {
	    int hash = (x << 2)+(y << 1);
	    return hash;
	}
	
	@Override
	public boolean equals(Object o){
		return (o instanceof Vector2i) && (((Vector2i)o).x == x) && (((Vector2i)o).y == y);
	}
}