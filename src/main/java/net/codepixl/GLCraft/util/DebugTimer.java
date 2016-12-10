package net.codepixl.GLCraft.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DebugTimer{
	private long time = 0;
	private long ltime = 0;
	private long ptime = 0;
	public String name;
	private static HashMap<String,DebugTimer> timers = new HashMap<String,DebugTimer>();
	public boolean paused = false;
	
	public static void addTimer(String name){
		timers.put(name,new DebugTimer(name));
	}
	
	public static void addTimer(DebugTimer debug){
		timers.put(debug.name,debug);
	}
	
	public static DebugTimer getTimer(String name){
		return timers.get(name);
	}
	
	public static ArrayList<DebugTimer> getTimers(){
		return new ArrayList<DebugTimer>(timers.values());
	}
	
	public static void startTimer(String name){
		getTimer(name).start();
	}
	
	public static void endTimer(String name){
		getTimer(name).end();
	}
	
	public static void pauseTimer(String name){
		getTimer(name).pause();
	}
	
	public DebugTimer(String name){
		this.name = name;
	}
	
	public void start(){
		time = System.nanoTime();
	}
	
	public void end(){
		ltime = System.nanoTime()-time+ptime;
		ptime = 0;
	}
	
	public void pause(){
		ptime += System.nanoTime()-time;
	}
	
	public float getMillis(){
		return ((float)ltime)/1000000f;
	}
	
	public long getNano(){
		return ltime;
	}
	
	@Override
	public String toString(){
		return name+": "+String.format("%.2f",getMillis())+"ms";
	}
}
