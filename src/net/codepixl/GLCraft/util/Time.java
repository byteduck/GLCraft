package net.codepixl.GLCraft.util;

public class Time {
	public static final double SECOND = 1000000000L;
	private static double delta = 0;
	
	public static void setDelta(double delt){
		delta = delt;
	}
	
	public static double getDelta(){
		return delta;
	}

	public static long getTime() {
		return System.nanoTime();
	}
}
