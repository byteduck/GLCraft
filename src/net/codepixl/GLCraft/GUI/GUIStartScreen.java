package net.codepixl.GLCraft.GUI;

import java.awt.Font;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.TrueTypeFont;

import static org.lwjgl.opengl.GL11.*;
import net.codepixl.GLCraft.Network.Client.ServerDiscovery;
import net.codepixl.GLCraft.Network.Server.ServerBroadcast;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.util.Texture;
import net.codepixl.GLCraft.world.entity.mob.MobManager;

import com.nishu.utils.Color4f;
import com.nishu.utils.Window;

public class GUIStartScreen extends GUI{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int SINGLEPLAYERY = (int) (Constants.HEIGHT*0.3);
	private static final int MULTIPLAYERY = (int) (Constants.HEIGHT*0.5);
	private static final int SERVERY = (int) (Constants.HEIGHT*0.7);
	
	public static void render() {
		Mouse.setGrabbed(false);
		// TODO Auto-generated method stub
		glEnable(GL_TEXTURE_2D);
		drawBG();
		glDisable(GL_TEXTURE_2D);
		//glClearColor(0.3f,0.1f,0.3f,1.0f);
		GUI.createButton(MIDDLE,SINGLEPLAYERY,10,Constants.BTNHEIGHT,"Singleplayer",new Color4f(0f,0f,0f,1f));
		GUI.createButton(MIDDLE,MULTIPLAYERY,10,Constants.BTNHEIGHT,"Multiplayer",new Color4f(0f,0f,0f,1f));
		GUI.createButton(MIDDLE,SERVERY,10,Constants.BTNHEIGHT,"Server",new Color4f(0f,0f,0f,1f));
	}
	
	public static void drawBG(){
		Spritesheet.tiles.bind();
		glBegin(GL_QUADS);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*2, Spritesheet.tiles.uniformSize());
			glVertex2f(0,0);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*3, Spritesheet.tiles.uniformSize());
			glVertex2f(0,Constants.HEIGHT);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*3, 0);
			glVertex2f(Constants.WIDTH,Constants.HEIGHT);
			glTexCoord2f(Spritesheet.tiles.uniformSize()*2, 0);
			glVertex2f(Constants.WIDTH,0);
		glEnd();
	}
	
	public static void input(){
		
		if(Mouse.isButtonDown(0)){
			if(GUI.testClick(Mouse.getX(),Mouse.getY(),MIDDLE,SINGLEPLAYERY,Constants.FONT.getWidth("Singleplayer")+20,Constants.BTNHEIGHT)){
				Constants.setState(Constants.GAME);
				Constants.world.getWorldManager().createWorld();
				Constants.world.prepareForGame();
				glDisable(GL_TEXTURE_2D);
			}
			if(GUI.testClick(Mouse.getX(),Mouse.getY(),MIDDLE,MULTIPLAYERY,Constants.FONT.getWidth("Multiplayer")+20,Constants.BTNHEIGHT)){
				Constants.isMultiplayer = true;
				ServerDiscovery s = new ServerDiscovery();
				Thread t = new Thread(s);
				t.start();
				//Constants.world.getWorldManager().createWorld();
				Constants.world.prepareForGame();
				Constants.setState(Constants.GAME);
			}
			if(GUI.testClick(Mouse.getX(),Mouse.getY(),MIDDLE,SERVERY,Constants.FONT.getWidth("Server")+20,Constants.BTNHEIGHT)){
				//Window.dispose();
				Constants.isMultiplayer = true;
				Constants.setState(Constants.SERVER);
				Constants.world.getWorldManager().createWorld();
				Constants.world.getWorldManager().mobManager = new MobManager(Constants.world.getWorldManager());
				ServerBroadcast s = new ServerBroadcast();
				Thread t = new Thread(s);
				t.start();
			}
		}
	}
	
}
