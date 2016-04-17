package net.codepixl.GLCraft.world.entity.tileentity;

import net.codepixl.GLCraft.GUI.tileentity.GUIChest;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class TileEntityChest extends TileEntityContainer{

	public TileEntityChest(int x, int y, int z, int size, WorldManager worldManager) {
		super(x, y, z, size, worldManager);
	}
	
	@Override
	public void update(){
		super.update();
	}
	
	public void openGUI(WorldManager w, EntityPlayer p){
		w.world.guiManager.showGUI(new GUIChest(this, p));
	}

}
