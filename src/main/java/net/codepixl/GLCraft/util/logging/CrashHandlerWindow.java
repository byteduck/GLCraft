package net.codepixl.GLCraft.util.logging;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CrashHandlerWindow extends JFrame{
	public CrashHandlerWindow(Thread t, Throwable e) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(CrashHandlerWindow.class.getResource("/textures/icons/icon32.png")));
		setTitle("GLCraft Crash");
		setSize(1000,700);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		String stackTrace = org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e);
		if(e instanceof CrashHandler.PurposelyInvokedCrash){
			stackTrace = "This was a purposely invoked crash.\n";
		}
		final JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText("Well, this is no fun. :(\n\n"+
				"GLCraft has crashed in thread: "+
				t+
				"\n\nNOTE: If you are launching this without the launcher from your desktop on Linux, it will not work. Use any other folder.\n\n"+
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
		
		JButton btnNewButton = new JButton("Copy text and open webpage to report issue");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StringSelection stringSelection = new StringSelection(textArea.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
				JOptionPane.showMessageDialog(null, "The error was copied. After you click okay, a website will open where you can report the bug.\nPaste the error into the \"details\" box\n(And maybe write some details about what you were doing when the crash happpened as well)\n and fill out the rest of the form.", "Text copied", JOptionPane.INFORMATION_MESSAGE);
				try {
					Desktop.getDesktop().browse(new URI("https://gitreports.com/issue/Codepixl/GLCraft"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "There was an error opening the bug report page.\n Go to\nhttps://gitreports.com/issue/Codepixl/GLCraft\ntoreport the issue.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "There was an error opening the bug report page.\n Go to\nhttps://gitreports.com/issue/Codepixl/GLCraft\ntoreport the issue.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		getContentPane().add(btnNewButton, BorderLayout.SOUTH);
		
		System.err.println(stackTrace);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
