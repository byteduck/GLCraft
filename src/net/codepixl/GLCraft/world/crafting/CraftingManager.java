package net.codepixl.GLCraft.world.crafting;

import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class CraftingManager {
	private static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	private static ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<FurnaceRecipe>();
	
	public static void addRecipe(Recipe r){
		recipes.add(r);
	}
	
	public static void addRecipe(FurnaceRecipe r){
		furnaceRecipes.add(r);
	}
	
	public static ItemStack checkRecipe(Recipe r){
		Iterator<Recipe> it = recipes.iterator();
		ItemStack result = new ItemStack();
		while(it.hasNext()){
			Recipe rec = it.next();
			if(rec.checkRecipe(r)){
				result = rec.result;
				if(rec.preserveMeta){
					switch(rec.preserveMetaFrom){
						case 0:
							result.setMeta(r.a.getMeta());
							break;
						case 1:
							result.setMeta(r.b.getMeta());
							break;
						case 2:
							result.setMeta(r.c.getMeta());
							break;
						case 3:
							result.setMeta(r.d.getMeta());
							break;
						case 4:
							result.setMeta(r.e.getMeta());
							break;
						case 5:
							result.setMeta(r.f.getMeta());
							break;
						case 6:
							result.setMeta(r.g.getMeta());
							break;
						case 7:
							result.setMeta(r.h.getMeta());
							break;
						case 8:
							result.setMeta(r.i.getMeta());
							break;
						default:
							result.setMeta(r.a.getMeta());
							break;
					}
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

	public static void initRecipes() {
		/**Wood**/
		Recipe woodRecipe = new Recipe(new ItemStack(Tile.Log),new ItemStack(),new ItemStack(),new ItemStack(),new ItemStack(Tile.Wood,4));
		woodRecipe.setPreserveMetaFromSlot(0);
		addRecipe(woodRecipe);
		/**Workbench**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Workbench,1)));
		/**Sticks**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick,4)));
		/**Wood Pick**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.WoodPick,1)));
		/**Stone Pick**/addRecipe(new Recipe(new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.StonePick,1)));
		/**Iron Pick**/addRecipe(new Recipe(new ItemStack(Item.IronIngot),new ItemStack(Item.IronIngot),new ItemStack(Item.IronIngot),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.IronPick,1)));
		/**Chest**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Chest,1)));
		/**Bucket**/addRecipe(new Recipe(new ItemStack(),new ItemStack(),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Bucket,1)));
		/**Furnace**/addRecipe(new Recipe(new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Cobblestone),new ItemStack(Tile.Furnace,1)));
		
		/* FURNACE RECIPES */
		
		/**Stone**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.Cobblestone), new ItemStack(Tile.Stone)));
		/**Glass**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.Sand), new ItemStack(Tile.Glass), 1.5f));
		/**Iron Ingot**/addRecipe(new FurnaceRecipe(new ItemStack(Tile.IronOre), new ItemStack(Item.IronIngot), 1.5f));
	}
	
}
