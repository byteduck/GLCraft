package net.codepixl.GLCraft.GUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUILabel;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.FileUtil;
import net.codepixl.GLCraft.util.data.saves.Save;

public class GUIDeleteWorld extends GUIScreen{
	
	private static final int MIDDLE = Constants.WIDTH/2;
	private static final int WARNINGY = (int) (Constants.HEIGHT*0.4);
	private static final int BUTTONY = (int) (Constants.HEIGHT*0.6);
	private static final int YESX = (int) (Constants.WIDTH*0.3);
	private static final int NOX = (int) (Constants.WIDTH*0.7);
	
	private Save save;
	private GUILabel warning;
	private GUIButton yes, no;
	
	public GUIDeleteWorld(Save save){
		setDrawStoneBackground(true);
		this.save = save;
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

	@Override
	public void onClose(){
		GUIManager.getMainManager().showGUI("singleplayer");
	}

}
