package net.codepixl.GLCraft.GUI.tileentity;


import net.codepixl.GLCraft.GUI.GUIScreen;
import net.codepixl.GLCraft.GUI.Elements.GUIProgressBar;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Keyboard;
import net.codepixl.GLCraft.util.Mouse;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntityFurnace;
import net.codepixl.GLCraft.world.item.ItemStack;

public class GUIFurnace extends GUIScreen{
	
	private TileEntityFurnace furnace;
	private EntityPlayer player;
	private GUISlot in,out;
	private GUIProgressBar progressBar;
	
	private static final int HMIDDLE = Constants.WIDTH/2;
	private static final int VMIDDLE = Constants.HEIGHT/2;
	private static final int HSIZE = (int) (GUISlot.size/2f);
	private static final int PBSIZE = 100;
	
	
	public GUIFurnace(TileEntityFurnace furnace, EntityPlayer player) {
		this.furnace = furnace;
		this.player = player;
		in = new GUISlot(HMIDDLE-HSIZE-PBSIZE/2-10,VMIDDLE);
		out = new GUISlot(HMIDDLE+HSIZE+PBSIZE/2+10,VMIDDLE);
		progressBar = new GUIProgressBar(HMIDDLE-PBSIZE/2, VMIDDLE-GUIProgressBar.PB_HEIGHT/2, PBSIZE);
		
		addElements(in,out, progressBar);
	}
	
	@Override
	public void update(){
		in.itemstack = furnace.getSlot(0);
		out.itemstack = furnace.getSlot(1);
		progressBar.setProgress(furnace.getProgressPercent());
	}
	
	@Override
	public void input(int xof, int yof){
		super.input(xof, yof);
		if(in.hover){
			//while(Mouse.next()){
			//	if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						ItemStack tempStack = in.itemstack;
						in.itemstack = player.getSelectedItemStack();
						player.setSelectedItemStack(tempStack);
						furnace.getInventory()[0] = in.itemstack;
					}
					if(Mouse.isButtonDown(1)){
						if(player.getSelectedItemStack().compatible(in.itemstack)){
							int c = player.getSelectedItemStack().addToStack(in.itemstack.count);
							in.itemstack.count = c;
							furnace.getInventory()[0] = in.itemstack;
						}else if(player.getSelectedItemStack().isNull()){
							ItemStack tempStack = in.itemstack;
							in.itemstack = player.getSelectedItemStack();
							player.setSelectedItemStack(tempStack);
							furnace.getInventory()[0] = in.itemstack;
						}
					}
			//	}
			//} TODO implement
			int dWheel = (int) Mouse.getDWheel();
			//if(Mouse.hasWheel()) {
				if(player.getSelectedItemStack().isNull()){
					player.setSelectedItemStack(new ItemStack(in.itemstack));
					player.getSelectedItemStack().count = 0;
				}
				if(in.itemstack.isNull()){
					in.itemstack = new ItemStack(player.getSelectedItemStack());
					in.itemstack.count = 0;
					furnace.getInventory()[0] = in.itemstack;
				}
				if((player.getSelectedItemStack().compatible(in.itemstack) || player.getSelectedItemStack().isNull()) && dWheel != 0){
					for(int i = 0; i < dWheel / 120; i++){
						if(in.itemstack.count < 64 && player.getSelectedItemStack().count > 0 && !player.getSelectedItemStack().isNull()){
							in.itemstack.count++;
							player.getSelectedItemStack().count--;
							furnace.getInventory()[0] = in.itemstack;
						}
					}
					for(int i = 0; i > dWheel / 120; i--){
						if(player.getSelectedItemStack().count < 64 && in.itemstack.count > 0 && !in.itemstack.isNull()){
							in.itemstack.count--;
							player.getSelectedItemStack().count++;
							furnace.getInventory()[0] = in.itemstack;
						}
					}
				}
				if(player.getSelectedItemStack().count == 0)
					player.setSelectedItemStack(new ItemStack());
				if(in.itemstack.count == 0){
					in.itemstack = new ItemStack();
					furnace.getInventory()[0] = in.itemstack;
				}
			//}
		}else if(out.hover && !out.itemstack.isNull()){
			//while(Mouse.next()){
			//	if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(0)){
						out.itemstack.count = player.addToInventory(out.itemstack);
						if(out.itemstack.count == 0)
							out.itemstack = new ItemStack();
						furnace.getInventory()[1] = out.itemstack;
					}
			//	} TODO implement
			//}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_1)){
			player.setSelectedSlot(0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_2)){
			player.setSelectedSlot(1);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_3)){
			player.setSelectedSlot(2);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_4)){
			player.setSelectedSlot(3);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_5)){
			player.setSelectedSlot(4);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_6)){
			player.setSelectedSlot(5);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_7)){
			player.setSelectedSlot(6);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_8)){
			player.setSelectedSlot(7);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_9)){
			player.setSelectedSlot(8);
		}
	}

}
