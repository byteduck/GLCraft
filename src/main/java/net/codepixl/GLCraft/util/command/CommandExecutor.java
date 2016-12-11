package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.command.Command.Permission;

public interface CommandExecutor{
	
	public static int PLAYER = 0;
	public static int SERVER = 1;
	
	public int getType();
	
	public void sendMessage(String msg);
	
	public Permission getPermission();
	
}
