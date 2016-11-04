package net.codepixl.GLCraft.render.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Properties;

import net.codepixl.GLCraft.util.Constants;

public class SettingsManager{
	private static Properties defaultProps = new Properties();
	private static Properties userProps = new Properties();
	public static void init(){
		boolean loadedUserProps = false;
		try {
			defaultProps.load(SettingsManager.class.getClassLoader().getResourceAsStream("properties/settings.properties"));
			if(new File(Constants.GLCRAFTDIR, "settings.properties").exists()){
				loadedUserProps = true;
				InputStream is = new FileInputStream(Constants.GLCRAFTDIR+File.separator+"settings.properties");
				userProps.load(is);
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!loadedUserProps){
			for(Entry<Object, Object> e : defaultProps.entrySet()){
				userProps.setProperty((String)e.getKey(), (String)e.getValue());
			}
			userProps.setProperty("name", "Player"+Constants.randInt(1, 10000));
			OutputStream os;
			try {
				os = new FileOutputStream(Constants.GLCRAFTDIR+File.separator+"settings.properties");
				userProps.store(os, "The GLCraft settings file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Entry<Object, Object> e : defaultProps.entrySet())
			if(!userProps.containsKey((String)e.getKey()))
				userProps.setProperty((String)e.getKey(), (String)e.getValue());
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				SettingsManager.saveSettings();
			}
		}));
	}
	
	public static String getSetting(String key){
		return userProps.getProperty(key, defaultProps.getProperty(key, ""));
	}
	
	public static void setSetting(String key, String value){
		userProps.setProperty(key, value);
	}
	
	public static void saveSettings(){
		OutputStream os;
		try {
			os = new FileOutputStream(Constants.GLCRAFTDIR+File.separator+"settings.properties");
			userProps.store(os, "The GLCraft settings file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
