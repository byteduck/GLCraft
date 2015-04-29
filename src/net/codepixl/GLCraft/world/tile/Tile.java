package net.codepixl.GLCraft.world.tile;

import java.util.HashMap;

import com.nishu.utils.Color4f;

public abstract class Tile {
	
	public static HashMap<Byte, Tile> tileMap = new HashMap<Byte, Tile>();
	
	//TILES
	public static Tile Void = new TileVoid();
	public static Tile Air = new TileAir();
	public static Tile Grass = new TileGrass(); 
	public static Tile Stone = new TileStone();
	public static Tile Water = new TileWater();
	public static Tile Glass = new TileGlass();
	public static Tile CoalOre = new TileCoal();
	public static Tile IronOre = new TileIron();
	public static Tile GoldOre = new TileGold();
	public static Tile Log = new TileLog();
	public static Tile Leaf = new TileLeaf();
	public static Tile TallGrass = new TileTallGrass();
	public static Tile Light = new TileLight();
	//TILES
	
	public abstract String getName();
	public abstract byte getId();
	public abstract Color4f getColor();
	public abstract float[] getTexCoords();
	public abstract boolean isTransparent();
	
	public static Tile getTile(byte id){
		return tileMap.get(id);
	}
	
	
	public void registerTile() {
		// TODO Auto-generated method stub
		System.out.println("Registering Tile "+getName()+" ("+getId()+")");
		Tile.tileMap.put(getId(), this);
	}
	
	public Tile(){
		if(this.getClass() != Tile.class){
			registerTile();
		}
	}
}
