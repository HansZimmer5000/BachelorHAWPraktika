package filecopy;

import java.util.ArrayList;

public class ADTBuffer {
	
	ArrayList<FCpacket> buffer = new ArrayList<>();
	int sendBase;
	int nextSeqNumber;
	final int windowSize;
	final int bufferSize;
	
	public ADTBuffer(int windowSize, String sourcePath, FCpacket controlPacket){
		this.windowSize = windowSize;
		this.buffer.add(controlPacket);
		
		this.buffer.addAll(FCpacketUtil.fileToFCpackets(sourcePath));
		this.bufferSize = buffer.size();
		this.sendBase = 0;
		this.nextSeqNumber = 0;
	}
	
	public synchronized boolean hasNext(){
		//TODO?: Hier berücksichtigen, dass vllt nicht alle packete geackt sind...
		return sendBase < this.getSize();
	}
	
	public synchronized FCpacket getNext(){
		// Logik für Bestimmung des nächsten Pakets einfügen. Sendbase, etc...
		FCpacket packet;
		int limit = (sendBase + windowSize);//- 1;
		int bufferSize = buffer.size();
		
		if(limit > bufferSize){
			limit = bufferSize; //-1;
		}
		
		while(nextSeqNumber < limit){
			 packet = buffer.get(nextSeqNumber);
			 if(!packet.isValidACK()){
				 nextSeqNumber++ ;
				 return packet;
			 } else{
				 nextSeqNumber++;
			 }
		}
		
		return null;
	}
	
	public FCpacket getPacketFromSeqNum(long longSeqNum){
		return buffer.get((int) longSeqNum);
	}
	
	public int getSize(){
		return this.buffer.size();
	}
	
	public synchronized void ackPacket(long longSeqNumber){
		// Momentan wird hier long zu int... Werden die Dateien jemals so groß, dass wir sie nichtmehr verarbeiten können? Im Zweifel abfragen
		// Glaube nicht da es mehrere Millionen Pakete sein müssten (IntMax = 2.147.483.647, Über 2 Milliarden Pakete... -> über 1 TB)
		int intSeqNumber = (int) longSeqNumber;
		buffer.get(intSeqNumber).setValidACK(true);
		FileCopyClient.testOut("Packet number "+ intSeqNumber +" acked! "+ buffer.get(intSeqNumber).isValidACK());
		updateSendBase(intSeqNumber);
	}
	
	private synchronized void updateSendBase(int seqNumber){
		if(seqNumber == sendBase){
			while(sendBase < buffer.size()){
				// Alle bis zum naechsten nicht bestaetigten Packet werden uebersprungen.
				// Ist man bei besagtem Packet angekommen muss dies ja "ge-Acked" werden 
				// und somit wird nextSeqNumer darauf gesetzt.
				if(buffer.get(sendBase).isValidACK()){
					this.sendBase++;
				}else {
					nextSeqNumber = sendBase;
					return;
				}
			}
		}
	}

	public synchronized void resendPacket(long longSeqNum) {
		this.nextSeqNumber = sendBase;
	}
}
