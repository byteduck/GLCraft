package net.codepixl.GLCraft.util.command;

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
			reason = "";
		for(int i = 1; i < args.length; i++)
			reason+=(i == 1 ? "" : " ")+args[i];
		WorldManager.saveWorldBlocking();
		centralManager.getWorldManager().closeWorld(reason, true);
		return true;
	}

	@Override
	public Permission getPermission(){
		return Permission.SERVER;
	}

	@Override
	public String getUsage() {
		return "[reason] - Stops the server.";
	}

}
