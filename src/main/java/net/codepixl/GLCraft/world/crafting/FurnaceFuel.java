package net.codepixl.GLCraft.world.crafting;

import net.codepixl.GLCraft.world.item.ItemStack;

public class FurnaceFuel {
	public ItemStack fuel;
	public float cookTime;
	public FurnaceFuel(ItemStack fuel, float cookTime){
		this.fuel = fuel;
		this.cookTime = cookTime;
	}
	public boolean check(ItemStack i) {
		return fuel.compatible(i, true);
	}
}
