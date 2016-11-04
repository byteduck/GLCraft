package net.codepixl.GLCraft.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {
	
	public static boolean deleteDirectory(File f) throws IOException{
		if(f.exists() && f.isDirectory()){
	        File[] files = f.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }else if(f.exists() && f.isFile()){
	    	return f.delete();
	    }
	    return(f.delete());
	}

}
