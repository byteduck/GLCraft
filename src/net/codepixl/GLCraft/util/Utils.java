package net.codepixl.GLCraft.util;

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
}
