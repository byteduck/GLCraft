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
		addRecipe(new Recipe(new ItemStack(Tile.Log),new ItemStack(),new ItemStack(),new ItemStack(),new ItemStack(Tile.Wood,4)));
		addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick,4)));
		addRecipe(new Recipe(new ItemStack(Item.Stick),new ItemStack(Tile.Wood),new ItemStack(Item.Stick),new ItemStack(Tile.Wood),new ItemStack(Item.WoodPick,1)));
	}
	
}
