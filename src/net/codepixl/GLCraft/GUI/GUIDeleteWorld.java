package net.codepixl.GLCraft.GUI;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.FileUtil;
import net.codepixl.GLCraft.util.data.saves.Save;

public class GUIDeleteWorld extends GUIScreen{
	
	private Save save;
	private GUILabel warning;
	private GUIButton yes, no;
	
	public GUIDeleteWorld(Save save){
		super();
		this.save = save;
	}
	
	public void makeElements(){
		final int MIDDLE = Constants.getWidth()/2;
		final int WARNINGY = (int) (Constants.getHeight()*0.4);
		final int BUTTONY = (int) (Constants.getHeight()*0.6);
		final int YESX = (int) (Constants.getWidth()*0.3);
		final int NOX = (int) (Constants.getWidth()*0.7);
		setDrawStoneBackground(true);
		warning = new GUILabel(MIDDLE, WARNINGY, "Are you sure you want to delete '"+save.dispName+"'?");
		warning.alignment = GUILabel.Alignment.CENTER;
		yes = new GUIButton("Yes", YESX, BUTTONY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				delete();
				return null;
			}
		});
		
		no = new GUIButton("No", NOX, BUTTONY, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				GUIManager.getMainManager().closeGUI(true);
				return null;
			}
		});
		
		addElements(warning, yes, no);
	}
	
	private void delete() {
		File f = new File(Constants.GLCRAFTDIR+"saves/"+save.name);
		if(f.exists()){
			try {
				FileUtil.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GUIManager.getMainManager().closeGUI(true);
	}

}
