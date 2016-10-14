package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.network.packet.PacketKick;
import net.codepixl.GLCraft.util.command.Command.Permission;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class CommandKick implements Command{

	@Override
	public String getName(){
		return "kick";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		if(args.length < 2)
			return false;
		EntityPlayerMP p = centralManager.getWorldManager().getPlayer(args[1]);
		String reason = "No Reason Given";
		if(args.length > 2)
			reason = "";
		for(int i = 2; i < args.length; i++)
			reason+=(i == 2 ? "" : " ")+args[i];
		if(p != null)
			centralManager.sendPacket(new PacketKick(reason), p);
		else
			e.sendMessage("Unknown player "+args[1]+"!");
		return true;
	}

	@Override
	public Permission getPermission(){
		return Permission.OP;
	}
	@Override
	public String getUsage(){
		return "kick <player> [reason] - Kicks the player given.";
	}

}
