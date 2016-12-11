package net.codepixl.GLCraft.render.texturepack;

import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.util.Constants;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;

public class TexturePackManager {
	public static void setTexturePack(String name) throws ZipException, FileNotFoundException, UnsupportedEncodingException{
		if(name.equals("Default textures")){
			new File(System.getProperty("user.home")+"/GLCraft/Texturepacks/currentTP.txt").delete();
		}else{
			File TP = new File(Constants.GLCRAFTDIR+"Texturepacks/"+name+".zip");
			File tempFolder = new File(System.getProperty("user.home") + "/GLCraft/Texturepacks/tmp/textures");
			tempFolder.delete();
			tempFolder.mkdirs();
			ZipFile zip = new ZipFile(TP);
			zip.extractAll(tempFolder.getAbsolutePath());

			File texturepackInfo = new File(System.getProperty("user.home") + "/GLCraft/Texturepacks/currentTP.txt");
			try {
				texturepackInfo.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			PrintWriter writer = null;
			writer = new PrintWriter(texturepackInfo.getAbsolutePath(), "UTF-8");
			writer.print(TP.getName().substring(0, TP.getName().length()-4));
			writer.close();
		}
		
		initTexturePacks();
		TextureManager.regenerateAtlas();
	}

	public static void initTexturePacks() {
		String texturepacksFolder = System.getProperty("user.home")+"/GLCraft/Texturepacks";
		new File(texturepacksFolder).mkdirs();
		File texturepackInfo = new File(System.getProperty("user.home")+"/GLCraft/Texturepacks/currentTP.txt");
		PrintWriter writer = null;
		if(!texturepackInfo.exists()){
			try {
				texturepackInfo.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				writer = new PrintWriter(texturepackInfo.getAbsolutePath(), "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			writer.print("none");
			writer.close();
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(texturepackInfo.getAbsolutePath()));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String sCurrentLine= null;
		try {
			while ((sCurrentLine = br.readLine()) != null) {
				TextureManager.currentTexturepack = sCurrentLine;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
