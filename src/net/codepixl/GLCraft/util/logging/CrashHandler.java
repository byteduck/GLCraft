package net.codepixl.GLCraft.util.logging;

import net.codepixl.GLCraft.GLCraft;

public class CrashHandler implements Thread.UncaughtExceptionHandler{
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		GLCraft.getGLCraft().dispose();
		new CrashHandlerWindow(t,e);
	}
	
	public static void invokeCrash(){
		throw new PurposelyInvokedCrash();
	}
	
	static protected class PurposelyInvokedCrash extends Error{
		
		private static final long serialVersionUID = -4368134279104370836L;
		
	}

}
