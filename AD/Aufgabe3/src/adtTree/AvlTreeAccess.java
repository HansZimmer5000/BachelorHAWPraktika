package adtTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import adt.interfaces.AdtArray;

public class AvlTreeAccess {
	
	private Knoten wurzel;
	private long read;
	private long write;
	
	private AvlTreeAccess(){
		//...
		read = 0;
		write = 0;
	}
	public Knoten getWurzel(){
		return this.wurzel;
	}
	
	public static AvlTreeAccess create(){
		return new AvlTreeAccess();
	}
	
	public boolean isEmpty() {
		return wurzel == null;
	}

	public int high() {
		return this.wurzel.getHoehe();
	}
	
	public long[] insert(int elem) {
		read++;
		if(wurzel == null){
			Knoten neu = new Knoten(elem);
			wurzel = neu;
			write++;
		}else{
			this.insert(wurzel,elem);
		}
		long[] result = new long[2];
		result[0] = read;
		result[1] = write;
		
		return result;
	}
	
	public void insert(Knoten wurzel, int elem){
		read++;
		if(elem < wurzel.getKey()){
			read++;
			if(!wurzel.haslSohn()){
				Knoten neu = new Knoten(elem);
				wurzel.setlSohn(neu);
				write++;
				neu.setVater(wurzel);
				write++;
				/*
				 * höhen updaten
				 */
				Knoten unBal = neu.vater.updateHoeheAdd();
				if(unBal != null){
					balanceKnoten(unBal);
				}
			}else{
				this.insert(wurzel.lSohn,elem);
			}
		}else{
			read++;
			if(!wurzel.hasrSohn()){
				Knoten neu = new Knoten(elem);
				wurzel.setrSohn(neu);
				write++;
//				//System.out.println("Wurzel = "+ wurzel);
				neu.setVater(wurzel);
				write++;
				Knoten unBal = neu.updateHoeheAdd();
				if(unBal != null){
					balanceKnoten(unBal);
				}
				
			}else{
				this.insert(wurzel.rSohn,elem);
			}
		}
	}
	
	
	public void delete(int elem) {
		this.delete(wurzel, elem);	
	}
	
	public void delete(Knoten tmp, int elem){
		Knoten del = this.search(elem);
		if(del == null){
			//System.out.println("Kein Knoten zum löschen, gefunden");
			return;
		}
		read++;
		read++;
		if(!del.haslSohn() && !del.hasrSohn()){
			//System.out.println("Knoten zum Löschen hat keine Söhne");
			Knoten vater = del.vater;
			read++;
			
			if(vater!=null){
				read++;
				read++;
				if((vater.getKey() < del.getKey())){
					vater.setrSohn(null);
					write++;
				}else{
					vater.setlSohn(null);
					write++;
				}
				Knoten unBal = del.vater.updateHoeheAdd();
				
				if(unBal != null){
					balanceKnoten(unBal);
				}
			}
		}else if(del.haslSohn() && !del.hasrSohn()){
			//System.out.println("Knoten zum Löschen hat linken Sohn");
			Knoten lSohn = del.lSohn;
			
			Knoten vater = del.vater;
			if(vater!=null){
				if(vater.getKey() < del.getKey()){
					vater.setrSohn(lSohn);
				}else{
					vater.setlSohn(lSohn);
				}
				Knoten unBal = del.vater.updateHoeheAdd();
				if(unBal != null){
					balanceKnoten(unBal);
				}
			}
		}else if(del.hasrSohn() && !del.haslSohn()){
			read++;			//Für vorherige If Abfrage
			read++;			//Für vorherige If Abfrage
			read++;
			read++;
			
			//System.out.println("Knoten zum Löschen hat rechten Sohn");
			Knoten rSohn = del.rSohn;
			
			Knoten vater = del.vater;
			if(vater!=null){
				if(vater.getKey() < del.getKey()){
					vater.setrSohn(rSohn);
				}else{
					vater.setlSohn(rSohn);
				}
				Knoten unBal = del.vater.updateHoeheAdd();
				if(unBal != null){
					balanceKnoten(unBal);
				}
			}
		}else{
			read++;			//Für vorherige If Abfrage
			read++;			//Für vorherige If Abfrage
			read++;			//Für vorherige If Abfrage
			read++;			//Für vorherige If Abfrage
			
//			//System.out.println("Knoten zum Löschen :"+ del.getKey());
			int max = del.lSohn.getMax();
			del.setKey(max);
			write++;
			Knoten k = this.search(del.lSohn,max);
			Knoten klinks = k.lSohn;
			read++;
			//System.out.println("links von 40 "+k.lSohn);
			//System.out.println("rechts von 40 "+k.rSohn);
			
			Knoten vaterVonK = k.vater;
			read++;
			
			read++;
			read++;
			if(vaterVonK.getKey() < k.getKey()){
				vaterVonK.setrSohn(klinks);
				write++;
			}else{
				vaterVonK.setlSohn(klinks);
				write++;
			}
			
			Knoten unBal = k.vater.updateHoeheAdd();
			if(unBal != null){
				balanceKnoten(unBal);
			}
			
		}
	}

