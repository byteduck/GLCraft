package net.codepixl.GLCraft.world.tile;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.BreakSource;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.particle.Particle;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.material.Material;
import net.codepixl.GLCraft.world.tile.ore.TileBluestoneOre;
import net.codepixl.GLCraft.world.tile.ore.TileCoalOre;
import net.codepixl.GLCraft.world.tile.ore.TileDiamondOre;
import net.codepixl.GLCraft.world.tile.ore.TileGoldOre;
import net.codepixl.GLCraft.world.tile.ore.TileIronOre;
import net.codepixl.GLCraft.world.tile.ore.TileStone;
import net.codepixl.GLCraft.world.tile.tick.TickHelper;
import net.codepixl.GLCraft.world.tile.tileentity.TileChest;
import net.codepixl.GLCraft.world.tile.tileentity.TileFurnace;

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
	public static Tile CoalOre = new TileCoalOre();
	public static Tile IronOre = new TileIronOre();
	public static Tile GoldOre = new TileGoldOre();
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
	public static Tile Bedrock = new TileBedrock();
	public static Tile Chest = new TileChest();
	public static Tile Dirt = new TileDirt();
	public static Tile Furnace = new TileFurnace();
	public static Tile Cobblestone = new TileCobblestone();
	public static Tile BluestoneOre = new TileBluestoneOre();
	public static Tile DiamondOre = new TileDiamondOre();
	public static Tile CondensedBluestone = new TileCondensedBluestone();
	
	public String getName(){
		return "Un-named block";
	}
	
	public byte getId(){
		return -1;
	}
	
	public boolean customHitbox(){
		return false;
	}
	
	public AABB getAABB(byte meta){
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
		return this.getName().toLowerCase().replace(" ", "_");
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
	
	public ItemStack getDrop(int x, int y, int z, BreakSource source, WorldManager w){
		return new ItemStack(this, 1, w.getMetaAtPos(x, y, z));
	}
	
	public void onBreak(int x, int y, int z, boolean drop, BreakSource source, WorldManager worldManager){
		if(drop){
			ItemStack is = getDrop(x, y, z, source, worldManager);
			if(!is.isNull()){
				worldManager.spawnEntity(new EntityItem(is,(float)x+0.5f,(float)y+0.5f,(float)z+0.5f,worldManager));
				for(int i=1; i<5; i++){
					Particle particle = new Particle(new Vector3f(x+Constants.randFloat(0,1),y+Constants.randFloat(0,1),z+Constants.randFloat(0,1)), new Vector3f(Constants.randFloat(-0.1f, 0.1f),0,Constants.randFloat(-0.1f, 0.1f)), worldManager);
					if(this.hasMetaTextures() == false){
						particle.setTexCoords(this.getTexCoords());
					}else{
						particle.setTexCoords(this.getIconCoords(worldManager.getMetaAtPos(x, y, z)));
					}
					particle.setLifeTime(Constants.randFloat(0.3f,0.7f));
					worldManager.entityManager.add(particle);
				}
			}
		}
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
		if(!tileMap.containsKey(id))
			return Air;
		else
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
									TextureManager.addTexture("tiles."+name, TextureManager.TILES+this.getFolderSuffix()+name+".png");
								}
							}
						}
					}else{
						for(String name : this.getMultiTextureNames()){
							if(!TextureManager.hasTexture("tiles."+name)){
								TextureManager.addTexture("tiles."+name, TextureManager.TILES+this.getFolderSuffix()+name+".png");
							}
						}
					}
				}else{
					if(this.hasMetaTextures()){
						for(int i = 0; i < 128; i++){
							TextureManager.addTexture("tiles."+this.getTextureName((byte) i), TextureManager.TILES+this.getFolderSuffix()+this.getTextureName((byte) i)+".png");
						}
					}else{
						TextureManager.addTexture("tiles."+this.getTextureName(), TextureManager.TILES+this.getFolderSuffix()+this.getTextureName()+".png");
					}
				}
				if(this.hasMetaTextures()){
					for(int i = 0; i < 128; i++){
						if(!TextureManager.hasTexture(this.getIconName((byte) i))){
							TextureManager.addTexture("tiles."+this.getIconName((byte) i), TextureManager.TILES+this.getFolderSuffix()+this.getIconName((byte) i)+".png");
						}	
					}
				}else{
					if(!TextureManager.hasTexture(this.getIconName())){
						TextureManager.addTexture("tiles."+this.getIconName(), TextureManager.TILES+this.getFolderSuffix()+this.getIconName()+".png");
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
		return TextureManager.tile(this);
	}
	
	public float[] getTexCoords(byte meta){
		return TextureManager.tile(this, meta);
	}

	public float[] getIconCoords() {
		return TextureManager.tileIcon(this);
	}
	
	public float[] getIconCoords(byte meta) {
		return TextureManager.tileIcon(this, meta);
	}

	public boolean canBeDestroyedByLiquid() {
		return false;
	}

	public boolean canBePlacedOver() {
		return false;
	}

	public RenderType getCustomRenderType() {
		return RenderType.CUBE;
	}
	
	public byte getPowerLevel(int x, int y, int z, WorldManager w){
		return 0;
	}
}
