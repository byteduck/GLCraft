package net.codepixl.GLCraft.render.texturepack;

import net.codepixl.GLCraft.util.Constants;
import net.lingala.zip4j.exception.ZipException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipFile;

public class TexturePackManagerWindow extends JFrame {

	private static final long serialVersionUID = 4365566519505917498L;

	BufferedImage ogIcon;

	JLabel lblPackName;
	final JLabel imageLabel = new JLabel("");

	public TexturePackManagerWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TexturePackManagerWindow.class.getResource("/textures/icons/icon32.png")));
		setTitle("Texture pack manager");

		try {
			ogIcon = ImageIO.read(TexturePackManagerWindow.class.getResourceAsStream("/textures/misc/DefaultRP.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		File tpdir = new File(Constants.GLCRAFTDIR + "Texturepacks");
		File[] tpacks = tpdir.listFiles();
		HashMap<String, BufferedImage> otpacks = new HashMap<String, BufferedImage>();
		if(tpacks !=null){
			for (File tpack : tpacks) {
				String ext = tpack.getName().substring(tpack.getName().lastIndexOf('.') + 1);
				if (ext.equalsIgnoreCase("zip")) {
					String tpname = tpack.getName().substring(0, tpack.getName().lastIndexOf('.'));
					BufferedImage tpicon = null;
					try {
						ZipFile zf = new ZipFile(tpack);
						InputStream in = zf.getInputStream(zf.getEntry("preview.png"));
						tpicon = ImageIO.read(in);
						zf.close();
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
						tpicon = (BufferedImage) ogIcon.getScaledInstance(ogIcon.getWidth(), ogIcon.getHeight(), Image.SCALE_FAST);
					}
					otpacks.put(tpname, tpicon);
				}
			}
		}
		otpacks.put("Default textures", ogIcon);

		JPanel listPanel = new JPanel();

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
					setTexturePack(tpname, tpImage);
				}
			});
			listPanel.add(label);
		}
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new GridLayout(1, 0, 0, 0));

		listPanel.setLayout(new GridLayout(0, 1, 0, 0));

		JScrollPane scrollPane = new JScrollPane(listPanel);
		panel_2.add(scrollPane);

		JPanel panel = new JPanel();
		panel_2.add(panel);
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

		JLabel lblNewLabel = new JLabel("Texture packs are located at: " + Constants.GLCRAFTDIR + "Texturepacks");
		getContentPane().add(lblNewLabel, BorderLayout.SOUTH);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
			fitImage(tpimage, imageLabel);
			lblPackName.setText(tpname);
			TexturePackManager.setTexturePack(tpname);
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
