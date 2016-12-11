package net.codepixl.GLCraft.world.item;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Spritesheet;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import static org.lwjgl.opengl.GL11.*;

public class ItemStack{
	private Tile tile;
	private Item item;
	public int count;
	private boolean isTile;
	private boolean isNull;
	private byte meta;
	public ItemStack(Tile t){
		if(t != null){
			count = 1;
			isTile = true;
			tile = t;
			meta = 0;
		}else{
			this.isNull = true;
			this.isTile = false;
		}
		if(tile == Tile.Air){
			this.isNull = true;
			this.isTile = false;
		}
	}
	public ItemStack(Tile t, byte meta){
		if(t != null){
			count = 1;
			isTile = true;
			tile = t;
			this.meta = meta;
		}else{
			this.isNull = true;
			this.isTile = false;
		}
		if(tile == Tile.Air){
			this.isNull = true;
			this.isTile = false;
		}
	}
	public ItemStack(){
		isNull = true;
		isTile = false;
	}
	public ItemStack(Item i){
		if(i != null){
			count = 1;
			isTile = false;
			item = i;
			meta = 0;
		}else{
			this.isNull = true;
			this.isTile = false;
		}
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
		if(t != null){
			this.count = count;
			isTile = true;
			tile = t;
			meta = 0;
		}else{
			this.isNull = true;
		}
		if(tile == Tile.Air){
			this.isNull = true;
			this.isTile = false;
		}
	}
	public ItemStack(Tile t, int count, byte meta){
		if(t != null){
			this.count = count;
			isTile = true;
			tile = t;
			this.meta = meta;
		}else{
			this.isNull = true;
			this.isTile = false;
		}
		if(tile == Tile.Air){
			this.isNull = true;
			this.isTile = false;
		}
	}
	public ItemStack(Item i, int count){
		if(i != null){
			this.count = count;
			isTile = false;
			item = i;
			meta = 0;
		}else{
			this.isNull = true;
			this.isTile = false;
		}
	}
	public ItemStack(Item i, int count, byte meta){
		if(i != null){
			this.count = count;
			isTile = false;
			item = i;
			this.meta = meta;
		}else{
			this.isNull = true;
			this.isTile = false;
		}
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
			if(this.count >= getMaxStackSize()){
				return ret;
			}
			this.count+=1;
			ret-=1;
		}
		return ret;
	}
	
	public int getMaxStackSize(){
		if(this.isNull)
			return 0;
		if(!this.isTile)
			return this.item.maxStackSize();
		else
			return 64;
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
		return compatible(itemstack, false);
	}
	
	public boolean compatible(ItemStack itemstack, boolean ignoreMeta) {
		if(this.isTile && itemstack.isTile() && this.tile == itemstack.tile && (this.meta == itemstack.meta || ignoreMeta)) return true;
		if(this.isItem() && itemstack.isItem() && this.item == itemstack.item && (this.meta == itemstack.meta || ignoreMeta)) return true;
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
	
	public float[] getTexCoords() {
		if(!this.isNull)
			if(this.isTile)
				if(this.tile.hasMetaTextures())
					return this.tile.getTexCoords(this.meta);
				else
					return this.tile.getTexCoords();
			else
				return this.item.getTexCoords();
		else
			return TextureManager.texture("misc.nothing");
	}
	
	public TagCompound toNBT(){
		TagCompound ret = new TagCompound("");
		if(!this.isNull){
			ret.setTag(new TagByte("isItem",(byte) (this.isItem() ? 1 : 0 )));
			ret.setTag(new TagByte("id",this.getId()));
			ret.setTag(new TagByte("count",(byte)this.count));
			ret.setTag(new TagByte("meta",(byte)this.meta));
		}
		return ret;
	}
	
	public TagCompound toNBT(String name){
		TagCompound ret = toNBT();
		ret.setName(name);
		return ret;
	}
	
	public static ItemStack fromNBT(TagCompound tag) throws UnexpectedTagTypeException, TagNotFoundException{
		ItemStack stack;
		try{
			if(tag.getByte("isItem") == 0){
				stack = new ItemStack(Tile.getTile(tag.getByte("id")));
			}else{
				stack = new ItemStack(Item.getItem(tag.getByte("id")));
			}
			stack.count = tag.getByte("count");
			try{
				stack.meta = tag.getByte("meta");
			}catch(TagNotFoundException e){} //In case the world was saved with an older version
		}catch(TagNotFoundException e){
			stack = new ItemStack();
		}
			
		return stack;
	}
	
	public float[] getIconCoords() {
		if(!this.isNull)
			if(this.isTile)
				if(this.tile.hasMetaTextures())
					return this.tile.getIconCoords(this.meta);
				else
					return this.tile.getIconCoords();
			else
				return this.item.getTexCoords();
		else
			return TextureManager.texture("misc.nothing");
	}
	
	public void renderIcon(int x, int y, float size){
		renderIcon(x,y,size,true,Color4f.WHITE);
	}
	
	public void renderIcon(int x, int y, float size, boolean text, Color4f color){
		if(!this.isNull() && this.count != 0){
			Spritesheet.atlas.bind();
			GL11.glPushMatrix();
			if(this.isTile() && (this.getTile().getRenderType() == RenderType.CUBE || (this.getTile().getRenderType() == RenderType.CUSTOM && this.getTile().getCustomRenderType() == RenderType.CUBE))){
				glTranslatef(x-size/2,y-size/2,0);
				glScalef(0.5f,0.5f,0.5f);
				GL11.glRotatef(140f,1.0f,0.0f,0.0f);
				GL11.glRotatef(45f,0.0f,1.0f,0.0f);
				glBegin(GL_QUADS);
				if(this.getTile().hasMetaTextures()){
					Shape.createCube(size/2.25f,-size*1.5f,0, color, this.getTile().getTexCoords(this.getMeta()), size);
				}else{
					Shape.createCube(size/2.25f,-size*1.5f,0, color, this.getTile().getTexCoords(), size);
				}
			}else{
				glTranslatef(x,y,0);
				glScalef(0.7f, 0.7f, 0.7f);
				glBegin(GL_QUADS);
				if(this.isItem())
					Shape.createCenteredSquare(0,0, color, this.getItem().getTexCoords(), size);
				else
					Shape.createCenteredSquare(0,0, color, this.getTile().getIconCoords(), size);
				
			}
			glEnd();
			GL11.glPopMatrix();
			if(text){
				if(this.count > 1)
					Tesselator.drawTextWithShadow(x+size*0.4f-Tesselator.getFontWidth(Integer.toString(this.count)), y, Integer.toString(this.count));
				else if(this.count < 1)
					Tesselator.drawTextWithShadow(x+size*0.4f-Tesselator.getFontWidth(Integer.toString(this.count)), y, Integer.toString(this.count), Color.red, Color.darkGray);
				TextureImpl.unbind();
			}
		}
		TextureImpl.unbind();
	}
	
	public String getName(){
		if(isTile)
			return tile.getName();
		else if(!isNull)
			return item.getName();
		else
			return "";
	}
}
