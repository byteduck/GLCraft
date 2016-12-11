package net.codepixl.GLCraft.util;

import org.newdawn.slick.Color;

import java.util.ArrayList;

public class FormattedStringSet{
	public final FormattedString[] strings;
	public FormattedStringSet(String s){
		ArrayList<FormattedString> set = new ArrayList<FormattedString>();
		int cset = 0;
		set.add(new FormattedString(""));
		char[] chars = s.toCharArray();
		for(int i = 0; i < chars.length; i++){
			if(chars[i] == ChatFormat.DELIMETER && i+1 < chars.length){
				if(i > 0){
					cset++;
					set.add(new FormattedString(""));
				}
				Color c = ChatFormat.getColor(""+chars[i]+chars[i+1]);
				if(c != null && !set.get(cset).compiledColor){
					set.get(cset).color = ""+chars[i]+chars[i+1];
				}
				i++;
			}else if(chars[i] != ChatFormat.DELIMETER){
				set.get(cset).string+=chars[i];
			}
		}
		strings = new FormattedString[set.size()];
		set.toArray(strings);
	}
}
