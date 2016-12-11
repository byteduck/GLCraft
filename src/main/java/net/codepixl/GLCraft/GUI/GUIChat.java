package net.codepixl.GLCraft.GUI;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.GUI.Elements.GUITextBox;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.network.packet.PacketChat;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import org.lwjgl.input.Mouse;

import java.util.concurrent.Callable;

public class GUIChat extends GUIScreen{
	
	public boolean mouseGrabbed = false;
	public GUITextBox input;
	
	public static int CHATY = (int)(Constants.getHeight()*0.79);
	public static int CHATX = 20;
	public static int CHATWIDTH = (int)(Constants.getWidth()*0.8);
	private WorldManager worldManager;
	public String initialText = "";
	
	public GUIChat(final WorldManager worldManager){
		super();
		this.worldManager = worldManager;
		CHATY = (int)(Constants.getHeight()-GUISlot.size*3);
		CHATX = 20;
		CHATWIDTH = (int)(Constants.getWidth()*0.8);
	}
	
	@Override
	public void makeElements(){
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
		input.setText(initialText);
		addElement(input);
	}
	
	@Override
	public void onOpen(){
		super.onOpen();
		GUIManager.getMainManager().setFocusedTextBox(input);
	}
	
	@Override
	public void update(){
		if(didResize){
			CHATY = (int)(Constants.getHeight()*0.79);
			CHATX = 20;
			CHATWIDTH = (int)(Constants.getWidth()*0.8);
		}
		super.update();
	}
	
	@Override
	public void onClose(){
		super.onClose();
		Mouse.setGrabbed(true);
	}
}
