package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.world.CentralManager;

import java.util.Iterator;

public class CommandHelp implements Command{

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
		Iterator<Command> i = centralManager.commandManager.getCommands(e.getPermission()).iterator();
		int page = 1;
		if(args.length >= 2)
			try {
				page = Integer.parseInt(args[1]);
			}catch(NumberFormatException e2){
				Command c = centralManager.commandManager.getCommand(args[1]);
				if(c != null)
					e.sendMessage(ChatFormat.GREEN+c.getName()+ChatFormat.WHITE+" "+c.getUsage()+" "+ChatFormat.GOLD+c.getPermission().getLabel());
				else
					e.sendMessage(ChatFormat.RED+"Unknown command!");
				return true;
			}
		e.sendMessage(ChatFormat.GREEN+"COMMANDS: "+ChatFormat.GOLD+"Permissions- (N)one (O)p (S)erver");
		e.sendMessage(ChatFormat.GREEN+"Showing page "+page+" of "+(centralManager.commandManager.getCommands().size()/8+(centralManager.commandManager.getCommands().size()%8==0 ? 0 : 1)));
		for(int s = 0; s < (page-1)*8; s++)
			if(i.hasNext())
				i.next();
		int done = 0;
		while(i.hasNext() && done < 8){
			Command c = i.next();
			if(c.getPermission().val <= e.getPermission().val)
				e.sendMessage(ChatFormat.GREEN+c.getName()+ChatFormat.WHITE+" "+c.getUsage()+" "+ChatFormat.GOLD+c.getPermission().getLabel());
			done++;
		}
		return true;
	}

	@Override
	public Permission getPermission(){
		return Permission.NONE;
	}

	@Override
	public String getUsage() {
		return "[page/command] - Get help for a command/commands.";
	}

}
