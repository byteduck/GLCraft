package net.codepixl.GLCraft.util.data.saves;

import java.io.File;

import net.codepixl.GLCraft.util.Constants;

public class Save implements Comparable<Save>{
	public String name,dispName,version,format;
	public long timestamp, worldTime;
	public Save(String name, String dispName, String version, String format){
		this.name = name;
		this.dispName = dispName;
		this.version = version;
		this.format = format;
	}
	
	public Save(String name, String dispName, String version, String format, long timestamp, long worldTime){
		this.name = name;
		this.dispName = dispName;
		this.version = version;
		this.format = format;
		this.timestamp = timestamp;
		this.worldTime = worldTime;
	}
	
	public File getFolder(){
		return new File(Constants.GLCRAFTDIR+"saves/"+this.name);
	}
	
	@Override
	public String toString(){
		return "|Name: "+name+"|Display Name: "+dispName+"|Version: "+version+"|Format: "+format+"|";
	}

	@Override
	public int compareTo(Save o) {
		return Long.compare(o.timestamp, this.timestamp);
	}
}
