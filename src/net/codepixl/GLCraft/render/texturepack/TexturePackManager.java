package net.codepixl.GLCraft.render.texturepack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import net.codepixl.GLCraft.util.Constants;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class TexturePackManager {
	public static void setTexturePack(String name) throws ZipException, FileNotFoundException, UnsupportedEncodingException{
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
		writer.println(TP.getName().substring(0, TP.getName().length()-4));
		writer.close();
		
		JOptionPane.showMessageDialog(null, "You must restart GLCraft for changes to take effect.");
	}
}
