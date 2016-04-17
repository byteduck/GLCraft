package net.codepixl.GLCraft.world.item;

import net.codepixl.GLCraft.world.tile.Tile;

public class ItemStack{
	private Tile tile;
	private Item item;
	public int count;
	private boolean isTile;
	private boolean isNull;
	private byte meta;
	public ItemStack(Tile t){
		count = 1;
		isTile = true;
		tile = t;
		meta = 0;
	}
	public ItemStack(Tile t, byte meta){
		count = 1;
		isTile = true;
		tile = t;
		this.meta = meta;
	}
	public ItemStack(){
		isNull = true;
		isTile = false;
	}
	public ItemStack(Item i){
		count = 1;
		isTile = false;
		item = i;
		meta = 0;
	}
	public ItemStack(ItemStack s){
		this.tile = s.tile;
		this.item = s.item;
		this.count = s.count;
		this.isTile = s.isTile;
		this.isNull = s.isNull;
		this.meta = s.meta;
	}
	public ItemStack(ItemStack s, int count){
		this.tile = s.tile;
		this.item = s.item;
		this.count = count;
		this.isTile = s.isTile;
		this.meta = s.meta;
	}
	public ItemStack(Tile t, int count){
		this.count = count;
		isTile = true;
		tile = t;
		meta = 0;
	}
	public ItemStack(Tile t, int count, byte meta){
		this.count = count;
		isTile = true;
		tile = t;
		this.meta = meta;
	}
	public ItemStack(Item i, int count){
		this.count = count;
		isTile = false;
		item = i;
		meta = 0;
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
		if(this.isTile && itemstack.isTile() && this.tile == itemstack.tile && this.meta == itemstack.meta) return true;
		if(this.isItem() && itemstack.isItem() && this.item == itemstack.item && this.meta == itemstack.meta) return true;
		return false;
	}
	public boolean isNull() {
		return isNull;
	}
	public byte getMeta() {
		return meta;
	}
	public void setMeta(byte meta){
		this.meta = meta;
	}
}
