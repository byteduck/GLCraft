package net.codepixl.GLCraft.item.crafting;

import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class CraftingManager {
	private static ArrayList<Recipe> recipies = new ArrayList<Recipe>();
	
	public static void addRecipe(Recipe r){
		recipies.add(r);
	}
	
	public static ItemStack checkRecipe(Recipe r){
		Iterator<Recipe> it = recipies.iterator();
		ItemStack result = null;
		while(it.hasNext()){
			Recipe rec = it.next();
			if(rec.checkRecipe(r))
				result = rec.result;
		}
		return result;
	}

	public static void initRecipes() {
		addRecipe(new Recipe(new ItemStack(Tile.Grass),new ItemStack(Tile.Log),new ItemStack(Tile.Log),new ItemStack(Tile.Grass),new ItemStack(Tile.Fire)));
	}
	
}
