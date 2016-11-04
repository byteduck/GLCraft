package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.world.CentralManager;

public interface Command extends CommandExecutable{
	public String getName();
	public Permission getPermission();
	public String getUsage();
	public static enum Permission{
		NONE(0), OP(1), SERVER(2);
		
		public final int val;

		public String getLabel() {
			return "("+this.toString().charAt(0)+")";
		}
		
		private Permission(int val){
			this.val = val;
		}
		//Returns true if perm>=this
	}
}
