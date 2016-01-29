package net.codepixl.GLCraft.world.tile;

public class PluginTile extends Tile{
	public static byte assignedID = -1;
	@Override
	public byte getId(){
		return assignedID;
	}
	
	@Override
	public void registerTile(){
		//System.out.println("Attempted to register a plugin tile the wrong way!");
	}
	
	public PluginTile(){
		
	}
}
