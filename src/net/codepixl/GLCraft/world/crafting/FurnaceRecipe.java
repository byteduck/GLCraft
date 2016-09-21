package net.codepixl.GLCraft.world.crafting;

import net.codepixl.GLCraft.world.item.ItemStack;

public class FurnaceRecipe {
	private ItemStack in, out;
	private float cookTime;
	
	public FurnaceRecipe(ItemStack in, ItemStack out, float cookTime){
		this.in = in;
		this.out = out;
		this.cookTime = cookTime;
	}
	
	public FurnaceRecipe(ItemStack in, ItemStack out){
		this.in = in;
		this.out = out;
		this.cookTime = 1f;
	}
	
	public boolean checkRecipe(ItemStack in){
		return this.in.compatible(in);
	}
	
	public float getCookTime(){
		return cookTime;
	}
	
	public ItemStack getOut(){
		return out;
	}
}
