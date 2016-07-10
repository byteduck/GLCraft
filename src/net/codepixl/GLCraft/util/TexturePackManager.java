package net.codepixl.GLCraft.util;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

public class TexturePackManager extends JFrame{
	public TexturePackManager() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TexturePackManager.class.getResource("/textures/icons/icon32.png")));
		setTitle("Texture pack manager");
		getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
	}

}
