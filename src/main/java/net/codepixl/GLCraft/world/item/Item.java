package net.codepixl.GLCraft.world.item;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.tool.*;

import java.util.HashMap;

public class Item {
	private static byte currentID = (byte)0x01; //Because idiot me started them at one when I made the game and now I have to stick with it
	public static HashMap<Byte, Item> itemMap = new HashMap<Byte, Item>();
	public static Item Seeds = new ItemSeeds();
	public static Item WoodPick = new ItemWoodPickaxe();
	public static Item StonePick = new ItemStonePickaxe();
	public static Item Stick = new ItemStick();
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

	protected byte id;
	
	public String getName(){
		return "Un-named item";
	}
	
	public final byte getId(){
		return id;
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
	
	public void registerItem(){
		this.id = currentID;
		Item.itemMap.put(getId(), this);
		currentID++;
		TextureManager.addTexture("items."+this.getTextureName(), TextureManager.ITEMS+this.getTextureName()+".png");
		GLogger.log("Registering Item "+getName()+" ("+getId()+")", LogSource.GLCRAFT);
	}
	
	public String getTextureName() {
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
