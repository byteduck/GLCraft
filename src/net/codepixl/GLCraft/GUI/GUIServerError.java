package net.codepixl.GLCraft.GUI;

import org.apache.commons.lang3.StringUtils;

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
		int lines = StringUtils.countMatches(prefix+message, "\n")+1;
		lbl = new GUILabel(Constants.getWidth()/2, Constants.getHeight()/2-(int)((float)Constants.FONT.getHeight()*(lines/2f)), prefix+message);
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
		GUIManager.getMainManager().clearGUIStack();
		GUIManager.getMainManager().showGUI(new GUIStartScreen());
	}

}
