package net.codepixl.GLCraft.util;

import java.awt.Font;
import java.util.Arrays;
import java.util.Random;

import org.newdawn.slick.TrueTypeFont;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.CentralManager;

public class Constants {
	public static Random rand = new Random();
	public static int viewDistance = 10;
	public static int seaLevel = 10;
	public static final int WORLDHEIGHT = 5;
	public static final int HEIGHT = 700;
	public static final int WIDTH = 1000;
	public static final int CHUNKSIZE = 16;
	public static final float textSize = 0.5f;
	public static boolean doneGenerating = false;
	public static final int BTNHEIGHT = 30;
	public static final int BTNPADDING = 10;
	public static final Color4f BTNCOLOR = new Color4f(0f,0f,0f,1f);
	public static final Color4f BTNHOVERCOLOR = new Color4f(0.25f,0.25f,0.25f,1f);
	public static final Color4f BTNPRESSEDCOLOR = new Color4f(0.35f,0.35f,0.35f,1f);
	public static final Color4f BTNTEXTCOLOR = new Color4f(1f,1f,1f,1f);
	public static TrueTypeFont FONT = new TrueTypeFont(new Font("GLCraft",Font.PLAIN,16), true);
	public static final int START_SCREEN = 0;
	public static final int GAME = 1;
	public static final int SERVER = 2;
	public static final String GLCRAFTDIR = System.getProperty("user.home")+"/GLCraft/";
	public static int GAME_STATE = START_SCREEN;
	public static CentralManager world;
	public static int worldLength = CHUNKSIZE*viewDistance;
	public static boolean IS_SERVER = false;
	
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
	public static int randInt(int min, int max) {
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	public static float randFloat(float min, float max) {
	    float randomNum = rand.nextFloat() * (max - min) + min;
	    return randomNum;
	}
}
