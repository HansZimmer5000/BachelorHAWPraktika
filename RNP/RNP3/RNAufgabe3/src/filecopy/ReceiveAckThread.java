package filecopy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveAckThread extends Thread{
	
	DatagramSocket serverSocket;
	ADTBuffer buffer;
	
	private FileCopyClient client;
	private byte[] receiveData;
	private int UDP_PACKET_SIZE;
	
	
	public ReceiveAckThread(FileCopyClient client, DatagramSocket serverSocket, ADTBuffer buffer){
		this.client = client;
		this.receiveData = new byte[client.UDP_PACKET_SIZE];
		this.UDP_PACKET_SIZE = client.UDP_PACKET_SIZE;
		
		this.serverSocket = serverSocket;
		this.buffer = buffer;
	}
	
	public void run(){
		while(!this.isInterrupted()){
			try{
				DatagramPacket receivePacket = new DatagramPacket(receiveData, UDP_PACKET_SIZE);
				serverSocket.receive(receivePacket);
				handlePacket(receivePacket);
			} catch(IOException e){
				//Socket wurde geschlossen, Thread auslaufen lassen.
				//System.out.println("ReceiveAckthread: Could not receive DatagramPacket!");
			}
		}
	}

	private synchronized void handlePacket(DatagramPacket receivePacket) {
		// Sequenznummer nun fuer das Acken wichtig!
		long seqNum = new FCpacket(receivePacket.getData(), receivePacket.getLength()).getSeqNum();
		FileCopyClient.ACK_COUNT++;
		buffer.ackPacket(seqNum);
		client.cancelTimer(buffer.getPacketFromSeqNum(seqNum));
	}

}
