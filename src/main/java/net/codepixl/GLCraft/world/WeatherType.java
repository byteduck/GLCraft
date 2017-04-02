package net.codepixl.GLCraft.world;

import net.codepixl.GLCraft.util.Constants;

public enum WeatherType {
	CLEAR(Constants.dayLengthMS/2, Constants.dayLengthMS*5, 0.6f, 1f, 1f, 0f, 3f, 0.6f, 0.05f), //Normal weather. Between 1/2 day long and 5 days long. Normal cloud density, and normal light.
	RAIN(Constants.dayLengthMS/2, Constants.dayLengthMS, 1.1f, 0.6f, 0.8f, 0.8f, 4f, 0.6f, 0.1f);   //Rain. Between 1/2 day long and one day long. Higher cloud density, and 40% darker light.

	public long minDuration, maxDuration;
	public float cloudDensity,lightMultiplier,cloudDarkness,rainOpacity,waveSpeed,waveFrequency,waveMultiplier;
	WeatherType(long minDuration, long maxDuration, float cloudDensity, float lightMultiplier, float cloudDarkness, float rainOpacity, float waveSpeed, float waveFrequency, float waveMultiplier){
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.cloudDensity = cloudDensity;
		this.lightMultiplier = lightMultiplier;
		this.cloudDarkness = cloudDarkness;
		this.rainOpacity = rainOpacity;
		this.waveSpeed = waveSpeed;
		this.waveFrequency = waveFrequency;
		this.waveMultiplier = waveMultiplier;
	}

	public static WeatherType getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
