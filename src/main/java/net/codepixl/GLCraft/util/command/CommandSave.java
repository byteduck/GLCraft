package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;

public class CommandSave implements Command{

	@Override
	public String getName(){
		return "save";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		WorldManager.saveWorldBlocking();
		e.sendMessage(ChatFormat.AQUA+"World Saved.");
		return true;
	}

	@Override
	public Permission getPermission(){
		return Permission.OP;
	}

	@Override
	public String getUsage(){
		return "- Saves the world.";
	}

}
