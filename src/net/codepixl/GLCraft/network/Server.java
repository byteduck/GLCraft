package net.codepixl.GLCraft.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.GUIManager;
import net.codepixl.GLCraft.GUI.GUIPauseMenu;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketBlockChange;
import net.codepixl.GLCraft.network.packet.PacketLANBroadcast;
import net.codepixl.GLCraft.network.packet.PacketOnPlace;
import net.codepixl.GLCraft.network.packet.PacketPlayerAction;
import net.codepixl.GLCraft.network.packet.PacketPlayerAdd;
import net.codepixl.GLCraft.network.packet.PacketPlayerDead;
import net.codepixl.GLCraft.network.packet.PacketPlayerLeave;
import net.codepixl.GLCraft.network.packet.PacketPlayerLogin;
import net.codepixl.GLCraft.network.packet.PacketPlayerLoginResponse;
import net.codepixl.GLCraft.network.packet.PacketPlayerPos;
import net.codepixl.GLCraft.network.packet.PacketReady;
import net.codepixl.GLCraft.network.packet.PacketRequestChunk;
import net.codepixl.GLCraft.network.packet.PacketSendChunk;
import net.codepixl.GLCraft.network.packet.PacketServerClose;
import net.codepixl.GLCraft.network.packet.PacketSetInventory;
import net.codepixl.GLCraft.network.packet.PacketUpdateEntity;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.network.packet.PacketWorldTime;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.LogSource;
import net.codepixl.GLCraft.util.Vector3i;
import net.codepixl.GLCraft.util.logging.GLogger;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.tile.Tile;

public class Server{
	
	public static int DEFAULT_SERVER_PORT = 54567;
	
	public DatagramSocket socket;
	public HashMap<InetAddressAndPort, ServerClient> clients;
	public ConnectionRunnable connectionRunnable;
	public Thread connectionThread;
	public WorldManager worldManager;
	private int port;
	public boolean lanWorld;
	public ServerClient host;
	public boolean broadcast = true;
	private Thread broadcastThread;
	private BroadcastRunnable broadcastRunnable;
	
	public Server(WorldManager w, int port) throws IOException{
		if(!commonInit(w,port)){throw new IOException("Error binding to port");}
	}
	
	public Server(WorldManager w) throws IOException{
		int i = 0;
		while(!commonInit(w,Constants.randInt(5400, 6000)) && i < 50){i++;}
		if(i >= 50)
			throw new IOException("Error binding to all tried ports");
	}
	
	private boolean commonInit(WorldManager w, int port){
		clients = new HashMap<InetAddressAndPort, ServerClient>();
		try{
			socket = new DatagramSocket(port);
		}catch(SocketException e){
			e.printStackTrace();
			return false;
		}
		this.port = port;
		this.worldManager = w;
		GLCraft.getGLCraft().setServer(this);
		connectionRunnable = new ConnectionRunnable(this);
		connectionThread = new Thread(connectionRunnable, "Server Thread");
		connectionThread.start();
		GLogger.log("Running on port "+port, LogSource.SERVER);
		try{
			broadcastRunnable = new BroadcastRunnable(this, "", port);
		}catch (IOException e){
			e.printStackTrace();
		}
		if(!GLCraft.getGLCraft().isServer()){
			((GUIPauseMenu)GUIManager.getMainManager().getGUI("pauseMenu")).setHost(true);
		}
		return true;
	}
	
