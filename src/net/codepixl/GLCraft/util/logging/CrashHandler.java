package net.codepixl.GLCraft.util.logging;

import org.lwjgl.opengl.Display;

import com.nishu.utils.Window;

public class CrashHandler implements Thread.UncaughtExceptionHandler{
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Window.dispose();
		new CrashHandlerWindow(t,e);
	}

}
