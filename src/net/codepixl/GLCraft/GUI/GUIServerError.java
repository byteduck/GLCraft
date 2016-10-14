package net.codepixl.GLCraft.GUI;

import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.util.Constants;

public class GUIServerError extends GUIScreen{
	
	GUILabel lbl;
	String message;
	
	public GUIServerError(String prefix, String message){
		this.message = message;
		if(message == null)
			message = "Unknown error";
		this.setDrawStoneBackground(true);
		lbl = new GUILabel(Constants.WIDTH/2, Constants.HEIGHT/2-Constants.FONT.getHeight(), prefix+message);
		lbl.alignment = GUILabel.Alignment.CENTER;
		lbl.size = 2f;
		addElement(lbl);
	}
	
	@Override
	public void update(){
		if(message.equals("")){
			GUIManager.getMainManager().closeGUI(true);
			return;
		}
	}
	
	@Override
	public void onClose(){
		super.onClose();
		GUIManager.getMainManager().showGUI("startScreen");
	}

}
