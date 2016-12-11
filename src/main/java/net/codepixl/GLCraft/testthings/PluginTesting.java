package net.codepixl.GLCraft.testthings;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.plugin.Plugin;
import net.codepixl.GLCraft.plugin.PluginManager;
import net.codepixl.GLCraft.world.crafting.CraftingManager;
import net.codepixl.GLCraft.world.crafting.Recipe;
import net.codepixl.GLCraft.world.crafting.Recipe.InvalidRecipeException;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.PluginTile;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.LWJGLException;

public class PluginTesting implements Plugin{
	public static void main(String[] args) throws LWJGLException{
		GLCraft.devEnvironment(new PluginTesting(), false);
	}

	@Override
	public void init() throws InvalidRecipeException {
		PluginManager p = GLCraft.getGLCraft().getPluginManager();
		PluginTile t = new TestTile(this);
		p.addTile(t);
		CraftingManager.addRecipe(new Recipe(new ItemStack(t,64), "dd","dd", 'd', new ItemStack(Tile.Dirt)));
	}

	@Override
	public void update() {
		
	}
}
