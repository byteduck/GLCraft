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
import net.codepixl.GLCraft.network.packet.PacketBlockChange;
import net.codepixl.GLCraft.network.packet.PacketPlayerAdd;
import net.codepixl.GLCraft.network.packet.PacketPlayerLogin;
import net.codepixl.GLCraft.network.packet.PacketPlayerLoginResponse;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;

public class Server{
	
	public static int SERVER_PORT = 54567;
	
	public DatagramSocket server;
	public HashMap<InetAddress, ServerClient> clients;
	public ConnectionRunnable connectionRunnable;
	public Thread connectionThread;
	public WorldManager worldManager;
	
	public Server(WorldManager w) throws IOException{
		clients = new HashMap<InetAddress, ServerClient>();
		server = new DatagramSocket(54567);
		this.worldManager = w;
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

	public void handlePacket(DatagramPacket dgp, Packet op){
		try{
			if(op instanceof PacketPlayerLogin){
				PacketPlayerLogin p = (PacketPlayerLogin)op;
				ServerClient c = new ServerClient(dgp.getAddress(), dgp.getPort(), this.server);
				clients.put(c.addr, c);
				EntityPlayerMP mp = new EntityPlayerMP(0,100f,0, worldManager);
				this.worldManager.entityManager.add(mp);
				c.writePacket(new PacketPlayerLoginResponse(mp.getID(),mp.getPos()));
				sendToAllClients(new PacketPlayerAdd(mp.getID(), p.name, mp.getPos()));
				System.out.println("[SERVER] New player logged in: "+p.name);
			}else if(op instanceof PacketBlockChange){
				PacketBlockChange p = (PacketBlockChange)op;
				sendToAllClients(p);
				worldManager.setTileAtPos(p.x, p.y, p.z, p.id, p.source, true, p.meta);
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
