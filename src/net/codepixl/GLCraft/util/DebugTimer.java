package net.codepixl.GLCraft.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DebugTimer{
	private long time = 0;
	private long ltime = 0;
	public String name;
	private static HashMap<String,DebugTimer> timers = new HashMap<String,DebugTimer>();
	
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
	
	public DebugTimer(String name){
		this.name = name;
	}
	
	public void start(){
		time = System.nanoTime();
	}
	
	public void end(){
		ltime = System.nanoTime()-time;
	}
	
	public float getMillis(){
		return ((float)ltime)/1000000f;
	}
	
	public long getNano(){
		return ltime;
	}
	
	@Override
	public String toString(){
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		return name+": "+df.format(getMillis())+"ms";
	}
}
