package net.codepixl.GLCraft.Network.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.codepixl.GLCraft.util.Constants;

public class ServerBroadcast implements Runnable {

	DatagramSocket socket;
	boolean connected = false;
	
	@Override
	public void run() {
		try {
			// Keep a socket open to listen to all the UDP trafic that is
			// destined for this port
			socket = new DatagramSocket(14454, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {

				// Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				// See if the packet holds the right command (message)
				String message = new String(packet.getData()).trim();
				if (message.equals("GLCRAFT_SERVER_REQUEST")) {
					byte[] sendData;
					
					sendData = "GLCRAFT_SERVER_RESPONSE".getBytes();

					// Send a response
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
					
					Constants.setIP(sendPacket.getAddress().toString());
					Constants.setServer(true);
					Server s = new Server(Constants.ConnIP);
					s.run();
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(ServerBroadcast.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static ServerBroadcast getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {

		private static final ServerBroadcast INSTANCE = new ServerBroadcast();
	}

}