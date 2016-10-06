package net.codepixl.GLCraft.GUI;

import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.util.Constants;

public class GUIServerError extends GUIScreen{
	
	GUILabel lbl;
	
	public GUIServerError(String prefix, String message){
		if(message == null)
			message = "Unknown error";
		this.setDrawStoneBackground(true);
		lbl = new GUILabel(Constants.WIDTH/2, Constants.HEIGHT/2, prefix+message);
		lbl.alignment = GUILabel.Alignment.CENTER;
		lbl.size = 1.5f;
		addElement(lbl);
	}
	
	@Override
	public void onClose(){
		super.onClose();
		GUIManager.getMainManager().showGUI("startScreen");
	}

}
