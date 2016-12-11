package net.codepixl.GLCraft.world.crafting;

import net.codepixl.GLCraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe {
	ItemStack result;
	ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	Type type;
	boolean preserveMeta;
	int preserveMetaFrom;
	byte retMeta = -1;
	
	/**
	 * The parameters Object... goes as follows:<br>
	 * 2x2 recipe: Two strings representing the recipe, followed by characters and ItemStacks that tell what the characters represent.<br>
	 * 3x3 recipe: The same thing, except with three strings.<br>
	 * Shapeless recipe: Just up to nine ItemStacks.<br>
	 * <br>
	 * For example:<br>
	 * new Recipe(new ItemStack(Item.Seeds),<br>
	 * "aab",<br>
	 * "b a",<br>
	 * "ccc",<br>
	 * 'a', new ItemStack(Tile.Wood),<br>
	 * 'b', new ItemStack(Item.Stick),<br>
	 * 'c', new ItemStack(Tile.Grass)<br>
	 * );<br>
	 * Would be a recipe for Seeds like this:<br>
	 * Wood,  Wood, Stick<br>
	 * Stick, Nothing, Wood<br>
	 * Grass, Grass, Grass<br>
	 * And<br>
	 * new Recipe(new ItemStack(Tile.Wood), new ItemStack(Tile.Grass), new ItemStack(Item.Seeds));<br>
	 * Would be a shapeless recipe for wood which requires Grass and Seeds.
	 * @param result - ItemStack that is the result of this recipe.
	 * @throws InvalidRecipeException 
	 */
	public Recipe(ItemStack result, Object...parameters) throws InvalidRecipeException{
		this.result = result;
		try{
			if(parameters[0] instanceof String){ //Not shapeless
				if(((String)parameters[0]).length() == 3){ //3x3 recipe
					this.type = Type.X3;
					HashMap<Character, ItemStack> lookup = new HashMap<Character, ItemStack>();
					for(int i = 3; i < parameters.length; i+=2){
						lookup.put((Character)parameters[i], (ItemStack)parameters[i+1]);
					}
					for(int i = 0; i < 3; i++){
						String str = (String)parameters[i];
						for(char c : str.toCharArray()){
							if(lookup.containsKey(c)){
								items.add(lookup.get(c));
							}else{
								items.add(new ItemStack());
							}
						}
					}
				}else if(((String)parameters[0]).length() == 2){ //2x2 recipe
					this.type = Type.X2;
					HashMap<Character, ItemStack> lookup = new HashMap<Character, ItemStack>();
					for(int i = 2; i < parameters.length; i+=2){
						lookup.put((Character)parameters[i], (ItemStack)parameters[i+1]);
					}
					for(int i = 0; i < 2; i++){
						String str = (String)parameters[i];
						for(char c : str.toCharArray()){
							if(lookup.containsKey(c)){
								items.add(lookup.get(c));
							}else{
								items.add(new ItemStack());
							}
						}
					}
				}else{
					throw new InvalidRecipeException("Invalid recipe size");
				}
			}else{ //Shapeless
				this.type = Type.SHAPELESS;
				for(Object o : parameters){
					ItemStack s = (ItemStack)o;
					items.add(s);
				}
			}
		}catch(ClassCastException e){
			throw new InvalidRecipeException(e);
		}catch(ArrayIndexOutOfBoundsException e){
			throw new InvalidRecipeException("Not enough arguments");
		}
	}
	
	/**
	 * Preserves the sets the metadata of the item in the slot given to the result, eg wood
	 * @param slot the slot to preserve metadata from
	 */
	public void setPreserveMetaFromSlot(int slot){
		preserveMeta = true;
		preserveMetaFrom = slot;
	}
	
	public boolean checkRecipe(Recipe r){
		boolean flag = r.type == type;
		byte meta = 0;
		if(flag){
			for(int i = 0; i < items.size(); i++){
				if(flag){
					if(r.items.get(i).isItem() && this.items.get(i).isItem())
						flag = r.items.get(i).getItem() == this.items.get(i).getItem();
					else if(r.items.get(i).isTile() && this.items.get(i).isTile())
						flag = r.items.get(i).getTile() == this.items.get(i).getTile();
					else if(r.items.get(i).isNull() && this.items.get(i).isNull())
						flag = true;
					else
						flag = false;
				}
			}
		}else if(type == Type.SHAPELESS){
			ArrayList<Boolean> hasMatch = new ArrayList<Boolean>();
			for(int i = 0; i < items.size(); i++)
				hasMatch.add(false);
			HashMap<ItemStack,ItemStack> matched = new HashMap<ItemStack,ItemStack>();
			boolean flag3 = false;
			for(ItemStack rs : r.items){
				for(int i = 0; i < items.size(); i++){
					ItemStack s = items.get(i);
					boolean match = false;
					if(!hasMatch.get(i)){
						if(rs.isItem() && s.isItem())
							match = rs.getItem() == s.getItem();
						else if(rs.isTile() && s.isTile())
							match = rs.getTile() == s.getTile();
						if(match && this.preserveMeta && i == this.preserveMetaFrom){
							meta = rs.getMeta();
						}
					}
					if(!matched.containsKey(s)){
						hasMatch.set(i, match);
						if(match){
							matched.put(s,rs);
						}
					}
				}
			}
			for(ItemStack rs : r.items){
				for(int i = 0; i < items.size(); i++){
					ItemStack s = items.get(i);
					if(matched.containsKey(s) && !rs.isNull() && matched.get(s) != rs)
						flag3 = true;
				}
			}
			if(!flag3){
				flag = true;
				for(boolean b : hasMatch){
					if(!b) flag = false;
				}
			}else{
				flag = false;
			}
			if(this.preserveMeta){
				r.retMeta = meta;
			}
		}
		return flag;
	}
	
	public enum Type{
		SHAPELESS, X2, X3;
	}
	
	public class InvalidRecipeException extends Exception{
		private static final long serialVersionUID = 3791749186041226846L;
		public InvalidRecipeException(ClassCastException e){
			super("Error while initializing recipe: ClassCastException:\n"+e.getMessage());
		}
		public InvalidRecipeException(String message) {
			super("Error while initializing recipe: "+message);
		}
	}
	
}
