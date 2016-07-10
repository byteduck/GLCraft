package net.codepixl.GLCraft.render.texturepack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.codepixl.GLCraft.util.Constants;
import net.lingala.zip4j.exception.ZipException;

public class TexturePackManagerWindow extends JFrame {
	BufferedImage ogIcon;
	
	JLabel lblPackName;
	final JLabel imageLabel = new JLabel("");

	public TexturePackManagerWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TexturePackManagerWindow.class.getResource("/textures/icons/icon32.png")));
		setTitle("Texture pack manager");
		getContentPane().setLayout(new GridLayout(0, 2, 0, 0));

		try {
			ogIcon = ImageIO.read(TexturePackManagerWindow.class.getResourceAsStream("/textures/misc/DefaultRP.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JPanel listPanel = new JPanel();

		File tpdir = new File(Constants.GLCRAFTDIR + "Texturepacks");
		File[] tpacks = tpdir.listFiles();
		HashMap<String, BufferedImage> otpacks = new HashMap<String, BufferedImage>();
		for (File tpack : tpacks) {
			String ext = tpack.getName().substring(tpack.getName().lastIndexOf('.') + 1);
			if (ext.equalsIgnoreCase("zip")) {
				String tpname = tpack.getName().substring(0, tpack.getName().lastIndexOf('.'));
				BufferedImage tpicon = null;
				try {
					ZipFile zf = new ZipFile(tpack);
					InputStream in = zf.getInputStream(zf.getEntry("preview.png"));
					tpicon = ImageIO.read(in);
				} catch (IOException e) {
					e.printStackTrace();
					tpicon = (BufferedImage) ogIcon.getScaledInstance(ogIcon.getWidth(), ogIcon.getHeight(), Image.SCALE_FAST);
				}
				otpacks.put(tpname, tpicon);
			}
		}
		
		otpacks.put("Default textures", ogIcon);

		listPanel.setLayout(new GridLayout(0, 1, 0, 0));

		if (otpacks.size() > 0) {
			listPanel.setLayout(new GridLayout(otpacks.size(), 0, 0, 0));
		}

		Iterator<String> i = otpacks.keySet().iterator();
		while (i.hasNext()) {
			final String tpname = i.next();
			final BufferedImage tpImage = otpacks.get(tpname);
			JLabel label = new JLabel(tpname);
			label.setIcon(new ImageIcon(tpImage.getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
			label.setBorder(BorderFactory.createLineBorder(Color.black));
			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					setTexturePack(tpname,tpImage);
				}
			});
			listPanel.add(label);
		}

		JScrollPane scrollPane = new JScrollPane(listPanel);
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

		lblPackName = new JLabel("Default textures");
		lblPackName.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblPackName);

		
		panel.add(imageLabel, BorderLayout.CENTER);

		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		File tpi = new File(Constants.GLCRAFTDIR + "Texturepacks/currentTP.txt");
		if (tpi.exists()) {
			try {
				String tpis = new String(Files.readAllBytes(tpi.toPath()));
				if (!tpis.equals("none")) {
					lblPackName.setText(tpis);
					File tpp = new File(System.getProperty("user.home") + "/GLCraft/Texturepacks/tmp/textures/preview.png");
					if (tpp.exists()) {
						ogIcon = ImageIO.read(tpp);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		fitImage(ogIcon, imageLabel);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				fitImage(ogIcon, imageLabel);
			}
		});
	}
	


	private void setTexturePack(String tpname, BufferedImage tpimage) {
		try {
			ogIcon = tpimage;
			fitImage(tpimage,imageLabel);
			lblPackName.setText(tpname);
			if(tpname.equals("Default textures")){
				new File(System.getProperty("user.home")+"/GLCraft/Texturepacks/currentTP.txt").deleteOnExit();
				JOptionPane.showMessageDialog(null, "You must restart GLCraft for changes to take effect.");
			}else{
				TexturePackManager.setTexturePack(tpname);
			}
		} catch (FileNotFoundException | UnsupportedEncodingException | ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void fitImage(BufferedImage icon, JLabel label) {
		if (label.getWidth() > label.getHeight()) {
			label.setIcon(new ImageIcon(icon.getScaledInstance(label.getHeight(), label.getHeight(), Image.SCALE_DEFAULT)));
		} else {
			label.setIcon(new ImageIcon(icon.getScaledInstance(label.getWidth(), label.getWidth(), Image.SCALE_DEFAULT)));
		}
	}

}
