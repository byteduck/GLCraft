package net.codepixl.GLCraft.util.data.saves;

import java.io.File;

import net.codepixl.GLCraft.util.Constants;

public class Save {
	public String name,dispName,version,format;
	public Save(String name, String dispName, String version, String format){
		this.name = name;
		this.dispName = dispName;
		this.version = version;
		this.format = format;
	}
	
	public File getFolder(){
		return new File(Constants.GLCRAFTDIR+"saves/"+this.name);
	}
	
	@Override
	public String toString(){
		return "Name: "+name+" Display Name: "+dispName+" Version: "+version+" Format: "+format;
	}
}
