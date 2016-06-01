package net.codepixl.GLCraft.util;

public class CrashHandler implements Thread.UncaughtExceptionHandler{
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		new CrashHandlerWindow(t,e);
	}

}
