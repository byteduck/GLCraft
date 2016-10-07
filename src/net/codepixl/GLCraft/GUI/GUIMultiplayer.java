package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;

import java.io.IOException;
import java.net.InetAddress;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.util.Constants;

public class GUIMultiplayer extends GUIScreen{
	
	public GUIMultiplayer(){
		this.setDrawStoneBackground(true);
	}
	
	@Override
	public void update(){
		/*try{
			GLCraft.getGLCraft().getWorldManager(false).createBlankWorld();
			GLCraft.getGLCraft().getCentralManager(false).connectToServer(InetAddress.getLocalHost(), 5443);
			Constants.setState(Constants.GAME);
			glDisable(GL_TEXTURE_2D);
			GUIManager.getMainManager().closeGUI(false);
		}catch (IOException e){
			e.printStackTrace();
		}*/
	}
	
	@Override
	public void onClose(){
		GUIManager.getMainManager().showGUI("startScreen");
	}
}
