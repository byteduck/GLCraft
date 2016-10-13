package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;

public class CommandStop implements Command{

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		String reason = "Server Closing";
		if(args.length > 1)
			reason = args[1];
		GLogger.log("Closing server: "+reason, LogSource.SERVER);
		WorldManager.saveWorld(true, false);
		return true;
	}

	@Override
	public boolean requiresOp() {
		return true;
	}

	@Override
	public String getUsage() {
		return "stop [reason] - Stops the server.";
	}

}
