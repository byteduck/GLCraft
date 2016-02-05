package net.codepixl.GLCraft.item.crafting;

import net.codepixl.GLCraft.world.item.ItemStack;

public class Recipe {
	ItemStack a,b,c,d,result;
	
	/** A crafting recipe formatted like this: 
	 * |A|B|
	 * |C|D|
	 * */
	public Recipe(ItemStack a, ItemStack b, ItemStack c, ItemStack d, ItemStack result){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.result = result;
	}
	
	public boolean checkRecipe(Recipe r){
		boolean flag = true;
		if(r.a.isItem() && this.a.isItem()){ if(r.a.getItem() != this.a.getItem()) flag = false; }else{ if(r.a.getTile() != this.a.getTile()) flag = false; }
		if(r.b.isItem() && this.b.isItem()){ if(r.b.getItem() != this.b.getItem()) flag = false; }else{ if(r.b.getTile() != this.b.getTile()) flag = false; }
		if(r.c.isItem() && this.c.isItem()){ if(r.c.getItem() != this.c.getItem()) flag = false; }else{ if(r.c.getTile() != this.c.getTile()) flag = false; }
		if(r.d.isItem() && this.d.isItem()){ if(r.d.getItem() != this.d.getItem()) flag = false; }else{ if(r.d.getTile() != this.d.getTile()) flag = false; }
		return flag;
	}
	
}
