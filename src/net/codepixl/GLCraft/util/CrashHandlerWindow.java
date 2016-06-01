package net.codepixl.GLCraft.util;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextArea;

public class CrashHandlerWindow extends JFrame{
	public CrashHandlerWindow(Thread t, Throwable e) {
		setTitle("GLCraft Crash");
		setSize(1000,700);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		getContentPane().add(textArea, BorderLayout.CENTER);
		
		String stackTrace = org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e);
		textArea.setText("Well, this is no fun. :(\nGLCraft has crashed in thread: "+t+".\n"+stackTrace);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
