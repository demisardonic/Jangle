package com.jangle.voice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.jangle.client.User;
import com.jangle.communicate.Client_ParseData;

/**
 * Class that acts as a wrapper for a socket, so is easier to manage for
 * VoiceChat
 * 
 * @author Nathan Conroy
 *
 */
public class VoiceChatSocket implements Runnable {

	private DatagramSocket socket;
	private String Address;
	private int port;
	private Client_ParseData Parser;
	
	private byte[] Data;
	
	private int micDataWidth;

	private User User;

	private String adress;
	private int amount;

	/**
	 * Create socket to communicate with
	 * 
	 * @param Adress
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public VoiceChatSocket(User gUser, int gport, Client_ParseData gParser)
			throws UnknownHostException, IOException {
		User = gUser;
		Parser = gParser;
		port = gport;
		Address = User.getIP();
		socket = new DatagramSocket();
		Address = User.getIP();
		
		
	}
	
	public void sendVoice(byte[] data, int gamount){
		Data = data;
		amount = gamount;
		Thread th = new Thread(this);
		th.start();
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if (User.getIP() == "" || User.getIP() == "FAIL" || Address == null){
			try {
				
				User.setIP(Parser.getUserIP(User));
				Address = User.getIP();
			} catch (IOException e) {
				//Happens if a communication error occurs. 
			}
		}
		
		if (User.getIP() == "" || User.getIP() == "FAIL" || Address == null){
			return;
		}
		
		byte[] toClient = new byte[Data.length + 4];
		byte[] amb = VoiceUtil.intToByteArr(amount);
		
		for (int i = 0; i < Data.length; i++){
			toClient[4 + i] = Data[i];
		}
		
		toClient[0] = amb[0];
		toClient[1] = amb[1];
		toClient[2] = amb[2];
		toClient[3] = amb[3];
		
		DatagramPacket packet = null;
		try {
			//packet = new DatagramPacket(Data, VoiceUtil.VOICE_DATA_SIZE, InetAddress.getByAddress(VoiceUtil.byteIP(Address)), port);
			packet = new DatagramPacket(toClient, toClient.length, InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			socket.send(packet);
		} catch (IOException e) {
			//failed to send the packet. Since this is datagram, if there is no reciever, nothing should happen. 
		}
		
	}

}
