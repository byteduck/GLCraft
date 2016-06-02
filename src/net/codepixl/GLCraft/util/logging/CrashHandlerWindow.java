package net.codepixl.GLCraft.util.logging;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CrashHandlerWindow extends JFrame{
	public CrashHandlerWindow(Thread t, Throwable e) {
		setTitle("GLCraft Crash");
		setSize(1000,700);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		String stackTrace = org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e);
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText("Well, this is no fun. :(\n\n"+
				"GLCraft has crashed in thread: "+
				t+
				"\n\n"+
				stackTrace+
				"\nAdditional diagnostic information:\n"+
				"Max Memory Available to JVM: "+Runtime.getRuntime().maxMemory()/1000000+"MB\n"+
				"Total Memory Available to JVM: "+Runtime.getRuntime().totalMemory()/1000000+"MB\n"+
				"Free Memory Available to JVM: "+Runtime.getRuntime().freeMemory()/1000000+"MB\n"+
				"Operating System: "+System.getProperty("os.name")+"\n\n"+
				"--------------------------------\n"+
				"           LOG BELOW            \n"+
				"--------------------------------\n\n");
		try {
			textArea.append(new String(Files.readAllBytes(new File(System.getProperty("user.home")+"/GLCraft/GLCraft.log").toPath())));
		} catch (IOException e1) {
			textArea.append("There was an error fetching the log:\n"+org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e1));
		}
		JScrollPane scrollPane = new JScrollPane(textArea);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		System.err.println(stackTrace);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