	public void print(String dateiname){
		try {
			preOrderPrint(dateiname);
			/*
			 * Batch datei erstellen mit:
			 * <Pfad zur graphviz exe> -Tpng graph.dot > graph.png
			 * im Command wird die Batch datei ausgeführt
			 * Diese befindet sich im Classpath des proejekts
			 */
			Process p = Runtime.getRuntime().exec("graphviz.bat");
//			Runtime.getRuntime().exec("graph.png");
//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String line = null;
//			while ((line = in.readLine()) != null) {
//				//System.out.println(line);
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * verschiedene Traversierungen durch Baum
	 */
	public void preOrderPrint(String dateiname) throws IOException{
		BufferedWriter fw;
		File fout = new File(dateiname+".dot");
		FileOutputStream fos = new FileOutputStream(fout);
		fw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
		try{
			fw.write("digraph G ");
			fw.write("{");
			this.preOrderPrint(wurzel,fw);
			fw.write("}");
		}
		catch ( IOException e ) {
			System.err.println( "Konnte Datei nicht erstellen" );
		}
		finally{
			fw.close();
		}
	}
	public void preOrderPrint(Knoten wurzel, BufferedWriter fw) throws IOException{
				
		try{
			if(wurzel == null){
				return;
			}
			
			int k = wurzel.getKey();
			if(wurzel.haslSohn()){
				int klinks = wurzel.lSohn.getKey();
				fw.write(" "+k+" -> "+klinks+";");
			}
			if(wurzel.hasrSohn()){
				int krechts = wurzel.rSohn.getKey();
				fw.write(" "+k+" -> "+krechts+";");
			}
			
			preOrderPrint(wurzel.lSohn,fw);
			preOrderPrint(wurzel.rSohn,fw);
			
		} catch (IOException e) {
			//System.out.println("Fehler in traverse");
			//			e.printStackTrace();
		}		
	}
	
	public void preOrder(){
		this.preOrder(wurzel);
	}
	
	public void preOrder(Knoten wurzel){
		if(wurzel == null){
			return;
		}
		
		//System.out.println("#####"+wurzel.getKey()+"#####");
		//System.out.println("linkerSohn : "+wurzel.lSohn);
		//System.out.println("rechterSohn : "+wurzel.rSohn);
		//System.out.println("Vater : "+ wurzel.vater);
//		//System.out.println("####################");
		
//		if(wurzel.haslSohn())
//			//System.out.println("Linker SOhn: "+wurzel.lSohn.getKey());
		
//		if(wurzel.hasrSohn())
//			//System.out.println("Rechter Sohn: "+wurzel.rSohn.getKey());
		
		preOrder(wurzel.lSohn);
		preOrder(wurzel.rSohn);
	}
	
	
	/*
	 * Liefert den Knoten mit dem übergebenen elem
	 */
	public Knoten search(int elem){
		return this.search(wurzel,elem);
	}
	public Knoten search(Knoten wurzel,int elem){
		Knoten k = wurzel;
		while(k!=null){
			read++;
			if(elem == k.getKey()){
				return k;
			}
			read++;
			if(elem < k.getKey()){
				k = k.lSohn;
				read++;
			}else{
				k = k.rSohn;
				read++;
			}
		}
		return null;
	}
	
	public void leftRotate(Knoten unbal){
		//System.out.println("links");
		read++;
		if(unbal.vater != null){
			read++;
			if(unbal == unbal.vater.lSohn){
				unbal.vater.setlSohn(unbal.rSohn);
				write++;
			}else{
				unbal.vater.setrSohn(unbal.rSohn);
				write++;
			}
		}
		
		int lsohnheight = HilfsFunktion.hoehe(unbal.lSohn);
		read++;
		int rsohnlsohnheight = HilfsFunktion.hoehe(unbal.rSohn.lSohn);
		read++;
		int rsohnrsohnheight = HilfsFunktion.hoehe(unbal.rSohn.rSohn);
		read++;
		Knoten unbalrsohn = unbal.rSohn;
		read++;
		
		unbal.rSohn.setVater(unbal.vater);
		write++;
		unbal.setVater(unbal.rSohn);
		write++;
		Knoten templeft = unbal.rSohn.lSohn;
		read++;
		unbal.rSohn.setlSohn(unbal);
		write++;
		unbal.setrSohn(templeft);
		write++;
		if(templeft != null) templeft.setVater(unbal);
		write++;
		read++;
		if(unbal == wurzel){
			wurzel = unbal.vater;
			read++;
			write++;
		}
		
		unbal.setHoehe(1 + HilfsFunktion.max(lsohnheight,rsohnlsohnheight));
		unbalrsohn.setHoehe(1 + HilfsFunktion.max(rsohnrsohnheight,HilfsFunktion.hoehe(unbal)));
		
		write++;
		write++;
	}
	
	public void rightRotate(Knoten unbal){
		//System.out.println("rechts");
		read++;
		if(unbal.vater != null){
			read++;
			if(unbal == unbal.vater.lSohn){
				write++;
				unbal.vater.setlSohn(unbal.lSohn);
			}else{
				write++;
				unbal.vater.setrSohn(unbal.lSohn);
			}
		}
		
		int rsohnheight = HilfsFunktion.hoehe(unbal.rSohn);
		int lsohnrsohnheight = HilfsFunktion.hoehe(unbal.lSohn.rSohn);
		int lsohnlsohnheight = HilfsFunktion.hoehe(unbal.lSohn.lSohn);
		Knoten unballsohn = unbal.lSohn;
		
		
		unbal.lSohn.setVater(unbal.vater);
		unbal.setVater(unbal.lSohn);
		Knoten tmpRight = unbal.lSohn.rSohn;
		unbal.lSohn.setrSohn(unbal);
		unbal.setlSohn(tmpRight);
		if(tmpRight != null) tmpRight.setVater(unbal);
		if(unbal == wurzel){
			wurzel = unbal.vater;
			read++;
			write++;
		}
		
		unbal.setHoehe(1 + Math.max(rsohnheight, lsohnrsohnheight));
		unballsohn.setHoehe(1 + HilfsFunktion.max(lsohnlsohnheight,HilfsFunktion.hoehe(unbal)));
		
		/*
		 * Aus leftrotate übernommen
		 */
		read++;
		read++;
		read++;
		read++;
		read++;
		read++;
		
		write++;
		write++;
		write++;
		write++;
		write++;
		write++;
		write++;
	}
	
	public void doubleRight(Knoten unbal){
		//System.out.println("doppelrechts");
		leftRotate(unbal.lSohn);
		rightRotate(unbal);
	}
	
	public void doubleLeft(Knoten unbal){
		//System.out.println("doppellinks");
		rightRotate(unbal.rSohn);
		leftRotate(unbal);
	}
	
	public void balanceKnoten(Knoten unbal){
		read++;
		int balThis = unbal.getBalance();
		int balLinks = 0;
		int balRechts = 0;
		read++;
		read++;
		if(unbal.lSohn != null)	{balLinks = unbal.lSohn.getBalance();}
		if(unbal.rSohn != null) {balRechts = unbal.rSohn.getBalance();}
		
		if(balThis == -2 && balLinks == -1){
			//RechtsRotate
			rightRotate(unbal);
		}else if(balThis == -2 && balLinks == 1){
			//doppelRechtsRoate
			doubleRight(unbal);
		}else if(balThis == 2 && balRechts == -1){
			//DoppelLinksRotate
			doubleLeft(unbal);
		}else{
			//linksRotate
			leftRotate(unbal);
		}
	}


	public static void main(String[] args) {
		AvlTreeAccess tree = AvlTreeAccess.create();
		AdtArray arr = adtTree.generate.Generator.importNums("zahlen_klauck_avl.dat");
		System.out.println(arr.length());
		for(int i = 0; i<=arr.length(); i++){
			tree.insert(arr.get(i));
		}
		System.out.println(tree.high());
	}

}
