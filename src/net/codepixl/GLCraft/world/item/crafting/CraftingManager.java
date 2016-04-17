package net.codepixl.GLCraft.world.item.crafting;

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

	public static void initRecipes() {
		/**Wood**/
		Recipe woodRecipe = new Recipe(new ItemStack(Tile.Log),new ItemStack(),new ItemStack(),new ItemStack(),new ItemStack(Tile.Wood,4));
		woodRecipe.setPreserveMetaFromSlot(0);
		addRecipe(woodRecipe);
		/**Workbench**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Workbench,1)));
		/**Sticks**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick,4)));
		/**Wood Pick**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.WoodPick,1)));
		/**Stone Pick**/addRecipe(new Recipe(new ItemStack(Tile.Stone),new ItemStack(Tile.Stone),new ItemStack(Tile.Stone),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(),new ItemStack(Item.Stick),new ItemStack(),new ItemStack(Item.StonePick,1)));
		/**Chest**/addRecipe(new Recipe(new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Wood),new ItemStack(Tile.Chest,1)));
		/**Bucket**/addRecipe(new Recipe(new ItemStack(),new ItemStack(),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Tile.Wood),new ItemStack(),new ItemStack(Item.Bucket,1)));
	}
	
}
