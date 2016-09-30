package net.codepixl.GLCraft.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketPlayerLogin;
import net.codepixl.GLCraft.network.packet.PacketPlayerLoginResponse;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.world.WorldManager;

public class Client{
	
	public static int DEFAULT_CLIENT_PORT = 54566;
	
	public DatagramSocket socket;
	public ConnectionRunnable connectionRunnable;
	public Thread connectionThread;
	public WorldManager worldManager;
	public ClientServer connectedServer;
	public int port;
	public volatile ServerConnectionState connectionState;
	
	public Client(WorldManager w, int port) throws IOException{
		socket = new DatagramSocket(port);
		this.worldManager = w;
		this.connectionState = new ServerConnectionState();
		connectionRunnable = new ConnectionRunnable(this);
		connectionThread = new Thread(connectionRunnable);
		connectionThread.start();
	}
	
	public void handlePacket(DatagramPacket dgp, Packet op){
		try{
			if(op instanceof PacketPlayerLoginResponse){
				PacketPlayerLoginResponse p = (PacketPlayerLoginResponse)op;
				if(p.accept){
					this.connectionState = new ServerConnectionState(p.entityID);
				}else{
					this.connectionState = new ServerConnectionState(p.message);
				}
			}else{
				//throw new IOException("Invalid Packet "+op.getClass());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ServerConnectionState connectToServer(InetAddress addr, int port) throws IOException{
		
		//Send PacketPlayerLogin
		PacketPlayerLogin ppl = new PacketPlayerLogin(worldManager.getEntityManager().getPlayer().getName());
		byte[] b = ppl.getBytes();
		DatagramPacket dgp = new DatagramPacket(b,b.length,addr,port);
		socket.send(dgp);
		
		while(!connectionState.connected);
		
		return connectionState;
	}
	
	public void sendToServer(Packet p) throws IOException{
		this.connectedServer.sendPacket(p);
	}
	
	public class ConnectionRunnable implements Runnable{
		
		private Client client;
		private byte[] buf = new byte[10000];
		
		public ConnectionRunnable(Client s){
			this.client = s;
		}
		
		@Override
		public void run() {
			while(true){
				try {
					DatagramPacket rec = new DatagramPacket(buf,buf.length);
					client.socket.receive(rec);
					Packet p = PacketUtil.getPacket(rec.getData());
					client.handlePacket(rec, p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public class ClientServer{
		public Client client;
		public InetAddress serverAddr;
		public int serverPort;
		public ClientServer(Client c, InetAddress serverAddr, int serverPort){
			this.client = c;
			this.serverAddr = serverAddr;
			this.serverPort = serverPort;
		}
		
		public void sendPacket(Packet p) throws IOException{
			byte[] b = p.getBytes();
			DatagramPacket dgp = new DatagramPacket(b,b.length);
			dgp.setAddress(serverAddr);
			dgp.setPort(serverPort);
			this.client.socket.send(dgp);
		}
	}
	
	public class ServerConnectionState{
		public boolean success;
		public int entityID;
		public String message;
		public boolean connected = true;
		public ServerConnectionState(String rejectMessage){
			success = false;
			message = rejectMessage;
		}
		public ServerConnectionState(int entityID){
			success = true;
			this.entityID = entityID;
		}
		public ServerConnectionState(){
			this.connected = false;
		}
	}
	
	public void destroy(){
		this.connectionThread.interrupt();
		this.socket.close();
	}
}
