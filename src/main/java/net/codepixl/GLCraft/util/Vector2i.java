package net.codepixl.GLCraft.util;

import java.io.Serializable;

public class Vector2i implements Serializable{
	public int x,y;
	public Vector2i(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2i(float x, float y){
		this.x = (int) x;
		this.y = (int) y;
	}

	public Vector2i(Vector2i pos) {
		this.x = pos.x;
		this.y = pos.y;
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

	public Vector2i mult(int m) {
		return new Vector2i(this.x*m, this.y*m);
	}

	public Vector2i set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
}