package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public interface Command{
	public String getName();
	public void execute(CentralManager centralManager, CommandExecutor e, String... args);
	public boolean requiresOp();
}
