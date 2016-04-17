package net.codepixl.GLCraft.item.crafting;

import net.codepixl.GLCraft.world.item.ItemStack;

public class Recipe {
	ItemStack a,b,c,d,e,f,g,h,i,result;
	boolean advanced,preserveMeta;
	int preserveMetaFrom;
	
	/** A crafting recipe formatted like this: 
	 * |A|B|
	 * |C|D|
	 * OR this
	 * |A|B|C|
	 * |D|E|F|
	 * |G|H|I|
	 * */
	public Recipe(ItemStack a, ItemStack b, ItemStack c, ItemStack d, ItemStack result){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = new ItemStack();
		this.f = new ItemStack();
		this.g = new ItemStack();
		this.h = new ItemStack();
		this.i = new ItemStack();
		this.result = result;
		advanced = false;
		preserveMeta = false;
	}
	
	public Recipe(ItemStack a, ItemStack b, ItemStack c, ItemStack d, ItemStack e, ItemStack f, ItemStack g, ItemStack h, ItemStack i, ItemStack result){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
		this.h = h;
		this.i = i;
		this.result = result;
		advanced = true;
		preserveMeta = false;
	}
	
	//Preserves the sets the metadata of the item in the slot given to the result, eg wood
	public void setPreserveMetaFromSlot(int slot){
		preserveMeta = true;
		preserveMetaFrom = slot;
	}
	
	public boolean checkRecipe(Recipe r){
		boolean flag = true;
		if(r.a.isItem() && this.a.isItem()){ if(r.a.getItem() != this.a.getItem()) flag = false; }else{ if(r.a.getTile() != this.a.getTile()) flag = false; }
		if(r.b.isItem() && this.b.isItem()){ if(r.b.getItem() != this.b.getItem()) flag = false; }else{ if(r.b.getTile() != this.b.getTile()) flag = false; }
		if(r.c.isItem() && this.c.isItem()){ if(r.c.getItem() != this.c.getItem()) flag = false; }else{ if(r.c.getTile() != this.c.getTile()) flag = false; }
		if(r.d.isItem() && this.d.isItem()){ if(r.d.getItem() != this.d.getItem()) flag = false; }else{ if(r.d.getTile() != this.d.getTile()) flag = false; }
		if(r.e.isItem() && this.e.isItem()){ if(r.e.getItem() != this.e.getItem()) flag = false; }else{ if(r.e.getTile() != this.e.getTile()) flag = false; }
		if(r.f.isItem() && this.f.isItem()){ if(r.f.getItem() != this.f.getItem()) flag = false; }else{ if(r.f.getTile() != this.f.getTile()) flag = false; }
		if(r.g.isItem() && this.g.isItem()){ if(r.g.getItem() != this.g.getItem()) flag = false; }else{ if(r.g.getTile() != this.g.getTile()) flag = false; }
		if(r.h.isItem() && this.h.isItem()){ if(r.h.getItem() != this.h.getItem()) flag = false; }else{ if(r.h.getTile() != this.h.getTile()) flag = false; }
		if(r.i.isItem() && this.i.isItem()){ if(r.i.getItem() != this.i.getItem()) flag = false; }else{ if(r.i.getTile() != this.i.getTile()) flag = false; }
		return flag;
	}
	
}
