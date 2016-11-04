package net.codepixl.GLCraft.testthings;

import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.world.tile.PluginTile;

public class TestTile extends PluginTile{

	public TestTile(Plugin p) {
		super(p);
	}
	
	@Override
	public String getName(){
		return "bob";
	}
	
	@Override
	public String getTextureName(){
		return "fire";
	}
	
	@Override
	public byte getLightLevel(byte meta){
		return (byte)15;
	}
	
	@Override
	public boolean isTransparent(){
		return true;
	}

}
