package net.codepixl.GLCraft.GUI;

import java.util.concurrent.Callable;

import org.lwjgl.input.Mouse;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.GUI.Elements.GUITextBox;
import net.codepixl.GLCraft.network.packet.PacketChat;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;

public class GUIChat extends GUIScreen{
	
	public boolean mouseGrabbed = false;
	public GUITextBox input;
	
	public static int CHATY = (int)(Constants.HEIGHT*0.79);
	public static int CHATX = 20;
	public static int CHATWIDTH = (int)(Constants.WIDTH*0.8);
	
	public GUIChat(final WorldManager worldManager){
		input = new GUITextBox(CHATX,CHATY,CHATWIDTH,"");
		input.drawOutline = false;
		input.bgColor = new Color4f(0,0,0,0.5f);
		input.closeOnUnfocus = true;
		input.setOnEnterCallback(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				String chat = input.getText();
				if(!chat.equals("")){
					worldManager.sendPacket(new PacketChat(chat));
					input.setText("");
				}
				GUIManager.getMainManager().unfocusTextBox();
				return null;
			}
		});
		input.characterLimit = 100;
		addElement(input);
	}
	
	@Override
	public void onOpen(){
		super.onOpen();
		GUIManager.getMainManager().setFocusedTextBox(input);
	}
	
	@Override
	public void onClose(){
		super.onClose();
		Mouse.setGrabbed(true);
	}
}
