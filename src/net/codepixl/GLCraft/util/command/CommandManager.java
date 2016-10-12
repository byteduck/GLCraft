package net.codepixl.GLCraft.util.command;

import java.util.HashMap;

import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;

public class CommandManager {
	public final CentralManager centralManager;
	public final WorldManager worldManager;
	private HashMap<String,Command> commands;
	public CommandManager(CentralManager c){
		if(c.isServer){
			centralManager = c;
			worldManager = c.getWorldManager();
			addCommands();
		}else{
			throw new RuntimeException("Only use CommandManager with a server!");
		}
	}
	
	private void addCommands(){
		
	}
	
	public void addCommand(Command c){
		if(!commands.containsKey(c.getName().toLowerCase()))
			this.commands.put(c.getName().toLowerCase(), c);
		else
			throw new DuplicateCommandException(c);
	}
	
	public static class DuplicateCommandException extends RuntimeException{
		public DuplicateCommandException(Command c){
			super("Command '"+c.getName()+"' already exists!");
		}
	}
	
	public void update(){
		//TODO Read stdin for commands if dedicated server, and chat if not
	}
}
