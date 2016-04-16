package net.codepixl.GLCraft.world.item;

import java.util.HashMap;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.tool.ItemStonePickaxe;
import net.codepixl.GLCraft.world.item.tool.ItemWoodPickaxe;

public class Item {
	
	public static HashMap<Byte, Item> itemMap = new HashMap<Byte, Item>();
	public static Item Seeds = new ItemSeeds();
	public static Item Stick = new ItemStick();
	public static Item WoodPick = new ItemWoodPickaxe();
	public static Item StonePick = new ItemStonePickaxe();
	
	public String getName(){
		return "Un-named item";
	}
	
	public byte getId(){
		return -1;
	}
	
	public static Item getItem(byte id){
		return itemMap.get(id);
	}
	
	public Color4f getColor() {
		return new Color4f(1,1,1,1);
	}

	public float[] getTexCoords() {
		return TextureManager.item(this);
	}
	
	public void registerItem() {
		System.out.println("Registering Item "+getName()+" ("+getId()+")");
		Item.itemMap.put(getId(), this);
		TextureManager.addTexture("items."+this.getTextureName(), TextureManager.ITEMS+this.getTextureName()+".png");
	}
	
	public String getTextureName() {
		// TODO Auto-generated method stub
		return this.getName();
	}
	
	public void onClick(EntityPlayer p){
		
	}

	public Item(){
		if(this.getClass() != Item.class){
			registerItem();
		}
	}

}
