package net.codepixl.GLCraft.GUI;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUIScrollBox;
import net.codepixl.GLCraft.GUI.Elements.GUITextBox;
import net.codepixl.GLCraft.network.Client;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketLANBroadcast;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.logging.GLogger;

public class GUIMultiplayer extends GUIScreen{
	
	public MulticastSocket socket;
	public Thread recvThread;
	public Runnable recvRunnable;
	public GUIScrollBox scrollBox;
	protected ArrayList<Server> servers = new ArrayList<Server>();
	private GUIMultiplayer thisinst;
	private GUITextBox textBox;
	private GUIButton joinButton;
	
	private static final int JOINX = (int) (Constants.WIDTH*0.7);
	private static final int JOINY = (int) (Constants.HEIGHT*0.925);
	private static final int TEXTBOXY = (int) (Constants.HEIGHT*0.925);
	private static final int TEXTBOXX = (int) (Constants.HEIGHT*0.3);
	
	public GUIMultiplayer(){
		thisinst = this;
		this.setDrawStoneBackground(true);
		try{
			socket = new MulticastSocket(5656);
			InetAddress group = InetAddress.getByName("238.245.233.230");
			socket.joinGroup(group);
		}catch (IOException e){
			e.printStackTrace();
		}
		
		scrollBox = new GUIScrollBox(10);
		scrollBox.x = 100;
		scrollBox.y = Constants.FONT.getHeight()*2+20;
		scrollBox.width = Constants.WIDTH-200;
		scrollBox.height = (int) (Constants.HEIGHT*0.75f-scrollBox.y);
		
		recvRunnable = new RecvThread(scrollBox);
		
		final String tbp = "   Enter Server Address   ";
		int tbtwidth = Constants.FONT.getWidth(tbp);
		
		textBox = new GUITextBox(TEXTBOXX, TEXTBOXY, tbtwidth, tbp);
		textBox.y-=textBox.height/2;
		
		joinButton = new GUIButton("Connect to server", JOINX, JOINY, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try{
					String addrs = textBox.getText();
					int port = net.codepixl.GLCraft.network.Server.DEFAULT_SERVER_PORT;
					if(addrs == null || addrs.trim().equals("")){}else{
						if(addrs.contains(":")){
							port = Integer.parseInt(addrs.substring(addrs.indexOf(":")+1));
							addrs = addrs.substring(0, addrs.indexOf(":"));
						}
						InetAddress addr = InetAddress.getByName(addrs);
						if(addr != null){
							Constants.setState(Constants.GAME);
							GLCraft.getGLCraft().getWorldManager(false).createBlankWorld();
							Client.ServerConnectionState cs = GLCraft.getGLCraft().getCentralManager(false).connectToServer(addr, port);
							if(!cs.success){
								Constants.setState(Constants.START_SCREEN);
								GUIManager.getMainManager().showGUI(new GUIServerError("Error connecting to server: ",cs.message));
								return null;
							}
							glDisable(GL_TEXTURE_2D);
							((GUIPauseMenu)GUIManager.getMainManager().getGUI("pauseMenu")).setHost(false);
							GUIManager.getMainManager().closeGUI(false);
						}
					}
				}catch(UnknownHostException | IndexOutOfBoundsException | NumberFormatException e){}
				return null;
			}
		});
		
		addElements(scrollBox, textBox, joinButton);
	}
	
	@Override
	public void onOpen(){
		super.onOpen();
		scrollBox.clearItems();
		servers.clear();
		recvThread = new Thread(recvRunnable, "LAN World Discovery");
		recvThread.start();
	}
	
	@Override
	public void onClose(){
		GUIManager.getMainManager().showGUI("startScreen");
		recvThread.interrupt();
	}
	
	protected class Server{
		private InetAddress addr;
		private int port;
		private String msg;

		private Server(InetAddress addr, int port, String msg){
			this.addr = addr;
			this.port = port;
			this.msg = msg;
		}
		
		@Override
		public boolean equals(Object equ){
			if(equ instanceof Server){
				return ((Server) equ).addr.equals(addr) && ((Server) equ).port == port;
			}else
				return false;
		}
	}
	
	public class RecvThread implements Runnable{
		private GUIScrollBox sbox;
		private byte[] buf = new byte[1000];
		
		public RecvThread(GUIScrollBox sbox){
			this.sbox = sbox;
		}

		@Override
		public void run() {
			while(!Thread.interrupted()){
				try {
					DatagramPacket pckt = new DatagramPacket(buf, buf.length);
					socket.receive(pckt);
					Packet p = PacketUtil.getPacket(pckt.getData());
					if(p instanceof PacketLANBroadcast){
						String msg = ((PacketLANBroadcast)p).msg;
						int port = ((PacketLANBroadcast)p).port;
						Server s = new Server(pckt.getAddress(), port, msg);
						if(!servers.contains(s)){
							servers.add(s);
							GUIServer gui = new GUIServer(thisinst, msg, pckt.getAddress(), port);
							gui.x = 10;
							gui.width = Constants.WIDTH-240;
							sbox.addItem(gui);
						}
					}
				} catch (IOException | ClassCastException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
