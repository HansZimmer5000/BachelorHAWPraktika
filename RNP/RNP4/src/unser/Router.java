package unser;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Path;

import gegeben.ControlPacket;
import gegeben.ControlPacket.Type;
import gegeben.IpPacket;
import gegeben.NetworkLayer;

/**
 * Router der aufgrund einer Routingtabelle Pakete weiterleitet oder mit Fehler zur�ckschickt.
 * @author Michael
 *
 */
public class Router {

	NetworkLayer networkLayer;
	NetworkLayer outNetworkLayer;
	RoutingTable routingTable;
	int PORT;
	private Inet6Address localHostIp6Address;
	
	/**
	 * Erstellt einen neuen Router
	 * @param port auf den der Router auf einkommende Pakete horcht.
	 * @param routingTable die Informationen zu den Routen enthaelt.
	 */
	public Router(int port, Path routingTable, Inet6Address localHostIp6Address){
		
		this.localHostIp6Address = localHostIp6Address;
		
		try {
			this.networkLayer = new NetworkLayer(port);
		} catch (SocketException e) {
			System.out.println("Kritisher Fehler im Konstruktor vom Router mit dem Port: " + port);
			this.networkLayer = null;
		}
		
		this.PORT = port;
		this.routingTable = new RoutingTable(routingTable);
	}
	
	/**
	 * run-Methode des Routers.
	 * @throws Exception 
	 */
	public void run() throws Exception{
		while(true){
			receiveAndHandlePacket();
		}
		
	}
	
	/**
	 * Horcht auf einen Port und wartet auf ein Paket.
	 * Dieses Paket wird verarbeitet.
	 * Also weitergeleitet gemaess Routingtabelle 
	 * oder mit Fehlermeldung zurueck an Absender geschickt.
	 * @throws Exception 
	 */
	public void receiveAndHandlePacket() throws Exception{
		IpPacket receivedPacket = null;
		int newHopLimit;
		RoutingEntry route;
		
		try {
			receivedPacket = this.networkLayer.getPacket();
			System.out.println("Packet received: "+ receivedPacket.toString()+" // NextHopPort: "+ receivedPacket.getNextHopPort());
		} catch (IOException e) {
			System.out.println("Fehler beim Empfangen eines Packets im receiveAndHandlePacket von Router mit: " + this.routingTable);
		}
		
		if(receivedPacket != null){
			
			//PaketTyp ermitteln
			IpPacket.Header packetType = receivedPacket.getType();
			boolean isDataPacket = ((packetType.compareTo(IpPacket.Header.Data)) == 0);
			
			//Verringern des Hoplimits
			int decrementedHopLimit = receivedPacket.getHopLimit()-1 ;
			
			// Aussortieren der Fehler
			// Wenn keine Fehler -> weiterleiten.
			//TODO: ControlPacket mit HopLimit 0 nicht weiterschicken
			if(decrementedHopLimit <= 0 ){
				
					System.out.println("HopLimit ist gleich 0");
					if(isDataPacket){
						System.out.println("Errorpaket wird geschickt");
						sendHopErrorPacket(receivedPacket);
					}
					
			} else {
				receivedPacket.setHopLimit(decrementedHopLimit);
					
				route = this.routingTable.getRightEntry(receivedPacket.getDestinationAddress());
				if(route == null){
					if(isDataPacket){
						System.out.println("Route konnte nicht ermittelt werden, ErrorPacket wir zurückgeschickt");
						sendRouteErrorPacket(receivedPacket);
					}
				} else {
					//System.out.println("Packet wird weitergeschickt auf folgender Route: "+ route.toString());
					forwardPacket(receivedPacket, route);
				}
			}
		}
	}
	
	/**
	 * Leitet die Nachricht an die naechste IP und Port weiter
	 * @param packet das weitergeschickt werden soll.
	 * @param route die genommen wird.
	 */
	private void forwardPacket(IpPacket packet, RoutingEntry route){
		
		//Paket bekommt Neue Werte zugewiesen. 
		packet.setNextHopIp(route.getNextIP());
		packet.setNextPort(route.getNextPort());
		
		try {
			//System.out.println("Packet wird ueber Port " +packet.getNextHopPort() + "weitergeschickt");
			networkLayer.sendPacket(packet);
		} catch (SocketException e1) {
			System.out.println("Socketfehler beim forwarden des Pakets");
		} catch (IOException e) {
			System.out.println("IOfehler beim forwarden des Pakets");
		}
	}
	
