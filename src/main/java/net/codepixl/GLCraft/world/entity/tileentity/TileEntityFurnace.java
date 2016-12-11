package net.codepixl.GLCraft.world.entity.tileentity;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagFloat;
import com.nishu.utils.Time;
import net.codepixl.GLCraft.GUI.tileentity.GUIFurnace;
import net.codepixl.GLCraft.network.packet.PacketFurnace;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.FurnaceFuel;
import net.codepixl.GLCraft.world.crafting.FurnaceRecipe;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;

public class TileEntityFurnace extends TileEntityContainer{
	
	private float progress = 0, fuelTime = 0;
	private boolean cooking = false;
	private FurnaceRecipe currentRecipe;
	private FurnaceFuel currentFuel;
	private int progressPercent = 0, fuelPercent = 0;

	public TileEntityFurnace(int x, int y, int z, WorldManager worldManager) {
		super(x, y, z, 3, worldManager);
	}
	
	@Override
	public void update(){
		super.update();
		float bProgress = progress, bFuelTime = fuelTime;
		boolean bCooking = cooking;
		if(currentRecipe != null && progress >= currentRecipe.getCookTime()){
			cooking = false;
			progress = 0;
			if(getInventory()[1].isNull())
				setInventory(1,new ItemStack(currentRecipe.getOut()));
			else
				getInventory()[1].addToStack(currentRecipe.getOut().count);
			if(getInventory()[0].subFromStack(1) == 1)
				setInventory(0,new ItemStack());
			this.updatedInventory = true;
		}
		
		if(currentRecipe != null && fuelTime == 0 && CraftingManager.checkFuel(getInventory()[2]) != null){
			currentFuel = CraftingManager.checkFuel(getInventory()[2]);
			fuelTime = currentFuel.cookTime;
			if(getInventory()[2].subFromStack(1) == 1)
				getInventory()[2] = new ItemStack();
			this.updatedInventory = true;
		}
		
		if(!cooking){
			currentRecipe = CraftingManager.checkRecipe(getInventory()[0]);
			if(fuelTime > 0 && currentRecipe != null && !currentRecipe.getOut().isNull() && (currentRecipe.getOut().compatible(getInventory()[1]) || getInventory()[1].isNull())){
				if(getInventory()[1].isNull() || getInventory()[1].addToStack(currentRecipe.getOut().count) == 0){
					getInventory()[1].subFromStack(currentRecipe.getOut().count);
					cooking = true;
					this.updatedInventory = true;
				}
			}
		}
		
		if(cooking && CraftingManager.checkRecipe(getInventory()[0]) != currentRecipe)
			cooking = false;
		
		if(cooking && fuelTime > 0){
			progress += Time.getDelta();
		}else if(cooking && fuelTime <= 0){
			progress -= Time.getDelta()*2;
			if(progress < 0){
				progress = 0;
				cooking = false;
			}
		}else{
			progress = 0;
		}

		fuelTime -= Time.getDelta();
		
		if(fuelTime < 0){
			fuelTime = 0;
		}
		
		if(bCooking != cooking || bFuelTime != fuelTime || bProgress != progress)
			worldManager.sendPacket(new PacketFurnace(this));
	}

	public void openGUI(WorldManager w, EntityPlayer p) {
		w.centralManager.guiManager.showGUI(new GUIFurnace(this, p));
	}
	
	public float getProgress(){
		return progress;
	}
	
	public int getProgressPercentServer(){
		if(progress == 0)
			return 0;
		else
			return (int) ((progress/currentRecipe.getCookTime())*100);
	}
	
	public int getProgressPercent(){
		return progressPercent ;
	}
	
	public int getFuelPercentServer(){
		if(fuelTime == 0)
			return 0;
		else
			return (int) ((fuelTime/currentFuel.cookTime)*100);
	}
	
	public int getFuelPercent(){
		return fuelPercent;
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException {
		TileEntityContainer c = (TileEntityContainer) TileEntityContainer.fromNBT(t, w);
		TileEntityFurnace f = new TileEntityFurnace(c.getBlockpos().x, c.getBlockpos().y, c.getBlockpos().z, w);
		f.setInventory(c.getInventory());
		if(f.getInventory().length <= 2)
			f.setInventory(new ItemStack[]{new ItemStack(f.getInventory()[0]), f.getInventory()[1], new ItemStack()}); //Compatibility with loading older furnaces without fuel slot
		try{
			f.currentFuel = CraftingManager.checkFuel(ItemStack.fromNBT(t.getCompound("currentFuel")));
			f.currentRecipe = CraftingManager.checkRecipe(ItemStack.fromNBT(t.getCompound("currentRecipe")));
			f.progress = t.getFloat("progress");
			f.fuelTime = t.getFloat("fuelTime");
			f.cooking = t.getByte("cooking") == 1;
		}catch(TagNotFoundException e){}
		return f;
	}
	
	public void writeToNBT(TagCompound t){
		super.writeToNBT(t);
		TagFloat progress = new TagFloat("progress", this.progress);
		TagFloat fuelTime = new TagFloat("fuelTime", this.fuelTime);
		TagCompound currentRecipe = null;
		if(this.currentRecipe != null)
			currentRecipe = this.currentRecipe.getIn().toNBT("currentRecipe");
		else
			currentRecipe = new ItemStack().toNBT("currentRecipe");
		TagCompound currentFuel = null;
		if(this.currentFuel != null)
			currentFuel = this.currentFuel.fuel.toNBT("currentFuel");
		else
			currentFuel = new ItemStack().toNBT("currentFuel");
		TagByte cooking = new TagByte("cooking", this.cooking ? (byte)1 : (byte)0);
		t.setTag(cooking);
		t.setTag(progress);
		t.setTag(fuelTime);
		t.setTag(currentRecipe);
		t.setTag(currentFuel);
	}

	public float getFuelTime() {
		return fuelTime;
	}
	
	public boolean isCooking(){
		return cooking;
	}

	public void update(PacketFurnace p) {
		this.cooking = p.cooking;
		this.fuelTime = p.fuelTime;
		this.progress = p.progress;
		this.progressPercent = p.progressPercent;
		this.fuelPercent = p.fuelPercent;
	}

}