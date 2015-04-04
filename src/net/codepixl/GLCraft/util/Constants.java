package net.codepixl.GLCraft.util;

import java.util.Random;

public class Constants {
	public static Random rand = new Random();
	public static int viewDistance = 5;
	public static final int HEIGHT = 700;
	public static final int WIDTH = 1000;
	public static final int CHUNKSIZE = 16;
	public static final float textSize = 0.5f;
	public static boolean doneGenerating = false;
	
	public static void setDoneGenerating(boolean generating){
		doneGenerating = generating;
	}
}
