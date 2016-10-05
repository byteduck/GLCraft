package net.codepixl.GLCraft.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.network.packet.Packet;
import net.codepixl.GLCraft.network.packet.PacketBlockChange;
import net.codepixl.GLCraft.network.packet.PacketOnPlace;
import net.codepixl.GLCraft.network.packet.PacketPlayerAction;
import net.codepixl.GLCraft.network.packet.PacketPlayerAdd;
import net.codepixl.GLCraft.network.packet.PacketPlayerLogin;
import net.codepixl.GLCraft.network.packet.PacketPlayerLoginResponse;
import net.codepixl.GLCraft.network.packet.PacketPlayerPos;
import net.codepixl.GLCraft.network.packet.PacketRespawn;
import net.codepixl.GLCraft.network.packet.PacketSendChunk;
import net.codepixl.GLCraft.network.packet.PacketSetBufferSize;
import net.codepixl.GLCraft.network.packet.PacketSetInventory;
import net.codepixl.GLCraft.network.packet.PacketUpdateEntity;
import net.codepixl.GLCraft.network.packet.PacketUtil;
import net.codepixl.GLCraft.network.packet.PacketWorldTime;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayerMP;
import net.codepixl.GLCraft.world.tile.Tile;

public class Server{
	
	public static int DEFAULT_SERVER_PORT = 54567;
	
	public DatagramSocket socket;
	public HashMap<InetAddress, ServerClient> clients;
	public ConnectionRunnable connectionRunnable;
	public Thread connectionThread;
	public WorldManager worldManager;
	
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
		clients = new HashMap<InetAddress, ServerClient>();
		try{
			socket = new DatagramSocket(port);
		}catch(SocketException e){
			e.printStackTrace();
			return false;
		}
		this.worldManager = w;
		GLCraft.getGLCraft().setServer(this);
		connectionRunnable = new ConnectionRunnable(this);
		connectionThread = new Thread(connectionRunnable);
		connectionThread.start();
		System.out.println("[SERVER] Running on port "+port);
		return true;
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
			byte[] bytes = p.getBytes();
			server.send(new DatagramPacket(bytes, bytes.length, addr, port));
		}
	}

	public void handlePacket(DatagramPacket dgp, Packet op){
		try{
			ServerClient c = clients.get(dgp.getAddress());
			if(op instanceof PacketPlayerLogin){
				PacketPlayerLogin p = (PacketPlayerLogin)op;
				EntityPlayerMP mp = new EntityPlayerMP(new Vector3f(), worldManager);
				c = new ServerClient(dgp.getAddress(), dgp.getPort(), this.socket, mp);
				clients.put(c.addr, c);
				this.worldManager.entityManager.add(mp);
				c.writePacket(new PacketPlayerLoginResponse(mp.getID(),mp.getPos()));
				c.writePacket(new PacketWorldTime(worldManager.getWorldTime()));
				sendToAllClients(new PacketPlayerAdd(mp.getID(), p.name, mp.getPos()));
				this.sendChunkPackets();
				System.out.println("[SERVER] New player logged in: "+p.name);
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
			}else if(op instanceof PacketSetBufferSize){
				PacketSetBufferSize p = (PacketSetBufferSize)op;
				if(p.bufferSize <= 1000000){ //Make sure the size is <= 1M (to prevent attacks)
					this.socket.setReceiveBufferSize(p.bufferSize);
				}
			}else if(op instanceof PacketOnPlace){
				PacketOnPlace p = (PacketOnPlace)op;
				Tile.getTile(p.tile).onPlace(p.x, p.y, p.z, p.meta, p.facing, worldManager);
			}else if(op instanceof PacketUpdateEntity){
			}else if(op instanceof PacketSetInventory){
				PacketSetInventory p = (PacketSetInventory)op;
				p.setInventory(worldManager);
			}else if(op instanceof PacketPlayerAction){
				PacketPlayerAction p = (PacketPlayerAction)op;
				Entity e;
				switch(p.type){
				case DROPHELDITEM:
					e = worldManager.getEntityManager().getEntity(p.entityID);
					if(e != null && e instanceof EntityPlayerMP)
						((EntityPlayerMP)e).dropHeldItem(p.all);
					break;
				case DROPOTHERITEM:
					break;
				}
			}else{
				System.err.println("Unhandled Packet: "+op.getClass());
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
	
	public void sendToAllClientsExcept(Packet p, ServerClient c) throws IOException{
		Iterator<Entry<InetAddress, ServerClient>> i = clients.entrySet().iterator();
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
		public void run() {
			while(true){
				try {
					DatagramPacket rec = new DatagramPacket(buf,buf.length);
					server.socket.receive(rec);
					Packet p = PacketUtil.getPacket(rec.getData());
					server.handlePacket(rec, p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void destroy(){
		this.connectionThread.interrupt();
		this.socket.close();
		this.clients.clear();
	}
	
	public int getPort(){
		return socket.getLocalPort();
	}

	public void sendChunkPackets() throws IOException{
		sendToAllClients(new PacketSetBufferSize(1000000));
		sendToAllClients(new PacketSendChunk(Constants.worldLengthChunks*Constants.worldLengthChunks*Constants.worldLengthChunks));
		for(Entry<InetAddress,ServerClient> e : clients.entrySet()){
			ServerClient c = e.getValue();
			List<PacketSendChunk> chunks = worldManager.getChunkPackets(c.player);
			for(PacketSendChunk ch : chunks){
				c.writePacket(ch);
			}
		}
		sendToAllClients(new PacketRespawn());
		sendToAllClients(new PacketSetBufferSize(10000));
	}
}
