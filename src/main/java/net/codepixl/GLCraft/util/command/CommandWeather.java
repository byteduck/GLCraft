package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WeatherState;
import net.codepixl.GLCraft.world.WeatherType;

public class CommandWeather implements Command{
	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
		if(args.length == 2){
			String arg = args[1];
			if(arg.equalsIgnoreCase("change")){
				centralManager.getWorldManager().changeWeather();
				e.sendMessage("Changed weather to "+ChatFormat.YELLOW+centralManager.getWorldManager().currentWeather.toStringSimple());
			}else{
				try {
					WeatherType wt = WeatherType.valueOf(arg.toUpperCase());
					centralManager.getWorldManager().changeWeather(new WeatherState(wt, centralManager.getWorldManager()));
					e.sendMessage("Changed weather to " + ChatFormat.YELLOW + centralManager.getWorldManager().currentWeather.toStringSimple());
				}catch(IllegalArgumentException e2){e.sendMessage(ChatFormat.RED+"Invalid weather type!");}
			}
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "weather";
	}

	@Override
	public Permission getPermission() {
		return Permission.OP;
	}

	@Override
	public String getUsage() {
		return "<type/change> - Changes weather, or change for random.";
	}
}
