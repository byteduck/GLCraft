package net.codepixl.GLCraft.util;

public enum LogSource{
	SERVER, CLIENT, GLCRAFT, SILENT, NONE;
	
	@Override
	public String toString(){
		switch(this){
			case SERVER:
				return "[SERVER] ";
			case CLIENT:
				return "[CLIENT] ";
			case GLCRAFT:
				return "[GLCRAFT] ";
			case SILENT:
				return "[SILENT] ";
			default:
				return "";
		}
	}
}
