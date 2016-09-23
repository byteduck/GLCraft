package net.codepixl.GLCraft.plugin;

import net.codepixl.GLCraft.world.crafting.Recipe;

public interface Plugin {
	public void init() throws Recipe.InvalidRecipeException;
	public void update();
}
