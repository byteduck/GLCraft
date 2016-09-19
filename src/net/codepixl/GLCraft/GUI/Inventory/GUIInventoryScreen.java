package net.codepixl.GLCraft.GUI.Inventory;

import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUIInventoryScreen extends GUIScreen{
	@Override
	public boolean shouldRenderMouseItem(){
		return true;
	}
	
	@Override
	public void onClose(){
		super.onClose();
		EntityPlayer p = Constants.world.getWorldManager().getEntityManager().getPlayer();
		if(!p.mouseItem.isNull())
			p.dropItem(p.mouseItem);
		p.mouseItem = new ItemStack();
	}
}
