package net.codepixl.GLCraft.util.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;

public class CommandManager {
	
	
	public final CentralManager centralManager;
	public final WorldManager worldManager;
	private HashMap<String,Command> commands = new HashMap<String,Command>();;
	private HashMap<String,CommandExecutor> commandQueue = new HashMap<String,CommandExecutor>();
	private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	
	
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
		addCommand(new CommandStop());
		addCommand(new CommandHelp());
		addCommand(new CommandKick());
		addCommand(new CommandGive());
		addCommand(new CommandTime());
	}
	
	public void addCommand(Command c){
		if(!commands.containsKey(c.getName().toLowerCase()))
			this.commands.put(c.getName().toLowerCase(), c);
		else
			throw new DuplicateCommandException(c);
	}
	
	public Collection<Command> getCommands(){
		return commands.values();
	}
	
	public static class DuplicateCommandException extends RuntimeException{
		public DuplicateCommandException(Command c){
			super("Command '"+c.getName()+"' already exists!");
		}
	}
	
	public void addCommandToQueue(String cmd, CommandExecutor ex){
		commandQueue.put(cmd, ex);
	}
	
	public void update(){
		if(GLCraft.getGLCraft().isServer()){
			try{
				if(stdin.ready()){
					commandQueue.put(stdin.readLine(), new ConsoleCommandExecutor());
				}
			}catch(IOException e){
				
			}
		}
		
		@SuppressWarnings("unchecked")
		HashMap<String,CommandExecutor> commandQueue = (HashMap<String,CommandExecutor>)this.commandQueue.clone();
		this.commandQueue.clear();
		for(Entry<String,CommandExecutor> cmd : commandQueue.entrySet()){
			String[] args = cmd.getKey().split(" ");
			if(this.commands.containsKey(args[0].toLowerCase())){
				Command c = this.commands.get(args[0].toLowerCase());
				if(c.getPermission().val <= cmd.getValue().getPermission().val){
					if(c.execute(centralManager, cmd.getValue(), args)); else
						cmd.getValue().sendMessage(this.commands.get(args[0].toLowerCase()).getUsage());
				}else
					cmd.getValue().sendMessage("You do not have permission to do that!");
			}else{
				cmd.getValue().sendMessage(ChatFormat.RED+"Unknown command "+args[0]+"!");
			}
		}
	}

	public Command getCommand(String cmd) {
		return commands.get(cmd.toLowerCase());
	}
}
