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

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class PluginManagerWindow extends JFrame{
	DefaultMutableTreeNode top;
	PluginManager p;
	private final JButton btnNewButton = new JButton("Add new plugin");
	public PluginManagerWindow(PluginManager p) {
		getContentPane().setBackground(Color.WHITE);
		this.p = p;
		this.setSize(356, 249);
		top = new DefaultMutableTreeNode("Plugins");
		addPlugins();
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jf = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Compressed GLCraft Plugins", "glcp", "zip", "jar");
				jf.setFileFilter(filter);
				int returnVal = jf.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION){
					File plugin = jf.getSelectedFile();
					File tempFolder = new File(new File(PluginManager.path).getParentFile(),"temp");
					try {
						Files.deleteIfExists(tempFolder.toPath());
						ZipFile zip = new ZipFile(plugin);
						zip.extractAll(tempFolder.getAbsolutePath());
						byte[] data = Files.readAllBytes(new File(tempFolder,"plugin.json").toPath());
						String jsonString = new String(data,StandardCharsets.UTF_8);
						JSONObject j = new JSONObject(jsonString);
						String name = j.getString("pluginName");
						Files.move(tempFolder.toPath(), new File(PluginManager.path, name).toPath(), StandardCopyOption.REPLACE_EXISTING);
						JOptionPane.showMessageDialog(null, "Plugin added successfully.", "GLCraft", JOptionPane.PLAIN_MESSAGE);
					} catch (ZipException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "There was an error extracting the plugin.", "GLCraft", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "There was an error installing the plugin.\nMake sure to remove existing plugins with the same name first.", "GLCraft", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		getContentPane().add(btnNewButton, BorderLayout.NORTH);
		
		JLabel lblRestart = new JLabel("You must restart GLCraft for changes to take effect.");
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
		this.setAlwaysOnTop(true);
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

}
