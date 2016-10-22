package net.codepixl.GLCraft.util;

import org.newdawn.slick.Color;

public class ChatFormat {
	
	public static final char DELIMETER = '§';
	public static final char PREFIX = DELIMETER;
	
	//COLOR
	public static final String BLACK =      PREFIX+"0";
	public static final String DARKBLUE =   PREFIX+"1";
	public static final String DARKGREEN =  PREFIX+"2";
	public static final String DARKAQUA =   PREFIX+"3";
	public static final String DARKRED =    PREFIX+"4";
	public static final String DARKPURPLE = PREFIX+"5";
	public static final String GOLD =       PREFIX+"6";
	public static final String GRAY =       PREFIX+"7";
	public static final String DARKGRAY =   PREFIX+"8";
	public static final String BLUE =       PREFIX+"9";
	public static final String GREEN =      PREFIX+"a";
	public static final String AQUA =       PREFIX+"b";
	public static final String RED =        PREFIX+"c";
	public static final String PURPLE =     PREFIX+"d";
	public static final String YELLOW =     PREFIX+"e";
	public static final String WHITE =      PREFIX+"f";
	
	//FORMAT
	public static final String OBFUSCATE =     PREFIX+"k";
	public static final String BOLD =          PREFIX+"l";
	public static final String STRIKETHROUGH = PREFIX+"m";
	public static final String UNDERLINE =     PREFIX+"n";
	public static final String ITALIC =        PREFIX+"o";
	public static final String RESET =         PREFIX+"r";
	
	public static final Color CBBLACK = new Color(0x000000);
	public static final Color CBDARKBLUE = new Color(0x00002A);
	public static final Color CBDARKGREEN = new Color(0x002A00);
	public static final Color CBDARKAQUA = new Color(0x002A2A);
	public static final Color CBDARKRED = new Color(0x2A0000);
	public static final Color CBDARKPURPLE = new Color(0x2A002A);
	public static final Color CBGOLD = new Color(0x2A2A00);
	public static final Color CBGRAY = new Color(0x2A2A2A);
	public static final Color CBDARKGRAY = new Color(0x151515);
	public static final Color CBBLUE = new Color(0x15153F);
	public static final Color CBGREEN = new Color(0x153F15);
	public static final Color CBAQUA = new Color(0x153F3F);
	public static final Color CBRED = new Color(0x3F1515);
	public static final Color CBPURPLE = new Color(0x3F153F);
	public static final Color CBYELLOW = new Color(0x3F3F15);
	public static final Color CBWHITE = new Color(0x3F3F3F);
	
	public static final Color CBLACK = new Color(0x000000);
	public static final Color CDARKBLUE = new Color(0x0000AA);
	public static final Color CDARKGREEN = new Color(0x00AA00);
	public static final Color CDARKAQUA = new Color(0x00AAAA);
	public static final Color CDARKRED = new Color(0xAA0000);
	public static final Color CDARKPURPLE = new Color(0xAA00AA);
	public static final Color CGOLD = new Color(0xFFAA00);
	public static final Color CGRAY = new Color(0xAAAAAA);
	public static final Color CDARKGRAY = new Color(0x555555);
	public static final Color CBLUE = new Color(0x5555FF);
	public static final Color CGREEN = new Color(0x55FF55);
	public static final Color CAQUA = new Color(0x55FFFF);
	public static final Color CRED = new Color(0xFF5555);
	public static final Color CPURPLE = new Color(0xFF55FF);
	public static final Color CYELLOW = new Color(0xFFFF55);
	public static final Color CWHITE = new Color(0xFFFFFF);
	
	public static Color getColor(String code){
		if(code.equals(BLACK))
			return CBLACK;
		else if(code.equals(DARKBLUE))
			return CDARKBLUE;
		else if(code.equals(DARKGREEN))
			return CDARKGREEN;
		else if(code.equals(DARKAQUA))
			return CDARKAQUA;
		else if(code.equals(DARKRED))
			return CDARKRED;
		else if(code.equals(DARKPURPLE))
			return CDARKPURPLE;
		else if(code.equals(GOLD))
			return CGOLD;
		else if(code.equals(GRAY))
			return CGRAY;
		else if(code.equals(DARKGRAY))
			return CDARKGRAY;
		else if(code.equals(BLUE))
			return CBLUE;
		else if(code.equals(GREEN))
			return CGREEN;
		else if(code.equals(AQUA))
			return CAQUA;
		else if(code.equals(RED))
			return CRED;
		else if(code.equals(PURPLE))
			return CPURPLE;
		else if(code.equals(YELLOW))
			return CYELLOW;
		else if(code.equals(WHITE))
			return CWHITE;
		return null;
	}
	
	public static Color getBackgroundColor(String code){
		if(code.equals(BLACK))
			return CBBLACK;
		else if(code.equals(DARKBLUE))
			return CBDARKBLUE;
		else if(code.equals(DARKGREEN))
			return CBDARKGREEN;
		else if(code.equals(DARKAQUA))
			return CBDARKAQUA;
		else if(code.equals(DARKRED))
			return CBDARKRED;
		else if(code.equals(DARKPURPLE))
			return CBDARKPURPLE;
		else if(code.equals(GOLD))
			return CBGOLD;
		else if(code.equals(GRAY))
			return CBGRAY;
		else if(code.equals(DARKGRAY))
			return CBDARKGRAY;
		else if(code.equals(BLUE))
			return CBBLUE;
		else if(code.equals(GREEN))
			return CBGREEN;
		else if(code.equals(AQUA))
			return CBAQUA;
		else if(code.equals(RED))
			return CBRED;
		else if(code.equals(PURPLE))
			return CBPURPLE;
		else if(code.equals(YELLOW))
			return CBYELLOW;
		else if(code.equals(WHITE))
			return CBWHITE;
		return null;
	}
}
