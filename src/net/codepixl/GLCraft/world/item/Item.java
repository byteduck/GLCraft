package net.codepixl.GLCraft.world.item;

import java.util.HashMap;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.tool.ItemDiamondAxe;
import net.codepixl.GLCraft.world.item.tool.ItemDiamondPickaxe;
import net.codepixl.GLCraft.world.item.tool.ItemGoldAxe;
import net.codepixl.GLCraft.world.item.tool.ItemGoldPickaxe;
import net.codepixl.GLCraft.world.item.tool.ItemIronAxe;
import net.codepixl.GLCraft.world.item.tool.ItemIronPickaxe;
import net.codepixl.GLCraft.world.item.tool.ItemStoneAxe;
import net.codepixl.GLCraft.world.item.tool.ItemStonePickaxe;
import net.codepixl.GLCraft.world.item.tool.ItemWoodAxe;
import net.codepixl.GLCraft.world.item.tool.ItemWoodPickaxe;

public class Item {
	
	public static HashMap<Byte, Item> itemMap = new HashMap<Byte, Item>();
	public static Item Seeds = new ItemSeeds();
	public static Item Stick = new ItemStick();
	public static Item WoodPick = new ItemWoodPickaxe();
	public static Item StonePick = new ItemStonePickaxe();
	public static Item Bucket = new ItemBucketEmpty();
	public static Item BucketWater = new ItemBucketWater();
	public static Item IronPick = new ItemIronPickaxe();
	public static Item Diamond = new ItemDiamond();
	public static Item IronIngot = new ItemIronIngot();
	public static Item Coal = new ItemCoal();
	public static Item DiamondPick = new ItemDiamondPickaxe();
	public static Item GoldPick = new ItemGoldPickaxe();
	public static Item GoldIngot = new ItemGoldIngot();
	public static Item WoodAxe = new ItemWoodAxe();
	public static Item StoneAxe = new ItemStoneAxe();
	public static Item IronAxe = new ItemIronAxe();
	public static Item GoldAxe = new ItemGoldAxe();
	public static Item DiamondAxe = new ItemDiamondAxe();
	
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
		GLogger.log("Registering Item "+getName()+" ("+getId()+")", LogSource.GLCRAFT);
		Item.itemMap.put(getId(), this);
		TextureManager.addTexture("items."+this.getTextureName(), TextureManager.ITEMS+this.getTextureName()+".png");
	}
	
	public String getTextureName() {
		// TODO Auto-generated method stub
		return this.getName().toLowerCase().replace(' ', '_');
	}
	
	public void onClick(EntityPlayer p){
		
	}

	public Item(){
		if(this.getClass() != Item.class){
			registerItem();
		}
	}
	
	public int maxStackSize(){
		return 64;
	}

}
