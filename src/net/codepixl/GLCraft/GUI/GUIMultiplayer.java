package net.codepixl.GLCraft.GUI;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import net.codepixl.GLCraft.GUI.Elements.GUIScrollBox;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketLANBroadcast;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.util.Constants;

public class GUIMultiplayer extends GUIScreen{
	
	public MulticastSocket socket;
	public Thread recvThread;
	public Runnable recvRunnable;
	public GUIScrollBox scrollBox;
	protected ArrayList<Server> servers = new ArrayList<Server>();
	private GUIMultiplayer thisinst;
	
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
		
		addElement(scrollBox);
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
