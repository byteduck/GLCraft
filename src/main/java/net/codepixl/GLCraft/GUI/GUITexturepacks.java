package net.codepixl.GLCraft.GUI;

import net.codepixl.GLCraft.GUI.Elements.GUIScrollBox;
import net.codepixl.GLCraft.GUI.Elements.GUITexture;
import net.codepixl.GLCraft.GUI.Elements.GUITexturepack;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.texturepack.TexturePackManagerWindow;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipFile;

/**
 * Created by aaron on 2/19/2017.
 */
public class GUITexturepacks extends GUIScreen{

	private GUIScrollBox scrollBox;
	private BufferedImage ogIcon;
	private GUITexturepack selectedPack;

	public void makeElements(){
		setDrawStoneBackground(true);

		scrollBox = new GUIScrollBox(10);
		scrollBox.x = 100;
		scrollBox.y = Tesselator.getFontHeight()*2+20;
		scrollBox.width = Constants.getWidth()-200;
		scrollBox.height = (int) (Constants.getHeight()*0.75f-scrollBox.y);
		addElement(scrollBox);
	}

	public void onOpen(){
		super.onOpen();
		scrollBox.clearItems();

		try {
			ogIcon = ImageIO.read(TexturePackManagerWindow.class.getResourceAsStream("/textures/misc/DefaultRP.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		File tpdir = new File(Constants.GLCRAFTDIR + "Texturepacks");
		File[] tpacks = tpdir.listFiles();
		HashMap<String, BufferedImage> otpacks = new HashMap<String, BufferedImage>();
		if(tpacks !=null){
			for (File tpack : tpacks) {
				String ext = tpack.getName().substring(tpack.getName().lastIndexOf('.') + 1);
				if (ext.equalsIgnoreCase("zip")) {
					String tpname = tpack.getName().substring(0, tpack.getName().lastIndexOf('.'));
					BufferedImage tpicon = null;
					try {
						ZipFile zf = new ZipFile(tpack);
						InputStream in = zf.getInputStream(zf.getEntry("preview.png"));
						tpicon = ImageIO.read(in);
						zf.close();
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
						tpicon = (BufferedImage) ogIcon.getScaledInstance(ogIcon.getWidth(), ogIcon.getHeight(), Image.SCALE_FAST);
					}
					otpacks.put(tpname, tpicon);
				}
			}
		}
		otpacks.put("Default textures", ogIcon);

		Iterator<String> i = otpacks.keySet().iterator();
		while(i.hasNext()){
			String name = i.next();
			BufferedImage icon = otpacks.get(name);
			GUITexturepack g = new GUITexturepack(name, icon, this);
			g.x = 10;
			g.width = Constants.getWidth()-240;
			if(name.equals(TextureManager.currentTexturepack) || (name.equals("Default textures") && TextureManager.currentTexturepack.equals("[[none]]"))) {
				g.selected = true;
				selectedPack = g;
			}
			scrollBox.addItem(g);
		}
	}

	public void onClose(){
		super.onClose();
		for(GUIScreen g : scrollBox.getItems())
			((GUITexturepack)g).deleteTexture();
	}

	public void setSelectedTP(GUITexturepack guiTexturepack) {
		if(selectedPack != null) selectedPack.selected = false;
		guiTexturepack.selected = true;
		selectedPack = guiTexturepack;
	}
}
