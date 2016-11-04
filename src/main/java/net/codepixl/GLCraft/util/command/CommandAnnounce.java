package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.util.StringUtil;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.network.packet.PacketChat;

public class CommandAnnounce implements Command{

	@Override
	public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
		if(args.length > 1){
			String message = StringUtil.fromArray(args, " ", 1).replaceAll("(?<!\\\\)&", Character.toString(ChatFormat.DELIMETER)).replace("\\&", "&");
			centralManager.sendPacket(new PacketChat(message));
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "announce";
	}

	@Override
	public Permission getPermission() {
		return Permission.OP;
	}

	@Override
	public String getUsage() {
		return "<message> - Announces a message. Use &* for formatting.";
	}
	
}
