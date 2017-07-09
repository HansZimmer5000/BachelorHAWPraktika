package unser;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Ist die interne Representation einer Routingtabelle die als txt im Projekt Folder liegt.
 * @author Michael
 *
 */
public class RoutingTable {

	private ArrayList<RoutingEntry> entries;

	/**
	 * Die neue Tabelle wird durch einlesen der routingtabelle als txt Datei erstellt.
	 * @param path Pfad zur TXTFile, nur der Name der File wenn sie im Projektordner liegt.s
	 */
	public RoutingTable(Path path) {
		RoutingEntry tmpEintrag;
		List<String> tmpLines = new ArrayList<>();
		String[] tmpLine;
		this.entries = new ArrayList<>();

		// Es wird versucht alle Zeilen zu lesen.
		// Klappt das nicht bleibt das Array leer.
		try {
			tmpLines = Files.readAllLines(path);
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen der RoutingTabelle in " + path.toString());
		}

		// Es werden nun alle Reihen (Linien) durchgegangen.
		// Eine Linie ist getrennt durch ";". prefix/suffix ; targetIP ; targetPort
		for (String tmpStr : tmpLines) {
			tmpLine = tmpStr.split(";");
			tmpEintrag = makeRoutingEntryFrom(tmpLine);

			if (tmpEintrag != null) {
				this.entries.add(tmpEintrag);
			}
		}
	}

	/**
	 * Erstellt mit den gegebenen Infos einen Eintrag der Tabelle (eine Reihe)
	 * @param args 0 = prefix mit suffix / 1 = nextIP / 2 = nextPort
	 * @return Einen Eintrag der routingtabelle, der Eintrag ist noch nicht in der Routingtabelle.
	 */
	public RoutingEntry makeRoutingEntryFrom(String[] args) {
		Inet6Address prefix, nextIP;
		int suffix, nextPort;
		String[] addressRange;
		RoutingEntry res = null; // Defaultwert falls Fehler

		// Es m�ssen genug Argumente vorhanden sein.
		// Sonst f�hrt es sp�ter zu einer ArrayIndexOutofBoundException o.�.
		if (args.length == 0) {
			System.out.println("Zeile wurde nicht richtig gelesen! Input ist leer.");
			return res;
		} else if (args.length != 3) {
			System.out.println(
					"Zeile wurde nicht richtig gelesen! Es sind nicht genau 3 Informationen. 1. Feld des Inputs: "
							+ args[0]);
			return res;
		}

		// Eine Linie ist getrennt durch ";". prefix/suffix ; targetIP ; targetPort
		// prefix und suffix sind getrennt durch "/" und m�ssen noch einmal extra separiert werden.
		addressRange = args[0].split("/");
		suffix = (int) Integer.valueOf(addressRange[1]);
		nextPort = (int) Integer.valueOf(args[2]);

		// Konnte der prefix erstellt werden wird versuht nextIP zu erstellen.
		// Klappt beides wird ein neuer Eintrag erstellt und zur�ckgegeben.
		try {
			prefix = (Inet6Address) Inet6Address.getByName(addressRange[0]);
			
			try {
				nextIP = (Inet6Address) Inet6Address.getByName(args[1]);
				res = new RoutingEntry(prefix, suffix, nextIP, nextPort);
				
			} catch (UnknownHostException e2) {
				System.out.println("Fehler mit der nextIP zu Inet6Address");
			}
		} catch (UnknownHostException e1) {
			System.out.println("Fehler mit dem Prefix zu Inet6Address");
		}

		return res;
	}
	
	/**
	 * Sucht anhand der erstellten Routingtabelle den passenden Routeneintrag heraus.
	 * @param targetIP ist die IP an die das Paket ankommen soll.
	 * @return Einen Eintrag der Routingtabelle.
	 */
	public RoutingEntry getRightEntry(Inet6Address targetIP){
		// TODO: Nicht ganze IP sondern nur Prefix (Longest-PREFIX) beachten!
		System.out.println("RoutingTable: getRightEntry mit targetIP: "+ targetIP.toString());
		RoutingEntry res = null; // Defaultwert falls Fehler
		int resInt = 0, tmpInt;
		
		// Geht nun alle Eintr�ge der Tabelle durch.
		// Passt ein anderer Eintrag besser als der bisherige (tmpInt > resInt) wird der neue Eintrag in res gespeichert.
		// Zudem wird resInt angepasst. 
		for(RoutingEntry tmpEntry : this.entries){
			System.out.println("  Vergleich mit " + tmpEntry.getPrefix().toString());
			tmpInt = lastOkIndex(targetIP, tmpEntry.getPrefix(), tmpEntry.getSuffix());
			if(tmpInt > resInt){
				resInt = tmpInt;
				res = tmpEntry;
			}
		}
		return res;
	}
	
	/**
	 * Vergleicht 2 Inet6Addressen und sucht nach dem ersten Index bei dem die byte Arrays nicht mehr gleich sind!t 
	 * @param ip1
	 * @param ip2
	 * @return index mit der ersten ungleichen stelle.
	 */
	private int lastOkIndex(Inet6Address ip1, Inet6Address ip2, int suffix){
		BitSet bitset1 = BitSet.valueOf(ip1.getAddress()), bitset2 = BitSet.valueOf(ip2.getAddress());
		int res = 0;
		
		if(ip1.equals(ip2)){
			return 128;
		}
		
		// Es werden nun alle Stellen abgegaben.
		// Passt eine stelle nicht wird der vorletzte Index zurueckgegeben.
		for(int i = 0; i < 128 ; i++){
			if(bitset1.get(i)!=bitset2.get(i)){
				res = i-1;
				i=128;
			}
		}
		
		if(res < suffix){
			return 0;
		} else {
			return res;
		}
	}
	

	public ArrayList<RoutingEntry> getEntries() {
		return entries;
	}
	
	public String toString(){
		String res = "";
		for(RoutingEntry tmpEntry : this.entries){
			if(!res.equals("")){
				res += "\n";
			}
			res += tmpEntry.toString();
		}
		return res;
	}

}
