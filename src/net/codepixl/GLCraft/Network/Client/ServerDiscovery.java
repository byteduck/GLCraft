package net.codepixl.GLCraft.Network.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import net.codepixl.GLCraft.util.Constants;

public class ServerDiscovery implements Runnable{
	static DatagramSocket c;
	
	@Override
	public void run(){
		try{
			// Find the server using UDP broadcast
			// Open a random port to send the package
			c = new DatagramSocket();
			c.setBroadcast(true);
	
			byte[] sendData = "GLCRAFT_SERVER_REQUEST".getBytes();
	
			// Try the 255.255.255.255 first
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, InetAddress.getByName("192.168.1.255"),14454);
				c.send(sendPacket);
			} catch (Exception e) {
			}
	
			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
	
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback interface
				}
	
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}
	
					// Send the broadcast package!
					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 14454);
						c.send(sendPacket);
					} catch (Exception e) {
					}
					
				}
			}
	
			// Wait for a response
			byte[] recvBuf = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf,
					recvBuf.length);
			c.setSoTimeout(1000);
			c.receive(receivePacket);
	
			// We have a response
	
			// Check if the message is correct
			String message = new String(receivePacket.getData()).trim();
			
			 if (message.equals("GLCRAFT_SERVER_RESPONSE")) {
				 Constants.setIP(receivePacket.getAddress().toString()); 
			 }
			 c.close();
			 Client c = new Client(Constants.ConnIP);
			 c.run();
		}catch(IOException e){
			e.printStackTrace();
			if(e instanceof SocketTimeoutException){
				System.out.println(":( no servers");
				JOptionPane.showMessageDialog(null, "Either the server was too slow or there was no server.", "Error", JOptionPane.ERROR_MESSAGE);
				Constants.setState(Constants.START_SCREEN);
			}
		}
	}
}
