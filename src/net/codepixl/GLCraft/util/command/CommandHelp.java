package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.command.Command.Permission;
import net.codepixl.GLCraft.world.CentralManager;

public class CommandHelp implements Command{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
		e.sendMessage("COMMANDS:\nPermissions- [N]one [O]p [S]erver");
		for(Command c : centralManager.commandManager.getCommands()){
			e.sendMessage(c.getUsage()+" "+c.getPermission().getLabel());
		}
		return true;
	}

	@Override
	public Permission getPermission(){
		return Permission.NONE;
	}

	@Override
	public String getUsage() {
		return "help - Helps you.";
	}

}
