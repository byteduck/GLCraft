package net.codepixl.GLCraft.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.codepixl.GLCraft.GLCraft;

public class Mouse {
	
	public static GLFWCursorPosCallback posCallback;
	public static GLFWScrollCallback scrollCallback;
	private static double PX = 0,PY = 0,DX = 0,DY = 0,DWheel = 0,DWheelX = 0;
	private static int x = 0, y = 0;
	
	public static boolean isButtonDown(int button){
		return GLFW.glfwGetMouseButton(GLCraft.getGLCraft().window, button) == GLFW.GLFW_PRESS;
	}
	
	public static void setGrabbed(boolean grabbed){
		if(grabbed)
			GLFW.glfwSetInputMode(GLCraft.getGLCraft().window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		else
			GLFW.glfwSetInputMode(GLCraft.getGLCraft().window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public static boolean isGrabbed() {
		return GLFW.glfwGetInputMode(GLCraft.getGLCraft().window, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}
	
	public static void initMouse(){
		GLFW.glfwSetCursorPosCallback(GLCraft.getGLCraft().window, posCallback = new GLFWCursorPosCallback() {
		    @Override
		    public void invoke(long window, double xpos, double ypos) {
		    	DX = xpos-PX;
		    	DY = ypos-PY;
		    	PX = xpos;
		    	PY = ypos;
		    	x = (int) xpos;
		    	y = (int) ypos;
		    }
		});
		
		GLFW.glfwSetScrollCallback(GLCraft.getGLCraft().window, scrollCallback = new GLFWScrollCallback() {
		    @Override
		    public void invoke(long window, double xoffset, double yoffset) {
		    	DWheel = yoffset;
		    	DWheelX = xoffset;
		    }
		});
	}
	
	public static float getDX(){
		return (float) DX;
	}
	
	public static float getDY(){
		return (float) DY;
	}
	
	public static double getDWheel(){
		return DWheel;
	}
	
	public static double getDWheelX(){
		return DWheelX;
	}

	public static int getY() {
		return Constants.HEIGHT - y;
	}
	
	public static int getX() {
		// TODO Auto-generated method stub
		return x;
	}
}
