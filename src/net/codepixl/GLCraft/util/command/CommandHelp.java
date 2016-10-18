package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.world.CentralManager;

public class CommandHelp implements Command{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
		if(args.length == 1){
			e.sendMessage(ChatFormat.GREEN+"COMMANDS: "+ChatFormat.GOLD+"Permissions- (N)one (O)p (S)erver");
			for(Command c : centralManager.commandManager.getCommands()){
				if(c.getPermission().val <= e.getPermission().val)
					e.sendMessage(ChatFormat.GREEN+c.getName()+ChatFormat.WHITE+" "+c.getUsage()+" "+ChatFormat.GOLD+c.getPermission().getLabel());
			}
		}else{
			Command c = centralManager.commandManager.getCommand(args[1]);
			if(c != null){
				e.sendMessage(ChatFormat.GOLD+"Permissions- (N)one (O)p (S)erver");
				e.sendMessage(ChatFormat.GREEN+c.getName()+ChatFormat.WHITE+" "+c.getUsage()+" "+ChatFormat.GOLD+c.getPermission().getLabel());
			}else
				e.sendMessage("Cannot give help for that command!");
		}
		return true;
	}

	@Override
	public Permission getPermission(){
		return Permission.NONE;
	}

	@Override
	public String getUsage() {
		return "[command] - Get help for a command/commands.";
	}

}
