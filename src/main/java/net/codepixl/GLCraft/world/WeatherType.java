package net.codepixl.GLCraft.world;

import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.util.Constants;

public enum WeatherType {
	CLEAR(Constants.dayLengthMS/2, Constants.dayLengthMS*5, 0.6f, 1f, 1f, 0f, null, 0f, 3f, 0.6f, 0.05f), //Normal weather. Between 1/2 day long and 5 days long. Normal cloud density, and normal light.
	RAIN(Constants.dayLengthMS/2, Constants.dayLengthMS, 1.1f, 0.6f, 0.8f, 0.8f, Spritesheet.rain, 1f, 4f, 0.6f, 0.1f),   //Rain. Between 1/2 day long and one day long. Higher cloud density, and 40% darker light.
	SNOW(Constants.dayLengthMS/2, Constants.dayLengthMS, 1.3f, 0.7f, 1f, 1f, Spritesheet.snow, 0.1f, 1f,0.6f, 0.05f);

	public long minDuration, maxDuration;
	public float cloudDensity, lightMultiplier, cloudDarkness, precipitationOpacity, waveSpeed, waveFrequency,
			waveMultiplier, rainSpeed;
	public Spritesheet precipitationSpritesheet;
	WeatherType(long minDuration, long maxDuration, float cloudDensity, float lightMultiplier, float cloudDarkness,
				float rainOpacity, Spritesheet precipitationSpritesheet, float rainSpeed, float waveSpeed,
				float waveFrequency, float waveMultiplier){
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.cloudDensity = cloudDensity;
		this.lightMultiplier = lightMultiplier;
		this.cloudDarkness = cloudDarkness;
		this.precipitationOpacity = rainOpacity;
		this.waveSpeed = waveSpeed;
		this.waveFrequency = waveFrequency;
		this.waveMultiplier = waveMultiplier;
		this.precipitationSpritesheet = precipitationSpritesheet;
		this.rainSpeed = rainSpeed;
	}

	public static WeatherType getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}
