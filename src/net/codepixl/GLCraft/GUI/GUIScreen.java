package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.tile.Tile;
import net.codepixl.GLCraft.world.tile.ore.TileOre;

public class GUIScreen{
	public int x = 0;
	public int y = 0;
	public int width = Constants.getWidth();
	public int height = Constants.getHeight();
	public boolean enabled = true;
	private ArrayList<GUIScreen> elements = new ArrayList<GUIScreen>();
	public boolean mouseGrabbed = false;
	public boolean playerInput = false;
	public boolean visible = true;
	private float[][][] bgTexCoords;
	private boolean drawStoneBackground = false;
	public boolean didResize;

	public void setDrawStoneBackground(boolean drawStoneBackground) {
		this.drawStoneBackground = drawStoneBackground;
	}

	public void renderMain(){
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		drawBG();
		this.render();
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			GUIScreen next = it.next();
			if(next.visible)
				next.renderMain();
		}
		GL11.glPopMatrix();
	}
	
	/**
	 * IMPORTANT: Once this method is called, glTranslate has already been called with this GUIScreen's x and y position.
	 */
	public void render(){
		
	}
	
	public void initGL(){
		
	}
	
	public boolean testMouse(int xof, int yof){
		int mouseY = Mouse.getY()+yof;
		int mouseX = Mouse.getX()-xof;
		mouseY = -mouseY+Constants.getHeight();
		if(mouseY <= y+height && mouseY >= y){
			if(mouseX <= x+width && mouseX >= x){
				return true;
			}
		}
		return false;
	}
	
	public boolean testClick(int xof, int yof, int button){
		if(testMouse(xof,yof)){
			while(Mouse.next()){
				if(Mouse.getEventButtonState()){
					if(Mouse.isButtonDown(button)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void update(){
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			it.next().update();
		}
		if(isFullScreen() && this.didResize){
			this.height = Constants.getHeight();
			this.width = Constants.getWidth();
			this.clearElements();
			this.makeElements();
			if(drawStoneBackground)
				generateRandomTiles();
			this.didResize = false;
		}
	}
	
	public void input(int xOffset, int yOffset){
		if(!enabled)
			return;
		Iterator<GUIScreen> it = elements.iterator();
		while(it.hasNext()){
			GUIScreen s = it.next();
			s.input(xOffset, yOffset);
		}
	}
	
	public GUIScreen(){
		
	}
	
	public void addElement(GUIScreen element){
		this.elements.add(element);
	}
	
	public void addElements(GUIScreen...guiScreens){
		for(GUIScreen s : guiScreens)
			this.elements.add(s);
	}
	
	public void drawBG(){
		if(drawStoneBackground){
			try{
				Spritesheet.atlas.bind();
				int howManyWide = (width/64)+1;
				int howManyTall = (height/64)+1;
				for(int x = 0; x < howManyWide*64; x+=64){
					for(int y = 0; y < howManyTall*64; y+=64){
						glBegin(GL_QUADS);
						Shape.createSquare(x, y, Color4f.WHITE, bgTexCoords[x/64][y/64], 64);
						glEnd();
					}
				}
			}catch(NullPointerException e){
				//This happens if we're in the middle of changing texturepacks
			}
		}
	}
	
	public void onClose() {
		
	}
	
	public void onOpen() {
		if(drawStoneBackground)
			generateRandomTiles();
	}
	
	public static int getScreenWidth(){
		return Display.getWidth();
	}
	
	public static int getScreenHeight(){
		return Display.getHeight();
	}
	
	private void generateRandomTiles() {
		int howManyWide = (Constants.getWidth()/64)+1;
		int howManyTall = (Constants.getHeight()/64)+1;
		bgTexCoords = new float[howManyWide][howManyTall][2];
		for(int x = 0; x < howManyWide; x++){
			for(int y = 0; y < howManyTall; y++){
				int rand = Constants.randInt(0, 10+TileOre.ores.size()-1);
				if(rand > 11) bgTexCoords[x][y] = TextureManager.tile(TileOre.ores.get(rand-10)); else bgTexCoords[x][y] = TextureManager.tile(Tile.Stone);
			}
		}
	}

	public boolean canBeExited() {
		return true;
	}

	public boolean shouldRenderMouseItem(){
		return false;
	}
	
	public boolean isFullScreen(){
		return true;
	}
	
	public void makeElements(){}
	
	public void clearElements(){
		this.elements.clear();
	}
}
