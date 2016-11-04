package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public interface CommandExecutable{
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args);
}
