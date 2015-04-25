package net.codepixl.GLCraft.util;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.codepixl.GLCraft.world.World;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.TrueTypeFont;

public class Constants {
	public static Random rand = new Random();
	public static int viewDistance = 5;
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
	public static World world;
	public static String ConnIP = "null";
	public static boolean connected = false;
	public static boolean isMultiplayer = false;
	public static boolean downloadedWorld = false;
	public static boolean transferredWorld = false;
	public static boolean isServer = false;
	public static byte[][][] worldBuf;
	public static PipedOutputStream packetsToSend = new PipedOutputStream();
	public static PipedOutputStream actionsToDo = new PipedOutputStream();
	
	public static void setDoneGenerating(boolean generating){
		doneGenerating = generating;
	}
	
	public static void setState(int state){
		GAME_STATE = state;
	}
	
	public static void setWorld(World w){
		world = w;
	}
	
	public static void setIP(String ip){
		ConnIP = ip.replace("/", " ").trim();
	}
	
	public static void connectedToServer(){
		connected = true;
	}
	
	public static void downloadedWorld(){
		downloadedWorld = true;
	}
	
	public static void setServer(boolean server){
		isServer = server;
	}
	public static byte[] trim(byte[] bytes)
	{
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
	}
}
