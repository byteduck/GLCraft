package net.codepixl.GLCraft.item;

import net.codepixl.GLCraft.world.tile.Tile;

public class ItemStack{
	private Tile tile;
	private Item item;
	public int count;
	private boolean isTile;
	public ItemStack(Tile t){
		count = 1;
		isTile = true;
		tile = t;
	}
	public ItemStack(Item i){
		count = 1;
		isTile = false;
		item = i;
	}
	public ItemStack(ItemStack s){
		this.tile = s.tile;
		this.item = s.item;
		this.count = s.count;
		this.isTile = s.isTile;
	}
	public ItemStack(ItemStack s, int count){
		this.tile = s.tile;
		this.item = s.item;
		this.count = count;
		this.isTile = s.isTile;
	}
	public ItemStack(Tile t, int count){
		this.count = count;
		isTile = true;
		tile = t;
	}
	public ItemStack(Item i, int count){
		this.count = count;
		isTile = false;
		item = i;
	}
	public boolean isTile(){
		return isTile;
	}
	public boolean isItem(){
		return !isTile;
	}
	public Tile getTile(){
		if(tile == null){
			return Tile.Void;
		}
		return tile;
	}
	public Item getItem(){
		if(item == null){
			return null;
		}
		return item;
	}
	public byte getId(){
		if(this.isItem()){
			return this.item.getId();
		}else{
			return this.tile.getId();
		}
	}
	public int addToStack(int count){
		int ret = count;
		for(int i = count; i > 0; i--){
			if(this.count >= 64){
				return ret;
			}
			this.count+=1;
			ret-=1;
		}
		return ret;
	}
	
	public int subFromStack(int count){
		int ret = count;
		for(int i = 0; i < count; i++){
			if(this.count <= 1){
				return ret;
			}
			this.count-=1;
			ret-=1;
		}
		return 0;
	}
	public boolean compatible(ItemStack itemstack) {
		if(this.isTile && itemstack.isTile() && this.tile == itemstack.tile) return true;
		if(this.isItem() && itemstack.isItem() && this.item == itemstack.item) return true;
		return false;
	}
}
