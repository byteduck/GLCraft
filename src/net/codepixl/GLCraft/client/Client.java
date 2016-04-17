package net.codepixl.GLCraft.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	Socket socket;
	InputStream in;
	OutputStream out;

	public Client(int port) {
		init();
		try {
			socket = new Socket("localhost",port);
			System.out.println("Connected to server!");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void init() {
		
	}
	
	public void connect(){
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update() {
		if(socket != null && socket.isConnected()){
			
		}else if(!socket.isConnected()){
			System.out.println("Server disconnected. Exiting.");
			System.exit(0);
		}
	}
	
	public void disconnect(){
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
