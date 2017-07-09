package unser;

import java.net.Inet6Address;
import java.net.UnknownHostException;

/**
 * Stellt eine Reihe (Eintrag) in der Routingtabelle dar.
 * @author Michael
 *
 */
public class RoutingEntry {

	private Inet6Address prefix, nextIP;
	private int suffix, nextPort;
	
	/**
	 * Erstellt einen neuen Eintrag.
	 * @param prefix stellt den Netzwerk teil der IP dar.
	 * @param suffix stellt dar wieviele bits von vorne der prefix sind.
	 * @param nextIP ist die IP zu der ein Paket weiter geleitet wird.
	 * @param nextPort siehe nextIP, nur für den Port.
	 */
	public RoutingEntry(Inet6Address prefix, int suffix, Inet6Address nextIP, int nextPort){
		this.prefix = prefix;
		this.suffix = suffix;
		this.nextIP = nextIP;
		this.nextPort = nextPort;
	}

	public String toString(){
		return this.prefix + "/" + this.suffix + "   " + this.nextIP + " " + this.nextPort;
	}
	
	/**
	 * Gibt aus welche IP höchstens noch innerhalb der Präfix/Suffix Kombination liegt.
	 * @return
	 */
	public Inet6Address getMaxIp(){
		// Vorerst noch konstant da kein Weg für eine dynamische Erstellung gefunden wurde.
		Inet6Address res = null;
		
		switch(this.suffix){
		case(48): 	try {
						res = (Inet6Address) Inet6Address.getByName("2001:db8:0000:ffff:ffff:ffff:ffff:ffff");
					} catch (UnknownHostException e) {}
					break;
		case(47): 	try {
						res = (Inet6Address) Inet6Address.getByName("2001:db8:0001:ffff:ffff:ffff:ffff:ffff");
					} catch (UnknownHostException e) {}
					break;
		}
		return res;
	}
	
	public Inet6Address getPrefix() {
		return prefix;
	}
	public Inet6Address getNextIP() {
		return nextIP;
	}
	public int getSuffix(){
		return this.suffix;
	}
	public int getNextPort() {
		return nextPort;
	}
}