	/**
	 * Schickt ein Controlpacket mit Fehlermeldung zur�ck an den Absender.
	 * Fehler: Kein Hoplimit mehr.
	 * @param packet das den Fehler verursachte
	 */
	private void sendHopErrorPacket(IpPacket packet){
			//TODO:  Gucken inwiefern man die drei sendXXXPacket-Methoden zusammenfuehren/abstrahieren kann
		
		//Header von zu verwerfendem Packet in neues ByteArray kopieren,
		int headerLength = packet.HEADER_LEN;
		byte[] controlPacketPayload = new byte[headerLength];
		System.arraycopy(packet.getBytes(), 0, controlPacketPayload, 0, headerLength);
				
		//...um es dem ControlPacket als Payload mitzugeben
		ControlPacket hopErrorControlPacket = new ControlPacket(Type.TimeExceeded, controlPacketPayload);
		
		//Paket muss wieder Hops bekommen, um zum Ausgangsort geschickt werden zu koennen;
		//TODO: sinnvolles HopLimit setzen
		int newHopLimit = 10;
		
		IpPacket hopErrorIpPacket = null;
		
		try {
			//per RoutingTable den Korrekten Weg zurueck zum Absender finden (da Router den Weg zu seinen Zulieferern kennt)
			RoutingEntry route = this.routingTable.getRightEntry(packet.getSourceAddress());
			
			//neues IpPacket aus der gewonnenen Route generieren
			hopErrorIpPacket = new IpPacket(localHostIp6Address, packet.getSourceAddress(), newHopLimit,  route.getNextIP(),  route.getNextPort());
			
			//ControlPayload mitgeben und Type auf Control setzen 
			hopErrorIpPacket.setControlPayload(hopErrorControlPacket.getBytes());
			
		
			networkLayer.sendPacket(hopErrorIpPacket);
			
		} catch (UnknownHostException e1) {
			System.out.println("UknownHostfehler im sendHopError");
		} catch (IOException e) {
			System.out.println("IOfehler im sendHopError");
		}			
	}
	
	/**
	 * Schickt ein Controlpacket mit Fehlermeldung zur�ck an den Absender.
	 * Fehler: Keine Route zum Ziel vorhanden.
	 * @param packet das den Fehler verursachte
	 * @throws Exception 
	 */
	private void sendRouteErrorPacket(IpPacket packet) throws Exception{
		//TODO:  Gucken inwiefern man die drei sendXXXPacket-Methoden zusammenfuehren/abstrahieren kann
		
				//Header von zu verwerfendem Packet in neues ByteArray kopieren,
				int headerLength = packet.HEADER_LEN;
				byte[] controlPacketPayload = new byte[headerLength];
				System.arraycopy(packet.getBytes(), 0, controlPacketPayload, 0, headerLength);
						
				//...um es dem ControlPacket als Payload mitzugeben
				ControlPacket hopErrorControlPacket = new ControlPacket(Type.DestinationUnreachable, controlPacketPayload);
				
				//Paket muss wieder Hops bekommen, um zum Ausgangsort geschickt werden zu koennen;
				int newHopLimit = 10;
				
				IpPacket hopErrorIpPacket = null;
				
				try {
					//per RoutingTable den Korrekten Weg zurueck zum Absender finden (da Router den Weg zu seinen Zulieferern kennt)
					RoutingEntry route = this.routingTable.getRightEntry(packet.getSourceAddress());
					if(route == null){
						throw new Exception("Fatal Error: Client not found in RoutingTable");
					}
					//neues IpPacket aus der gewonnenen Route generieren
					hopErrorIpPacket = new IpPacket(localHostIp6Address, packet.getSourceAddress(), newHopLimit, route.getNextIP(), route.getNextPort());
					
					//ControlPayload mitgeben und Type auf Control setzen 
					hopErrorIpPacket.setControlPayload(hopErrorControlPacket.getBytes());
					
				
					networkLayer.sendPacket(hopErrorIpPacket);
					
				} catch (UnknownHostException e1) {
					System.out.println("UnkownHostfehler beim sendRouteError");
				} catch (IOException e) {
					System.out.println("IOfehler beim sendRouteError");
				}			
	}
}
