package net.codepixl.GLCraft.util.command;

import com.sun.scenario.Settings;
import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.SettingsManager;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class CommandOp implements Command{

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		if(args.length == 2){
			EntityPlayerMP p = centralManager.getWorldManager().getPlayer(args[1]);
			if(p != null){
				if(p.getPermission() == Permission.OP) {
					p.setPermission(Permission.NONE);
					if(GLCraft.getGLCraft().isServer()) SettingsManager.setSetting("ops",SettingsManager.getSetting("ops").replace(","+args[1],"").replace(args[1],""));
				}else {
					p.setPermission(Permission.OP);
					if(GLCraft.getGLCraft().isServer()) SettingsManager.setSetting("ops", SettingsManager.getSetting("ops")+","+args[1]);
				}
				e.sendMessage(args[1]+"'s permission level is now "+p.getPermission()+".");
			}else
				e.sendMessage("Player "+args[1]+" does not exist.");
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "op";
	}

	@Override
	public Permission getPermission() {
		return Permission.OP;
	}

	@Override
	public String getUsage() {
		return "ops/deops someone.";
	}
}