	public void setBroadcast(String name){
		PacketLANBroadcast plb = new PacketLANBroadcast(name,port);
		try {
			broadcastRunnable.packet = new DatagramPacket(plb.getBytes(), plb.getBytes().length, broadcastRunnable.group, 5656);
			broadcastThread = new Thread(broadcastRunnable, "Broadcast Thread");
			broadcastThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class ServerClient{
		public InetAddress addr;
		public DatagramSocket server;
		public int port;
		public EntityPlayerMP player;
		public ServerClient(InetAddress addr, int port, DatagramSocket server, EntityPlayerMP player){
			this.addr = addr;
			this.server = server;
			this.port = port;
			this.player = player;
		}
		public void writePacket(Packet p) throws IOException{
			if(!server.isClosed()){
				byte[] bytes = p.getBytes();
				server.send(new DatagramPacket(bytes, bytes.length, addr, port));
			}
		}
	}

	public void handlePacket(DatagramPacket dgp, Packet op){
		try{
			ServerClient c = clients.get(new InetAddressAndPort(dgp.getAddress(), dgp.getPort()));
			if(op instanceof PacketPlayerLogin){
				PacketPlayerLogin p = (PacketPlayerLogin)op;
				EntityPlayerMP mp = new EntityPlayerMP(new Vector3f(), worldManager);
				mp.setName(p.name);
				c = new ServerClient(dgp.getAddress(), dgp.getPort(), this.socket, mp);
				if(lanWorld && host == null)
					host = c;
				clients.put(new InetAddressAndPort(c.addr, c.port), c);
				this.worldManager.entityManager.add(mp);
				c.writePacket(new PacketPlayerLoginResponse(mp.getID(),mp.getPos()));
				try{
					Thread.sleep(100); //Make sure the packetplayerloginresponse gets there first
				}catch (InterruptedException e){
					e.printStackTrace();
				}
				c.writePacket(new PacketWorldTime(worldManager.getWorldTime()));
				for(Entry<InetAddressAndPort, ServerClient> entry : clients.entrySet()){
					ServerClient c2 = entry.getValue();
					c.writePacket(new PacketPlayerAdd(c2.player.getID(), c2.player.getName(), c2.player.getPos()));
				}
				sendToAllClientsExcept(new PacketPlayerAdd(c.player.getID(), c.player.getName(), c.player.getPos()), c);
				c.writePacket(new PacketSendChunk(worldManager.getActiveChunks().size()));
				GLogger.log("New player logged in: "+p.name, LogSource.SERVER);
			}else if(op instanceof PacketBlockChange){
				PacketBlockChange p = (PacketBlockChange)op;
				sendToAllClients(p);
				if(!p.justMeta)
					worldManager.setTileAtPos(p.x, p.y, p.z, p.id, p.source, false, p.meta);
				else
					worldManager.setMetaAtPos(p.x, p.y, p.z, p.meta, false, false);
			}else if(op instanceof PacketPlayerPos){
				PacketPlayerPos p = (PacketPlayerPos)op;
				sendToAllClientsExcept(new PacketPlayerPos(p),c);
				c.player.getPos().set(p.pos[0], p.pos[1], p.pos[2]);
				c.player.getRot().set(p.rot[0], p.rot[1], p.rot[2]);
				c.player.getVel().set(p.vel[0], p.vel[1], p.vel[2]);
			}else if(op instanceof PacketOnPlace){
				PacketOnPlace p = (PacketOnPlace)op;
				Tile.getTile(p.tile).onPlace(p.x, p.y, p.z, p.meta, p.facing, worldManager);
			}else if(op instanceof PacketUpdateEntity){
			}else if(op instanceof PacketSetInventory){
				PacketSetInventory p = (PacketSetInventory)op;
				p.setInventory(worldManager);
			}else if(op instanceof PacketPlayerAction){
				PacketPlayerAction p = (PacketPlayerAction)op;
				EntityPlayerMP player = c.player;
				switch(p.type){
				case DROPHELDITEM:
					player.dropHeldItem(p.all);
					break;
				case DROPOTHERITEM:
					break;
				}
			}else if(op instanceof PacketPlayerDead){
				c.player.setDead(true);
			}else if(op instanceof PacketReady){
				c.player.shouldUpdate = true;
			}else if(op instanceof PacketPlayerLeave){
				clients.remove(new InetAddressAndPort(c.addr,c.port));
				GLogger.log("Player Logged out: "+c.player.getName(), LogSource.SERVER);
				worldManager.getEntityManager().remove(c.player);
				sendToAllClients(new PacketPlayerLeave(c.player.getID()));
			}else if(op instanceof PacketRequestChunk){
				Vector3i pos = ((PacketRequestChunk) op).pos;
				Chunk ch = worldManager.getChunk(pos);
				c.writePacket(new PacketSendChunk(ch, c.player));
			}else{
				GLogger.logerr("Unhandled Packet: "+op.getClass(), LogSource.SERVER);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendToAllClients(Packet p) throws IOException{
		Iterator<Entry<InetAddressAndPort, ServerClient>> i = clients.entrySet().iterator();
		while(i.hasNext()){
			ServerClient next = i.next().getValue();
			next.writePacket(p);
		}
	}
	
	public void sendToAllClientsExcept(Packet p, ServerClient c) throws IOException{
		Iterator<Entry<InetAddressAndPort, ServerClient>> i = clients.entrySet().iterator();
		while(i.hasNext()){
			ServerClient next = i.next().getValue();
			if(next != c)
				next.writePacket(p);
		}
	}
	
	public class ConnectionRunnable implements Runnable{
		
		private Server server;
		private byte[] buf = new byte[10000];
		
		public ConnectionRunnable(Server s){
			this.server = s;
		}
		
		@Override
		public void run(){
			while(!Thread.interrupted()){
				try {
					DatagramPacket rec = new DatagramPacket(buf,buf.length);
					server.socket.receive(rec);
					Packet p = PacketUtil.getPacket(rec.getData());
					server.handlePacket(rec, p);
				}catch (IOException e){
					if(!server.socket.isClosed())
						e.printStackTrace();
				}
			}
		}
		
	}
	
	private class BroadcastRunnable implements Runnable{
		
		public InetAddress group;
		private DatagramPacket packet;
		private Server server;
		private MulticastSocket socket;
		
		public BroadcastRunnable(Server s, String msg, int port) throws IOException{
			PacketLANBroadcast plb = new PacketLANBroadcast(msg,port);
			this.server = s;
			socket = new MulticastSocket(5656);
			group = InetAddress.getByName("238.245.233.230");
			socket.joinGroup(group);
			byte[] pckt = plb.getBytes();
			packet = new DatagramPacket(pckt, pckt.length, group, 5656);
		}
		
		@Override
		public void run(){
			while(!Thread.interrupted()){
				try{
					Thread.sleep(1000);
					if(server.broadcast){
						socket.send(packet);
					}
				}catch (InterruptedException e){
					//e.printStackTrace();
				}catch(IOException e){
					if(!server.socket.isClosed())
						e.printStackTrace();
				}
			}
		}
	}
	
	public void destroy(){
		this.socket.close();
		this.clients.clear();
		this.connectionThread.interrupt();
		broadcastThread.interrupt();
		if(!GLCraft.getGLCraft().isServer()){
			((GUIPauseMenu)GUIManager.getMainManager().getGUI("pauseMenu")).setHost(false);
		}
	}
	
	public void reinit(){
		clients.clear();
		this.connectionThread = new Thread(connectionRunnable, "Server Thread");
		try {
			this.socket = new DatagramSocket(this.getPort());
			connectionThread.start();
			if(!GLCraft.getGLCraft().isServer()){
				((GUIPauseMenu)GUIManager.getMainManager().getGUI("pauseMenu")).setHost(true);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public int getPort(){
		return port;
	}

	public void close(String reason) throws IOException{
		sendToAllClients(new PacketServerClose(reason));
		socket.close();
		connectionThread.interrupt();
		broadcastThread.interrupt();
		if(!GLCraft.getGLCraft().isServer()){
			((GUIPauseMenu)GUIManager.getMainManager().getGUI("pauseMenu")).setHost(false);
		}
	}
	
	public void close() throws IOException{
		close("Server Closing");
	}

	public void sendToClient(Packet p, EntityPlayerMP mp) throws IOException{
		for(Entry<InetAddressAndPort, ServerClient> sc : clients.entrySet()){
			ServerClient c = sc.getValue();
			if(c.player == mp)
				c.writePacket(p);
		}
	}
	
	public class InetAddressAndPort{
		public InetAddress addr;
		public int port;

		public InetAddressAndPort(InetAddress addr, int port){
			this.addr = addr;
			this.port = port;
		}
		
		@Override
		public boolean equals(Object equ){
			if(equ instanceof InetAddressAndPort)
				return ((InetAddressAndPort) equ).addr.equals(addr) && ((InetAddressAndPort) equ).port == port;
			else
				return false;
		}
		
		@Override
		public int hashCode(){
			return addr.hashCode()*port;
		}
	}

	public boolean isOpen() {
		return !socket.isClosed();
	}
}
