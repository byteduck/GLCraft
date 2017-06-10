package net.codepixl.GLCraft.network.packet;

import net.codepixl.GLCraft.world.WeatherState;
import net.codepixl.GLCraft.world.WorldManager;

public class PacketWeather extends Packet {

	private static final long serialVersionUID = 5569171424586403325L;
	
	private WeatherState weather;
	public PacketWeather(WeatherState weather) {
		this.weather = weather;
	}

	public WeatherState getWeather(WorldManager worldManager){
		weather.worldManager = worldManager;
		return weather;
	}
}
