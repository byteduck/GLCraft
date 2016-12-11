package net.codepixl.GLCraft.util;

import net.codepixl.GLCraft.GLCraft;

import java.io.*;
import java.util.Map.Entry;
import java.util.Properties;

public class SettingsManager{
	private static Properties defaultProps = new Properties();
	private static Properties userProps = new Properties();
	public static File propsFile;
	public static void init(){
		propsFile = new File(GLCraft.getGLCraft().isServer() ? "server.properties" : Constants.GLCRAFTDIR+File.separator+"settings.properties");
		boolean loadedUserProps = false;
		try {
			defaultProps.load(SettingsManager.class.getClassLoader().getResourceAsStream(GLCraft.getGLCraft().isServer() ? "properties/server.properties" : "properties/settings.properties"));
			if(propsFile.exists()){
				loadedUserProps = true;
				InputStream is = new FileInputStream(propsFile);
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
			if(!GLCraft.getGLCraft().isServer()) userProps.setProperty("name", "Player"+Constants.randInt(1, 10000));
			OutputStream os;
			try {
				os = new FileOutputStream(propsFile);
				userProps.store(os, GLCraft.getGLCraft().isServer() ? "Properties for your server." : "The GLCraft settings file.");
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

	public static int getInt(String key){
		String setting = getSetting(key);
		try{
			return key == null ? 0 : Integer.parseInt(setting);
		}catch(NumberFormatException e){
			return 0;
		}
	}
	
	public static void setSetting(String key, String value){
		userProps.setProperty(key, value);
	}
	
	public static void saveSettings(){
		OutputStream os;
		try {
			os = new FileOutputStream(propsFile);
			userProps.store(os, "The GLCraft settings file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
