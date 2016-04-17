package net.codepixl.GLCraft.item.crafting;

import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class CraftingManager {
	private static ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	
	public static void addRecipe(Recipe r){
		recipes.add(r);
	}
	
	public static ItemStack checkRecipe(Recipe r){
		Iterator<Recipe> it = recipes.iterator();
		ItemStack result = new ItemStack();
		while(it.hasNext()){
			Recipe rec = it.next();
			if(rec.checkRecipe(r))
				result = rec.result;
		}
		return result;
	}

	public static void initRecipes() {
		/**Wood**/addRecipe(new Recipe(new ItemStack(Tile.Log),new ItemStack(),new ItemStack(),new ItemStack(),new ItemStack(Tile.Wood,4)));
		/**Workbench**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Workbench,1)));
		/**Sticks**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick,4)));
		/**Wood Pick**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.WoodPick,1)));
		/**Stone Pick**/addRecipe(new Recipe(new ItemStack(Tile.Stone),new ItemStack(Tile.Stone),new ItemStack(Tile.Stone),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.StonePick,1)));
	}
	
}
