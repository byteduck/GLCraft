package net.codepixl.GLCraft;

import java.awt.Color;

import com.thehowtotutorial.splashscreen.JSplash;

import net.codepixl.GLCraft.util.Constants;

public class CustomSplash {
	public int total  = (int)Math.pow(Constants.viewDistance,2);
	private JSplash splash;
	private int currentChunk = 0;
	private boolean shouldPopulate;
	
	public CustomSplash(){
		this.splash = new JSplash(getClass().getResource("/textures/icons/splash.png"), true, true, false, "", null, Color.RED, Color.BLACK);
		splash.splashOn();
		splash.setAlwaysOnTop(true);
		splash.setProgress(0,"Loading...");
	}
	
	public JSplash getSplash(){
		return this.splash;
	}
}
