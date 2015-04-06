package net.codepixl.GLCraft;

import java.awt.Color;

import net.codepixl.GLCraft.util.Constants;

import com.thehowtotutorial.splashscreen.JSplash;

public class Splash{
	public int total  = (int)Math.pow(Constants.viewDistance,3);
	private JSplash splash;
	private int currentChunk = 0;
	
	public Splash(){
		this.splash = new JSplash(getClass().getResource("/textures/icons/splash.png"), true, true, false, "", null, Color.RED, Color.BLACK);
		splash.splashOn();
		splash.setProgress(2,"so");
	}
	
	public JSplash getSplash(){
		return this.splash;
	}
	
	public int currentChunk(){
		return this.currentChunk;
	}
	
	public void addCurrentChunk(int add){
		this.currentChunk+=add;
	}
}