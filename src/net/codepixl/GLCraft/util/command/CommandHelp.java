package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public class CommandHelp implements Command{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
		e.sendMessage("COMMANDS:\n[O] - requires OP. [N] - Doesn't require OP.");
		for(Command c : centralManager.commandManager.getCommands()){
			e.sendMessage(c.getUsage()+(c.requiresOp() ? " [O]" : " [N]"));
		}
		return true;
	}

	@Override
	public boolean requiresOp() {
		return false;
	}

	@Override
	public String getUsage() {
		return "help - Helps you.";
	}

}
