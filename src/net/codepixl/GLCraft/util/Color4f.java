package net.codepixl.GLCraft.util;

public class Color4f {
	public static final Color4f WHITE = new Color4f(1f,1f,1f,1f);
	public static final Color4f BLACK = new Color4f(0f,0f,0f,1f);
	public static final Color4f GRAY = new Color4f(0.5f,0.5f,0.5f,1f);
	public float r,g,b,a;
	public Color4f(float r, float g, float b, float a){
		this.r = r;
		this.g= g;
		this.b = b;
		this.a = a;
	}
}
