package net.codepixl.GLCraft.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.json.JSONObject;

import net.codepixl.GLCraft.util.Constants;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class PluginManagerWindow extends JFrame{
	DefaultMutableTreeNode top;
	PluginManager p;
	public PluginManagerWindow(PluginManager p) {
		getContentPane().setBackground(Color.WHITE);
		this.p = p;
		this.setSize(356, 249);
		top = new DefaultMutableTreeNode("Plugins");
		addPlugins();
		
		JLabel lblRestart = new JLabel("Put plugins into "+Constants.GLCRAFTDIR+"plugins.");
		getContentPane().add(lblRestart, BorderLayout.SOUTH);
		lblRestart.setBackground(Color.WHITE);
		lblRestart.setForeground(Color.RED);
		lblRestart.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree(top);
		panel.add(tree);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private void addPlugins(){
		Iterator<LoadedPlugin> it = p.loadedPlugins.iterator();
		while(it.hasNext()){
			LoadedPlugin p = it.next();
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(p.name +" ver "+p.version);
			node.add(new DefaultMutableTreeNode(p.description));
			top.add(node);
		}
	}
	
	void purgeDirectory(File dir) {
		if(dir.exists()){
		    for (File file: dir.listFiles()) {
		        if (file.isDirectory()) purgeDirectory(file);
		        file.delete();
		    }
		}
	}

}
