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
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.material.Material;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;

public class Tile {

	public static HashMap<Byte, Tile> tileMap = new HashMap<Byte, Tile>();
	public static HashMap<Tile, TickHelper> tickMap = new HashMap<Tile, TickHelper>();
	
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
	public static Tile Sand = new TileSand();
	public static Tile ParticleProjector = new TileParticleProjector();
	public static Tile Workbench = new TileWorkbench();
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
	
	public int tickRate(){
		return 1;
	}
	
	public Color4f getColor(){
		return Color4f.WHITE;
	}
	
	public String getTextureName(){
		return this.getName();
	}
	
	public String getTextureName(byte meta){
		return this.getTextureName();
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
		worldManager.spawnEntity(new EntityItem(new ItemStack(this,worldManager.getMetaAtPos(x, y, z)),(float)x+0.5f,(float)y+0.5f,(float)z+0.5f,worldManager));
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
	
	public String[] getMultiTextureNames(byte meta){
		return getMultiTextureNames();
	}
	
	public boolean hasMetaTextures(){
		return false;
	}
	
	public boolean hasMultipleTextures(){
		return false;
	}
	
	public void tick(int x, int y, int z, WorldManager worldManager){}
	
	public static Tile getTile(byte id){
		return tileMap.get(id);
	}
	
	public Material getMaterial(){
		return Material.DEFAULT;
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
	
	public String getFolderSuffix(){
		return "";
	}
	
	public Tile(){
		if(this.getClass() != Tile.class && !(this instanceof PluginTile)){
			registerTile();
			if(this.hasTexture()){
				if(this.hasMultipleTextures()){
					if(this.hasMetaTextures()){
						for(int i = 0; i < 128; i++){
							for(String name : this.getMultiTextureNames((byte) i)){
								if(!TextureManager.hasTexture("tiles."+name)){
									TextureManager.addTexture("tiles."+name, TextureManager.TILES+this.getFolderSuffix()+"/"+name+".png");
								}
							}
						}
					}else{
						for(String name : this.getMultiTextureNames()){
							if(!TextureManager.hasTexture("tiles."+name)){
								TextureManager.addTexture("tiles."+name, TextureManager.TILES+this.getFolderSuffix()+"/"+name+".png");
							}
						}
					}
				}else{
					if(this.hasMetaTextures()){
						for(int i = 0; i < 128; i++){
							TextureManager.addTexture("tiles."+this.getTextureName((byte) i), TextureManager.TILES+this.getFolderSuffix()+"/"+this.getTextureName((byte) i)+".png");
						}
					}else{
						TextureManager.addTexture("tiles."+this.getTextureName(), TextureManager.TILES+this.getFolderSuffix()+"/"+this.getTextureName()+".png");
					}
				}
				if(this.hasMetaTextures()){
					for(int i = 0; i < 128; i++){
						if(!TextureManager.hasTexture(this.getIconName((byte) i))){
							TextureManager.addTexture("tiles."+this.getIconName((byte) i), TextureManager.TILES+this.getFolderSuffix()+"/"+this.getIconName((byte) i)+".png");
						}	
					}
				}else{
					if(!TextureManager.hasTexture(this.getIconName())){
						TextureManager.addTexture("tiles."+this.getIconName(), TextureManager.TILES+this.getFolderSuffix()+"/"+this.getIconName()+".png");
					}
				}
			}
		}
	}
	
	public boolean onClick(int x, int y, int z, EntityPlayer p, WorldManager worldManager){
		return false;
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
	
	public String getIconName(byte meta) {
		return this.getTextureName(meta);
	}
	
	@Override
	public String toString(){
		return getName();
	}

	public float[] getTexCoords() {
		// TODO Auto-generated method stub
		return TextureManager.tile(this);
	}
	
	public float[] getTexCoords(byte meta){
		// TODO Auto-generated method stub
		return TextureManager.tile(this, meta);
	}

	public float[] getIconCoords() {
		// TODO Auto-generated method stub
		return TextureManager.tileIcon(this);
	}
	
	public float[] getIconCoords(byte meta) {
		// TODO Auto-generated method stub
		return TextureManager.tileIcon(this, meta);
	}
}
