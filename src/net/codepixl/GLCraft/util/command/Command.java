package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public interface Command{
	public String getName();
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args);
	public boolean requiresOp();
	public String getUsage();
}
