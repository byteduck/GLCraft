package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public class CommandTime implements Command {

	@Override
	public String getName() {
		return "time";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		if(args.length == 3){
			try{
				String func = args[1];
				long num = Long.parseLong(args[2]);
				if(func.equalsIgnoreCase("set"))
					centralManager.getWorldManager().setWorldTime(num);
				else if(func.equalsIgnoreCase("add"))
					centralManager.getWorldManager().setWorldTime(centralManager.getWorldManager().getWorldTime()+num);
				else
					return false;
				return true;
			}catch(NumberFormatException e2){
				e.sendMessage("Invalid number!");
				return true;
			}
		}
		return false;
	}

	@Override
	public Permission getPermission() {
		return Permission.OP;
	}

	@Override
	public String getUsage() {
		return "<set/add> <amount> - Manipulate time.";
	}

}
