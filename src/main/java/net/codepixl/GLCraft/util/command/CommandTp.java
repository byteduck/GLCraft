package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class CommandTp implements Command{
	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		if(!(e instanceof EntityPlayer)){
			e.sendMessage("You're not a player.");
			return true;
		}
		EntityPlayer send = (EntityPlayer)e;
		if(args.length == 2){
			EntityPlayerMP p = centralManager.getWorldManager().getPlayer(args[1]);
			if(p != null){
				send.setPos(p.getPos());
			}else{
				e.sendMessage("Player "+args[1]+" does not exist.");
			}
			return true;
		}else if(args.length == 4){
			try{
				float x = Float.parseFloat(args[1]);
				float y = Float.parseFloat(args[2]);
				float z = Float.parseFloat(args[3]);
				send.setPos(x,y,z);
			}catch(NumberFormatException e2){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "tp";
	}

	@Override
	public Permission getPermission() {
		return Permission.OP;
	}

	@Override
	public String getUsage() {
		return "<player> OR <x> <y> <z> - Teleport!";
	}
}
