package net.codepixl.GLCraft.world;

import net.codepixl.GLCraft.util.Constants;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class WeatherState implements Serializable {
	public long weatherLength; //Time in milliseconds of the length of this weather state
	public long startTime; //currentTimeMillis that this weather state started at
	public WeatherType type;
	public transient WorldManager worldManager;

	public WeatherState(WeatherType type, WorldManager worldManager){
		this.type = type;
		this.weatherLength = ThreadLocalRandom.current().nextLong(type.minDuration, type.maxDuration);
		this.startTime = worldManager.getWorldTime();
		this.worldManager = worldManager;
	}

	public boolean isWeatherOver(){
		return worldManager.getWorldTime()-startTime >= weatherLength;
	}

	public long getTimeRemaining(){
		return (startTime+weatherLength)-worldManager.getWorldTime();
	}

	public static WeatherState random(WeatherType exclude, WorldManager worldManager){
		WeatherType choose = WeatherType.getRandom();
		while(choose == exclude) choose = WeatherType.getRandom();
		return new WeatherState(choose, worldManager);
	}

	@Override
	public String toString(){
		return type+" "+String.format("%.3f", getTimeRemaining()/(float)Constants.dayLengthMS)+" days left ("+String.format("%.3f",weatherLength/(float)Constants.dayLengthMS)+" days long)";
	}

	public String toStringSimple(){
		return type+" ("+String.format("%.3f",weatherLength/(float)Constants.dayLengthMS)+" days long)";
	}
}
