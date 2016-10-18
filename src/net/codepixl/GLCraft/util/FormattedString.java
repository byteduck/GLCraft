package net.codepixl.GLCraft.util;

import org.newdawn.slick.Color;

public class FormattedString {
	public String color = ChatFormat.WHITE;
	public boolean bold, italic, underline, obfuscated;
	protected boolean compiledFormat = false, compiledColor = false;
	public String string;
	public FormattedString(String string){
		this.string = string;
	}
	
	public Color getColor() {
		Color c = ChatFormat.getColor(color);
		return c != null ? c : ChatFormat.CWHITE;
	}
	
	public Color getBackgroundColor() {
		Color c = ChatFormat.getBackgroundColor(color);
		return c != null ? c : ChatFormat.CBWHITE;
	}
}
