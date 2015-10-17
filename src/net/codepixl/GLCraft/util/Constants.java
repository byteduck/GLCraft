package net.codepixl.GLCraft.util;

import java.awt.Font;
import java.util.Arrays;
import java.util.Random;

import org.newdawn.slick.TrueTypeFont;

import net.codepixl.GLCraft.world.CentralManager;

public class Constants {
	public static Random rand = new Random();
	public static int viewDistance = 5;
	public static final int WORLDHEIGHT = 5;
	public static final int HEIGHT = 700;
	public static final int WIDTH = 1000;
	public static final int CHUNKSIZE = 16;
	public static final float textSize = 0.5f;
	public static boolean doneGenerating = false;
	public static final int BTNHEIGHT = 30;
	public static TrueTypeFont FONT = new TrueTypeFont(new Font("GLCraft",Font.PLAIN,16), true);
	public static final int START_SCREEN = 0;
	public static final int GAME = 1;
	public static final int SERVER = 2;
	public static int GAME_STATE = START_SCREEN;
	public static CentralManager world;
	public static int worldLength = CHUNKSIZE*viewDistance;
	
	public static void setDoneGenerating(boolean generating){
		doneGenerating = generating;
	}
	
	public static void setState(int state){
		GAME_STATE = state;
	}
	
	public static void setWorld(CentralManager w){
		world = w;
	}
	
	public static byte[] trim(byte[] bytes){
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
	}
}
