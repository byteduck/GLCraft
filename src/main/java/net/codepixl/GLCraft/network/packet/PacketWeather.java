package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.WeatherState;
import net.codepixl.GLCraft.world.WorldManager;

public class PacketWeather extends Packet {
	private WeatherState weather;
	public PacketWeather(WeatherState weather) {
		this.weather = weather;
	}

	public WeatherState getWeather(WorldManager worldManager){
		weather.worldManager = worldManager;
		return weather;
	}
}
