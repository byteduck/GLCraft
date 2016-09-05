package net.codepixl.GLCraft.world.entity.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagList;

import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileEntityContainer extends TileEntity{
	
	private ItemStack[] inventory;
	
	public TileEntityContainer(int x, int y, int z, int size, WorldManager worldManager) {
		super(x, y, z, worldManager);
		inventory = new ItemStack[size];
		for(int i = 0; i < inventory.length; i++){
			inventory[i] = new ItemStack();
		}
	}
	
	public TileEntityContainer(int x, int y, int z, ItemStack[] inventory, WorldManager worldManager){
		super(x,y,z,worldManager);
		this.inventory = inventory;
	}
	
	public ItemStack getSlot(int slot){
		return inventory[slot];
	}
	
	public ItemStack[] getInventory(){
		return inventory;
	}
	
	public int addToContainer(ItemStack s){
		int c = s.count;
		ArrayList<Integer> blankSlots = new ArrayList<Integer>();
		ArrayList<Integer> compatibleSlots = new ArrayList<Integer>();
		for(int i = 0; i < inventory.length; i++){
			if(inventory[i].isNull()){
				blankSlots.add(i);
			}else if(inventory[i].compatible(s)){
				compatibleSlots.add(i);
			}
		}
		
		for(int i = 0; i < compatibleSlots.size(); i++){
			int cr = inventory[compatibleSlots.get(i)].addToStack(c);
			c=cr;
			if(cr == 0){
				return 0;
			}
		}
		
		if(blankSlots.size() > 0){
			inventory[blankSlots.get(0)] = new ItemStack(s);
			return 0;
		}
		return c;
	}
	
	public void dropAllItems(){
		for(int i = 0; i < inventory.length; i++){
			if(!inventory[i].isNull()){
				worldManager.spawnEntity(new EntityItem(inventory[i],this.pos,worldManager));
				inventory[i] = new ItemStack();
			}
		}
	}
	
	public void writeToNBT(TagCompound t){
		TagList inventory = new TagList("Inventory");
		for(int i = 0; i < getInventory().length; i++){
			ItemStack stack = getInventory()[i];
			if(!stack.isNull()){
				TagCompound slot = new TagCompound("");
				slot.setTag(new TagInteger("slot",i));
				slot.setTag(new TagByte("isItem",(byte) (stack.isItem() ? 1 : 0 )));
				slot.setTag(new TagByte("id",stack.getId()));
				slot.setTag(new TagByte("count",(byte)stack.count));
				inventory.addTag(slot);
			}
		}
		t.setTag (inventory);
		TagInteger size = new TagInteger("size",getInventory().length);
		t.setTag(size);
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException {
		Vector3f pos = NBTUtil.vecFromList("Pos",t);
		if(t.getList("Inventory", TagCompound.class) != null){
			List<TagCompound> inventory = t.getList("Inventory",TagCompound.class);
			ItemStack[] is = new ItemStack[t.getInteger("size")];
			for(int i = 0; i < t.getInteger("size"); i++)
				is[i] = new ItemStack();
			Iterator<TagCompound> i = inventory.iterator();
			while(i.hasNext()){
				TagCompound t2 = i.next();
				int slot = t2.getInteger("slot");
				ItemStack stack;
				if(t2.getByte("isItem") == 0){
					stack = new ItemStack(Tile.getTile(t2.getByte("id")));
				}else{
					stack = new ItemStack(Item.getItem(t2.getByte("id")));
				}
				stack.count = t2.getByte("count");
				is[slot] = stack;
			}
			return new TileEntityChest((int)pos.x, (int)pos.y, (int)pos.z, is, w);
		}
		
		return null;
	}

}
