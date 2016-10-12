package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public class CommandStop implements Command{

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public void execute(CentralManager centralManager, CommandExecutor e, String... args) {
		
	}

	@Override
	public boolean requiresOp() {
		return true;
	}

}
