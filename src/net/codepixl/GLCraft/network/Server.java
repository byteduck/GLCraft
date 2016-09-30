package net.codepixl.GLCraft.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketPlayerAdd;
import net.codepixl.GLCraft.network.packet.PacketPlayerLogin;
import net.codepixl.GLCraft.network.packet.PacketPlayerLoginResponse;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;

public class Server{
	
	public DatagramSocket server;
	public HashMap<InetAddress, ServerClient> clients;
	public ConnectionRunnable connectionRunnable;
	public Thread connectionThread;
	
	public Server() throws IOException{
		clients = new HashMap<InetAddress, ServerClient>();
		server = new DatagramSocket(4445);
		connectionRunnable = new ConnectionRunnable(this);
		connectionThread = new Thread(connectionRunnable);
		connectionThread.start();
	}
	
	public class ServerClient{
		public InetAddress addr;
		public DatagramSocket server;
		public int port;
		public ServerClient(InetAddress addr, int port, DatagramSocket server){
			this.addr = addr;
			this.server = server;
			this.port = port;
		}
		public void writePacket(Packet p) throws IOException{
			byte[] bytes = p.getBytes();
			server.send(new DatagramPacket(bytes, bytes.length, addr, port));
		}
	}

	public void handlePacket(DatagramPacket dgp, Packet p){
		try{
			if(p instanceof PacketPlayerLogin){ //Player login
				PacketPlayerLogin p2 = (PacketPlayerLogin)p;
				ServerClient c = new ServerClient(dgp.getAddress(), dgp.getPort(), this.server);
				clients.put(c.addr, c);
				c.writePacket(new PacketPlayerLoginResponse(10));
				sendToAllClients(new PacketPlayerAdd(10, p2.name));
				System.out.println("[SERVER] New player logged in: "+p2.name);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendToAllClients(Packet p) throws IOException{
		Iterator<Entry<InetAddress, ServerClient>> i = clients.entrySet().iterator();
		while(i.hasNext()){
			ServerClient next = i.next().getValue();
			next.writePacket(p);
		}
	}
	
	public class ConnectionRunnable implements Runnable{
		
		private Server server;
		private byte[] buf = new byte[1000];
		
		public ConnectionRunnable(Server s){
			this.server = s;
		}
		
		@Override
		public void run() {
			while(true){
				try {
					DatagramPacket rec = new DatagramPacket(buf,buf.length);
					server.server.receive(rec);
					Packet p = PacketUtil.getPacket(rec.getData());
					server.handlePacket(rec, p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
