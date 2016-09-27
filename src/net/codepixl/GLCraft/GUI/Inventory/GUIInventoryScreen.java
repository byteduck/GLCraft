package net.codepixl.GLCraft.GUI.Inventory;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUIInventoryScreen extends GUIScreen{
	
	public EntityPlayer player;
	public GUISlot[] playerSlots;
	
	@Override
	public boolean shouldRenderMouseItem(){
		return true;
	}
	
	public GUIInventoryScreen(EntityPlayer p){
		this.player = p;
		this.playerSlots = new GUISlot[p.getInventorySize()-9];
		for(int i = 0; i < playerSlots.length; i++){
			int row = i/9;
			int col = i%9;
			
			this.playerSlots[i] = new GUISlot((int)(col*GUISlot.size+GUISlot.size/2+(Constants.WIDTH/2-4.5f*GUISlot.size)), Constants.HEIGHT - (int) (row*GUISlot.size+GUISlot.size*3), p);
		}
		addElements(this.playerSlots);
	}
	
	@Override
	public void input(int xof, int yof){
		for(int i = 0; i < this.playerSlots.length; i++)
			this.playerSlots[i].itemstack = this.player.getInventory(i+9);
		super.input(xof, yof);
		for(int i = 0; i < this.playerSlots.length; i++)
			this.player.setInventory(i+9, this.playerSlots[i].itemstack);
	}
	
	@Override
	public void onClose(){
		super.onClose();
		EntityPlayer p = GLCraft.getGLCraft().getEntityManager().getPlayer();
		if(!p.mouseItem.isNull()){
			p.dropItem(p.mouseItem);
		}
		p.mouseItem = new ItemStack();
	}
}
