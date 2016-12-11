package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.GUI.Inventory.GUICraftingAdvanced;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.tile.material.Material;

public class TileWorkbench extends Tile{
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Workbench";
	}
	
	@Override
	public Material getMaterial(){
		return Material.WOOD;
	}
	
	@Override
	public String getIconName(){
		return "workbench_top";
	}
	
	@Override
	public boolean hasMultipleTextures(){
		return true;
	}
	
	@Override
	public String[] getMultiTextureNames(){
		return new String[]{
			"wood_oak",
			"workbench_top",
			"wood_oak",
			"wood_oak",
			"wood_oak",
			"wood_oak"
		};
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return new Color4f(1,1,1,1);
	}
	
	@Override
	public float getHardness(){
		return 2f;
	}
	
	@Override
	public boolean onClick(int x, int y, int z, EntityPlayer p, WorldManager worldManager){
		worldManager.centralManager.guiManager.showGUI(new GUICraftingAdvanced(p));
		return true;
	}
	
}
