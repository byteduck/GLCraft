package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.command.Command.Permission;
import net.codepixl.GLCraft.util.logging.GLogger;

public class ConsoleCommandExecutor implements CommandExecutor {
	
	@Override
	public int getType() {
		return CommandExecutor.SERVER;
	}

	@Override
	public void sendMessage(String msg) {
		GLogger.log(msg.replaceAll("(§.)|§", ""), LogSource.NONE);
	}

	@Override
	public Permission getPermission() {
		return Permission.SERVER;
	}

}
