package net.codepixl.GLCraft.util.data.saves;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;

public class SaveLoadWindow extends JFrame{
	WorldManager worldManager;
	volatile String worldName;
	
	public static String loadWorld(WorldManager w){
		SaveLoadWindow s = new SaveLoadWindow(w);
		while(s.worldName.equals("")){
			
		}
		return s.worldName;
	}
	
	public SaveLoadWindow(WorldManager w){
		this.worldManager = w;
		this.worldName = "";
		
		JLabel lblSavedWorlds = new JLabel("Saved Worlds");
		lblSavedWorlds.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblSavedWorlds, BorderLayout.NORTH);
		
		JPanel listPanel = new JPanel();
		
		JScrollPane scrollPane = new JScrollPane(listPanel);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		File savesDir = new File(Constants.GLCRAFTDIR+"saves/");
		final JFrame jf = this;
		if(savesDir.exists()){
			ArrayList<String> saves = new ArrayList<String>();
			for(File f : savesDir.listFiles()){
				if(new File(f,"/player.nbt").exists()){
					saves.add(f.getName());
				}
			}
			
			if(saves.size() > 0){
				listPanel.setLayout(new GridLayout(saves.size(), 0, 0, 0));
				for(final String s : saves){
					JPanel jp = new JPanel();
					jp.setLayout(new BorderLayout());
					JLabel j = new JLabel(s);
					jp.add(j, BorderLayout.WEST);
					JButton jb = new JButton("Load");
					jb.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							jf.setVisible(false);
							loadWorld(s);
						}
					});
					jp.add(jb, BorderLayout.EAST);
					listPanel.add(jp);
				}
			}
		}

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Load World");
		this.setSize(640,480);
		this.setVisible(true);
		
	}
	
	public void loadWorld(String s){
		worldName = s;
	}
}
