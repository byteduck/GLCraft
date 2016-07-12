package net.codepixl.GLCraft.util;

import java.awt.Font;
import java.util.Arrays;
import java.util.Random;
import org.newdawn.slick.TrueTypeFont;
import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.world.CentralManager;

public class Constants {
	public static Random rand = new Random();
	public static int viewDistance = 10;
	public static int seaLevel = 10;
	public static final int WORLDHEIGHT = 5;
	public static final int CHUNKSIZE = 16;
	public static final float textSize = 0.5f;
	public static boolean doneGenerating = false;
	public static final int BTNHEIGHT = 30;
	public static final int BTNPADDING = 10;
	public static final Color4f BTNCOLOR = new Color4f(0f, 0f, 0f, 1f);
	public static final Color4f BTNHOVERCOLOR = new Color4f(0.25f, 0.25f, 0.25f, 1f);
	public static final Color4f BTNPRESSEDCOLOR = new Color4f(0.35f, 0.35f, 0.35f, 1f);
	public static final Color4f BTNTEXTCOLOR = new Color4f(1f, 1f, 1f, 1f);
	public static TrueTypeFont FONT = new TrueTypeFont(new Font("GLCraft", Font.PLAIN, 16), true);
	public static final int START_SCREEN = 0;
	public static final int GAME = 1;
	public static final int SERVER = 2;
	public static final String GLCRAFTDIR = getGamePath();
	public static int GAME_STATE = START_SCREEN;
	public static CentralManager world;
	public static int worldLength = CHUNKSIZE * viewDistance;
	public static int WIDTH = 1000;
	public static int HEIGHT = 700;
	public static String[] SystemProperty = new String[54];
	public static String[] SystemPropertyName = new String[] { "java.runtime.name", "sun.boot.library.path",
			"java.vm.version", "java.vm.vendor", "java.vendor.url", "path.separator", "java.vm.name",
			"file.encoding.pkg", "user.country", "sun.java.launcher", "sun.os.patch.level",
			"java.vm.specification.name", "user.dir", "java.runtime.version", "java.awt.graphicsenv",
			"java.endorsed.dirs", "os.arch", "java.io.tmpdir", "java.vm.specification.vendor",
			"os.name", "sun.jnu.encoding", "java.library.path", "sun.awt.enableExtraMouseButtons",
			"java.specification.name", "java.class.version", "sun.management.compiler", "os.version", "user.home",
			"user.timezone", "java.awt.printerjob", "file.encoding", "java.specification.version", "user.name",
			"java.class.path", "java.vm.specification.version", "sun.arch.data.model", "java.home", "sun.java.command",
			"java.specification.vendor", "user.language", "awt.toolkit", "java.vm.info", "java.version",
			"java.ext.dirs", "sun.boot.class.path", "java.vendor", "file.separator", "java.vendor.url.bug",
			"sun.cpu.endian", "sun.io.unicode.encoding", "sun.font.fontmanager", "sun.desktop", "sun.cpu.isalist"};

	public static void getherSystemInfo() {

		Arrays.sort(SystemPropertyName);
		System.out.println("Gethering System Info...\n");
		for (int i = 0; i < SystemPropertyName.length; i++) {
			SystemProperty[i] = System.getProperty(SystemPropertyName[i]);
			System.out.println("	" + SystemPropertyName[i] + " = " + SystemProperty[i]);
		}
		System.out.println("\n");
	}

	public static String getGamePath() {
		if ("Linux".equals(System.getProperty("os.name"))) {
			return "~/GLCraft";
		} else {
			return System.getProperty("user.home") + "\\GLCraft\\";
		}
	}

	public static void setDoneGenerating(boolean generating) {
		doneGenerating = generating;
	}

	public static void setState(int state) {
		GAME_STATE = state;
	}

	public static void setWorld(CentralManager w) {
		world = w;
	}

	public static byte[] trim(byte[] bytes) {
		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}

		return Arrays.copyOf(bytes, i + 1);
	}

	public static int randInt(int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static float randFloat(float min, float max) {
		float randomNum = rand.nextFloat() * (max - min) + min;
		return randomNum;
	}

}
