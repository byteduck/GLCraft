package net.codepixl.GLCraft.world.tile;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.item.ItemStack;

public class Tile {

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
	public static Tile Lamp = new TileLamp();
	public static Tile Fire = new TileFire();
	public static Tile Bluestone = new TileBluestone();
	public static Tile Tnt = new TileTnt();
	public static Tile Lava = new TileLava();
	public static Tile Wood = new TileWood();
	public static Tile Sapling = new TileSapling();
	//TILES
	
	public String getName(){
		return "Un-named block";
	}
	
	public byte getId(){
		return -1;
	}
	
	public boolean customHitbox(){
		return false;
	}
	
	public AABB getAABB(){
		return new AABB(1,1,1);
	}
	
	public boolean needsConstantTick(){
		return false;
	}
	
	public Color4f getColor(){
		return new Color4f(1,1,1,1);
	}
	
	public String getTextureName(){
		return this.getName();
	}
	
	public boolean isTransparent(){
		return false;
	}
	
	public boolean canPassThrough(){
		return false;
	}
	
	public boolean isTranslucent(){
		return false;
	}
	
	public void onBreak(int x, int y, int z, WorldManager worldManager){
		worldManager.spawnEntity(new EntityItem(new ItemStack(this),(float)x+0.5f,(float)y+0.5f,(float)z+0.5f,worldManager));
	}
	
	public void randomTick(int x, int y, int z, WorldManager worldManager){
		
	}
	
	/**
	 * 0: Bottom <p>
	 * 1: Top <p>
	 * 2-5: Sides 1-4 <p>
	 */
	public String[] getMultiTextureNames(){
		return new String[]{};
	}
	
	public boolean hasMultipleTextures(){
		return false;
	}
	
	public void tick(int x, int y, int z, WorldManager worldManager){}
	
	public static Tile getTile(byte id){
		return tileMap.get(id);
	}
	
	public RenderType getRenderType(){
		return RenderType.CUBE;
	}
	
	public void customRender(float x, float y, float z, WorldManager w, Chunk c){
		
	}
	
	public float getHardness(){
		return 0.4f;
	}
	
	public void registerTile() {
		// TODO Auto-generated method stub
		System.out.println("Registering Tile "+getName()+" ("+getId()+")");
		Tile.tileMap.put(getId(), this);
	}
	
	public void onPlace(int x, int y, int z, WorldManager w){
		
	}
	
	public boolean canPlace(int x, int y, int z, WorldManager w){
		return true;
	}
	
	public Tile(){
		if(this.getClass() != Tile.class){
			registerTile();
			if(this.hasTexture()){
				if(this.hasMultipleTextures()){
					for(String name : this.getMultiTextureNames()){
						if(!TextureManager.hasTexture("tiles."+name)){
							TextureManager.addTexture("tiles."+name, TextureManager.TILES+name+".png");
						}
					}
				}else{
					TextureManager.addTexture("tiles."+this.getTextureName(), TextureManager.TILES+this.getTextureName()+".png");
				}
				if(!TextureManager.hasTexture(this.getIconName())){
					TextureManager.addTexture("tiles."+this.getIconName(), TextureManager.TILES+this.getIconName()+".png");
				}
			}
		}
	}

	public void blockUpdate(int x, int y, int z, WorldManager worldManager) {
		
	}
	public void onCollide(int x, int y, int z, WorldManager worldManager, Entity e) {
		
	}

	public void renderHitbox(Vector3f pos) {
		
	}

	public boolean hasTexture() {
		return true;
	}

	public String getIconName() {
		return this.getTextureName();
	}

	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return TextureManager.tile(this);
	}

	public float[] getIconCoords() {
		// TODO Auto-generated method stub
		return TextureManager.tileIcon(this);
	}
}
