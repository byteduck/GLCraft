package net.codepixl.GLCraft.util.logging;

import com.nishu.utils.Window;

public class CrashHandler implements Thread.UncaughtExceptionHandler{
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Window.dispose();
		new CrashHandlerWindow(t,e);
	}
	
	public static void invokeCrash(){
		throw new PurposelyInvokedCrash();
	}
	
	static protected class PurposelyInvokedCrash extends Error{
		
		private static final long serialVersionUID = -4368134279104370836L;
		
	}

}
