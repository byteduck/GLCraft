package net.codepixl.GLCraft.util;

public class StringUtil{
	public static String fromArray(String[] strings, String separator, int startAt){
		StringBuilder s = new StringBuilder();
		for(int i = startAt; i < strings.length; i++){
			s.append(strings[i]);
			if(i < strings.length-1)
				s.append(separator);
		}
		return s.toString();
	}
}
