package net.codepixl.GLCraft.Network.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import net.codepixl.GLCraft.util.Constants;
/** TODO MULTIPLAYER ADDED 1038 LINES OF CODE! (ABOUT 1/3 OF THE TOTAL CODE CHANGED!) **/

public class Server implements Runnable{
	
	private ServerSocket s;
	private Socket c;
	private String ip;
	private OutputStream os;
	private PrintWriter p;
	private BufferedReader br;
	private PipedInputStream packetsToSend = new PipedInputStream();
	
	
	public Server(String ip){
		this.ip = ip;
	}
	
	@Override
	public void run() {
		try {
			packetsToSend.connect(Constants.packetsToSend);
			s = new ServerSocket(14454);
			c = s.accept();
			os = c.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			p = new PrintWriter(c.getOutputStream(),true);
			String str;
			while(true){
				while (br.ready()) {
					str = br.readLine();
					processInput(str);
				}
				while(packetsToSend.available() > 0){
					byte ByteBuf[] = new byte[1500];
					packetsToSend.read(ByteBuf);
					ByteBuf = Constants.trim(ByteBuf);
					String s = new String(ByteBuf);
					p.println(s);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendWorld() throws IOException{
		p.println("GLCRAFT_WORLD_RESPONSE");
		for(int x = 0; x < Constants.CHUNKSIZE * Constants.viewDistance; x++){
			for(int y = 0; y < Constants.CHUNKSIZE * Constants.viewDistance; y++){
				for(int z = 0; z < Constants.CHUNKSIZE * Constants.viewDistance; z++){
					p.println(Constants.world.getWorldManager().getTileAtPos(x, y, z));
				}
			}
		}
		Constants.setState(Constants.GAME);
	}
	
	public void processInput(String str) throws IOException{
		if(str.equals("GLCRAFT_WORLD_REQUEST")){
			sendWorld();
		}else
		if(str.equals("GLCRAFT_CONN_REQUEST")){
			System.out.println("SENDING CONN RESPONSE");
			p.println("GLCRAFT_CONN_RESPONSE");
		}else{
			Constants.actionsToDo.write(str.getBytes());
		}
	}

}
