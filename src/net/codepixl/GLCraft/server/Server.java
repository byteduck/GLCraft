package net.codepixl.GLCraft.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import net.codepixl.GLCraft.packet.Packet.PacketTooLongException;
import net.codepixl.GLCraft.packet.PacketChunk;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.Chunk;

public class Server {
	ServerSocket socket;
	Socket connectionSocket;
	InputStream in;
	OutputStream out;
	private boolean sentWorld = false;

	public Server(int port) {
		init();
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void init() {
		
	}
	
	public void connect(){
		try {
			System.out.println("Waiting for client...");
			connectionSocket = socket.accept();
			System.out.println("Connected to client!");
			in = connectionSocket.getInputStream();
			out = connectionSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update() {
		if(connectionSocket != null && connectionSocket.isConnected()){
			sendWorld();
		}else if(!connectionSocket.isConnected()){
			System.out.println("Client disconnected. Exiting.");
			System.exit(0);
		}
	}
	
	private void sendWorld(){
		if(!sentWorld){
			Iterator<Chunk> i = Constants.world.getWorldManager().activeChunks.iterator();
			while(i.hasNext()){
				Chunk c = i.next();
				try {
					PacketChunk p = new PacketChunk(c);
					out.write(p.getPacket());
				} catch (PacketTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
