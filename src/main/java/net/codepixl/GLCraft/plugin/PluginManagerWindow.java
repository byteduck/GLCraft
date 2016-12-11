package net.codepixl.GLCraft.plugin;

import net.codepixl.GLCraft.util.Constants;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.util.Iterator;

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
