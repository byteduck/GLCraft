package net.codepixl.GLCraft.util;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextPane;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class TexturePackManager extends JFrame{
	public TexturePackManager() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TexturePackManager.class.getResource("/textures/icons/icon32.png")));
		setTitle("Texture pack manager");
		getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new GridLayout(2, 0, 0, 0));
		
		JLabel lblSelectedTexturePack = new JLabel("Selected texture pack:");
		lblSelectedTexturePack.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblSelectedTexturePack);
		
		JLabel lblDefaultTextures = new JLabel("Default textures");
		lblDefaultTextures.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblDefaultTextures);
		
		JButton btnOpenTexturePack = new JButton("Open texture pack folder");
		panel.add(btnOpenTexturePack, BorderLayout.SOUTH);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setIcon(new ImageIcon(TexturePackManager.class.getResource("/textures/icons/icon32.png")));
		panel.add(label, BorderLayout.CENTER);
	}

}
