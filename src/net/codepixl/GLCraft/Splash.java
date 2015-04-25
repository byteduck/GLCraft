package net.codepixl.GLCraft;

import java.awt.Color;

import net.codepixl.GLCraft.util.Constants;

import com.thehowtotutorial.splashscreen.JSplash;

public class Splash{
	public int total  = (int)Math.pow(Constants.viewDistance,3);
	private JSplash splash;
	private int currentChunk = 0;
	private boolean shouldPopulate;
	private int currentTile = 0;
	
	public Splash(){
		this.splash = new JSplash(getClass().getResource("/textures/icons/splash.png"), true, true, false, "", null, Color.RED, Color.BLACK);
		splash.splashOn();
		splash.setAlwaysOnTop(true);
		splash.setProgress(0,"Loading...");
	}
	
	public JSplash getSplash(){
		return this.splash;
	}
	
	public boolean shouldPopulate(){
		return shouldPopulate;
	}
	
	public int currentChunk(){
		return this.currentChunk;
	}
	
	public int currentTilePercentage(){
		return (int) ((float)(((float)this.currentTile/(float)Math.pow(Constants.viewDistance*Constants.CHUNKSIZE,3))*(float)100));
	}
	
	public void addCurrentChunk(int add){
		this.currentChunk+=add;
	}
	
	public void addCurrentTile(int add){
		this.currentTile+=add;
	}
}