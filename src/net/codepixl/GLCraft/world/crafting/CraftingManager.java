package net.codepixl.GLCraft.world.crafting;

import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.world.crafting.Recipe.InvalidRecipeException;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class CraftingManager {
	private static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	private static ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<FurnaceRecipe>();
	private static ArrayList<FurnaceFuel> furnaceFuels = new ArrayList<FurnaceFuel>();
	
	public static void addRecipe(Recipe r){
		recipes.add(r);
	}
	
	public static void addRecipe(FurnaceRecipe r){
		furnaceRecipes.add(r);
	}
	
	public static void addFuel(FurnaceFuel f){
		furnaceFuels.add(f);
	}
	
	public static ItemStack checkRecipe(Recipe r){
		Iterator<Recipe> it = recipes.iterator();
		ItemStack result = new ItemStack();
		while(it.hasNext()){
			Recipe rec = it.next();
			if(rec.checkRecipe(r)){
				result = rec.result;
				if(rec.preserveMeta){
					if(r.retMeta == -1)
						result.setMeta(r.items.get(rec.preserveMetaFrom).getMeta());
					else
						result.setMeta(r.retMeta);
				}
			}
		}
		return result;
	}
	
	/**
	 *  Furnace Recipe
	**/
	public static FurnaceRecipe checkRecipe(ItemStack i){
		for(FurnaceRecipe r : furnaceRecipes){
			if(r.checkRecipe(i))
				return r;
		}
		
		return null;
	}
	
	/**
	 *  Furnace Fuel
	**/
	public static FurnaceFuel checkFuel(ItemStack i){
		for(FurnaceFuel f : furnaceFuels){
			if(f.check(i))
				return f;
		}
		
		return null;
	}

	public static void initRecipes() throws InvalidRecipeException {
		/**Wood**/
		Recipe woodRecipe = new Recipe(new ItemStack(Tile.Wood, 4), new ItemStack(Tile.Log));
		woodRecipe.setPreserveMetaFromSlot(0);
		addRecipe(woodRecipe);
		/**Workbench**/
		addRecipe(new Recipe(new ItemStack(Tile.Workbench),"ww","ww",'w',new ItemStack(Tile.Wood)));
		/**Sticks**/
		addRecipe(new Recipe(new ItemStack(Item.Stick,4),"w ","w ", 'w', new ItemStack(Tile.Wood)));
		addRecipe(new Recipe(new ItemStack(Item.Stick,4)," w"," w", 'w', new ItemStack(Tile.Wood)));
		/**Wood Pick**/
		addRecipe(new Recipe(new ItemStack(Item.WoodPick), "www", " s ", " s ", 'w', new ItemStack(Tile.Wood), 's', new ItemStack(Item.Stick)));
		/**Stone Pick**/
		addRecipe(new Recipe(new ItemStack(Item.StonePick), "ccc", " s ", " s ", 'c', new ItemStack(Tile.Cobblestone), 's', new ItemStack(Item.Stick)));
		/**Iron Pick**/
		addRecipe(new Recipe(new ItemStack(Item.IronPick), "iii", " s ", " s ", 'i', new ItemStack(Item.IronIngot), 's', new ItemStack(Item.Stick)));
		/**Gold Pick**/
		addRecipe(new Recipe(new ItemStack(Item.GoldPick), "ggg", " s ", " s ", 'g', new ItemStack(Item.GoldIngot), 's', new ItemStack(Item.Stick)));
		/**Diamond Pick**/
		addRecipe(new Recipe(new ItemStack(Item.DiamondPick), "ddd", " s ", " s ", 'd', new ItemStack(Item.Diamond), 's', new ItemStack(Item.Stick)));
		/**Chest**/
		addRecipe(new Recipe(new ItemStack(Tile.Chest),"www","w w","www",'w',new ItemStack(Tile.Wood)));
		/**Bucket**/
		addRecipe(new Recipe(new ItemStack(Item.Bucket),"w w"," w ","   ",'w',new ItemStack(Tile.Wood)));
		addRecipe(new Recipe(new ItemStack(Item.Bucket),"   ","w w"," w ",'w',new ItemStack(Tile.Wood)));
		/**Furnace**/
		addRecipe(new Recipe(new ItemStack(Tile.Furnace), "sss","s s","sss",'s',new ItemStack(Tile.Cobblestone)));
		/**Condensed Bluestone**/
		addRecipe(new Recipe(new ItemStack(Tile.CondensedBluestone),"bbb","bbb","bbb",'b',new ItemStack(Tile.Bluestone)));
		/**Wood Axe**/
		addRecipe(new Recipe(new ItemStack(Item.WoodAxe), "ww ", "ws "," s ",'w',new ItemStack(Tile.Wood),'s',new ItemStack(Item.Stick)));
		/**Stone Axe**/
		addRecipe(new Recipe(new ItemStack(Item.StoneAxe), "cc ", "cs "," s ",'c',new ItemStack(Tile.Cobblestone),'s',new ItemStack(Item.Stick)));
		/**Iron Axe**/
		addRecipe(new Recipe(new ItemStack(Item.IronAxe), "ii ", "is "," s ",'i',new ItemStack(Item.IronIngot),'s',new ItemStack(Item.Stick)));
		/**Gold Axe**/
		addRecipe(new Recipe(new ItemStack(Item.GoldAxe), "gg ", "gs "," s ",'g',new ItemStack(Item.GoldIngot),'s',new ItemStack(Item.Stick)));
		/**Diamond Axe**/
		addRecipe(new Recipe(new ItemStack(Item.DiamondAxe), "dd ", "ds "," s ",'d',new ItemStack(Item.Diamond),'s',new ItemStack(Item.Stick)));
		/**Door**/
		addRecipe(new Recipe(new ItemStack(Tile.Door), "ww ","ww ","ww ",'w',new ItemStack(Tile.Wood)));
		addRecipe(new Recipe(new ItemStack(Tile.Door), " ww"," ww"," ww",'w',new ItemStack(Tile.Wood)));
		
		/* FURNACE RECIPES */
		
		/**Stone**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.Cobblestone), new ItemStack(Tile.Stone)));
		/**Glass**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.Sand), new ItemStack(Tile.Glass), 7f));
		/**Iron Ingot**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.IronOre), new ItemStack(Item.IronIngot), 11f));
		/**Gold Ingot**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.GoldOre), new ItemStack(Item.GoldIngot), 11f));
		
		/* FURNACE FUEL */
		/**Coal**/addFuel(new FurnaceFuel(new ItemStack(Item.Coal), 80f));
		/**Log**/addFuel(new FurnaceFuel(new ItemStack(Tile.Log), 15f));
		/**Wood**/addFuel(new FurnaceFuel(new ItemStack(Tile.Wood), 15f));
		/**Stick**/addFuel(new FurnaceFuel(new ItemStack(Item.Stick), 5f));
	}
	
}
