package net.codepixl.GLCraft.Network.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import net.codepixl.GLCraft.Splash;
import net.codepixl.GLCraft.util.Constants;

import org.lwjgl.util.vector.Vector3f;

public class Client implements Runnable{
	
	private String ip;
	private Socket s;
	private OutputStream out;
	private PrintWriter p;
	private BufferedReader br;
	private byte[][][] worldBuf;
	private boolean recievingWorld = false;
	private PipedInputStream packetsToSend = new PipedInputStream();
	
	public Client(String ip){
		this.ip = ip;
	}
	
	@Override
	public void run() {
		try {
			packetsToSend.connect(Constants.packetsToSend);
			s = new Socket(ip,14454);
			this.out = s.getOutputStream();
			this.p = new PrintWriter(out,true);
			p.println("GLCRAFT_CONN_REQUEST");
			System.out.println("SENT CONN REQUEST");
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
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
	
	public void processInput(String str) throws IOException{
		if(str.equals("GLCRAFT_CONN_RESPONSE")){
			p.println("GLCRAFT_WORLD_REQUEST");
		}else
		if(str.equals("GLCRAFT_WORLD_RESPONSE")){
			recievingWorld = true;
			recieveWorld();
		}else{
			Constants.actionsToDo.write(str.getBytes());
		}
	}
	
	public void recieveWorld() throws IOException{
		String str;
		int size = Constants.CHUNKSIZE * Constants.viewDistance;
		worldBuf = new byte[size][size][size];
		Constants.world.getWorldManager().s = new Splash();
		for(int x = 0; x < Constants.CHUNKSIZE * Constants.viewDistance; x++){
			for(int y = 0; y < Constants.CHUNKSIZE * Constants.viewDistance; y++){
				for(int z = 0; z < Constants.CHUNKSIZE * Constants.viewDistance; z++){
					Constants.world.getWorldManager().s.addCurrentTile(1);
					Constants.world.getWorldManager().s.getSplash().setProgress(Constants.world.getWorldManager().s.currentTilePercentage(),"Downloading World...");
					while((str = br.readLine()) == null){}
					worldBuf[x][y][z] = (byte)Integer.parseInt(str);
				}
			}
		}
		Constants.world.getWorldManager().s.getSplash().splashOff();
		Constants.worldBuf = worldBuf;
		recievingWorld = false;
		Constants.downloadedWorld();
	}

}
