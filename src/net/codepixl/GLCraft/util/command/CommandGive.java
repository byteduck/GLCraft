package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.Utils;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.item.ItemStack;

public class CommandGive implements Command{

	@Override
	public String getName() {
		return "give";
	}

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args){
		try{
			if(args.length >= 3){
				EntityPlayerMP p = centralManager.getWorldManager().getPlayer(args[1]);
				if(p != null){
					String[] arg2 = args[2].split(":");
					ItemStack item = Utils.getItemOrTile(arg2[0]);
					if(!item.isNull()){
						int count = 1;
						byte meta = 0;
						if(args.length > 3)
							count = Integer.parseInt(args[3]);
						if(arg2.length > 1)
							meta = Byte.parseByte(arg2[1]);
						item.setMeta(meta);
						item.count = count;
						p.addToInventory(item);
						return true;
					}else{
						e.sendMessage("Unknown item/tile!");
						return true;
					}
				}else{
					e.sendMessage("Unknown player "+args[1]+"!");
					return true;
				}
			}
		}catch(NumberFormatException ex){
			e.sendMessage("Invalid number!");
			return true;
		}
		return false;
	}

	@Override
	public Permission getPermission() {
		return Permission.OP;
	}

	@Override
	public String getUsage() {
		return "<player> <item:meta/tile:meta> [count] - Give someone item(s).";
	}

}
