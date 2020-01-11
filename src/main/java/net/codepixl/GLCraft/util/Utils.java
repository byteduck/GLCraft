package net.codepixl.GLCraft.util;

import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

import java.util.Arrays;

public class Utils {
	public static Object[] rotateArray(Object[] array, int amt) {
		Object[] arr = Arrays.copyOf(array, array.length);
		if (arr == null || amt < 0) {
		    throw new IllegalArgumentException("Illegal argument!");
		}
	 
		for (int i = 0; i < amt; i++) {
			for (int j = arr.length - 1; j > 0; j--) {
				Object temp = arr[j];
				arr[j] = arr[j - 1];
				arr[j - 1] = temp;
			}
		}
		return arr;
	}
	public static float[] rotateArray(float[] array, int amt) {
		float[] arr = Arrays.copyOf(array, array.length);
		if (arr == null || amt < 0) {
		    throw new IllegalArgumentException("Illegal argument!");
		}
	 
		for (int i = 0; i < amt; i++) {
			for (int j = arr.length - 1; j > 0; j--) {
				float temp = arr[j];
				arr[j] = arr[j - 1];
				arr[j - 1] = temp;
			}
		}
		return arr;
	}
	public static ItemStack getItemOrTile(String name){
		if(name.equalsIgnoreCase("void") || name.equalsIgnoreCase("air"))
			return new ItemStack();
		for(Tile t : Tile.tileMap.values())
			if(t.getTextureName().equalsIgnoreCase(name))
				return new ItemStack(t);
		for(Item i : Item.itemMap.values())
			if(i.getTextureName().equalsIgnoreCase(name))
				return new ItemStack(i);
		return new ItemStack();
	}
}
