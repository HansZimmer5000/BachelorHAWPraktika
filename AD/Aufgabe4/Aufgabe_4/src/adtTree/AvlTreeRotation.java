package adtTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import adt.interfaces.AdtArray;

public class AvlTreeRotation {
	private Knoten wurzel;
	private long rot;
	
	private AvlTreeRotation(){
		//...
		this.rot = 0;
	}
	public long getRot(){
		return this.rot;
	}
	
	public Knoten getWurzel(){
		return this.wurzel;
	}
	
	public static AvlTreeRotation create(){
		return new AvlTreeRotation();
	}
	
	public boolean isEmpty() {
		return wurzel == null;
	}

	public int high() {
		return this.wurzel.getHoehe();
	}
	
	public long insert(int elem) {
		
		if(wurzel == null){
			Knoten neu = new Knoten(elem);
			wurzel = neu;
		}else{
			this.insert(wurzel,elem);
		}
		return this.rot;
	}
	
	public void insert(Knoten wurzel, int elem){
		if(elem < wurzel.getKey()){
			if(!wurzel.haslSohn()){
				Knoten neu = new Knoten(elem);
				wurzel.setlSohn(neu);
				neu.setVater(wurzel);
				/*
				 * h�hen updaten
				 */
				Knoten unBal = neu.vater.updateHoeheAdd();
				if(unBal != null){
					balanceKnoten(unBal);
				}
			}else{
				this.insert(wurzel.lSohn,elem);
			}
		}else{
			if(!wurzel.hasrSohn()){
				Knoten neu = new Knoten(elem);
				wurzel.setrSohn(neu);
//				//System.out.println("Wurzel = "+ wurzel);
				neu.setVater(wurzel);
				
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
			//System.out.println("Kein Knoten zum l�schen, gefunden");
			return;
		}
		if(!del.haslSohn() && !del.hasrSohn()){
			//System.out.println("Knoten zum L�schen hat keine S�hne");
			Knoten vater = del.vater;
			
			if(vater!=null){
				if((vater.getKey() < del.getKey())){
					vater.setrSohn(null);
				}else{
					vater.setlSohn(null);
				}
				Knoten unBal = del.vater.updateHoeheAdd();
				
				if(unBal != null){
					balanceKnoten(unBal);
				}
			}
		}else if(del.haslSohn() && !del.hasrSohn()){
			//System.out.println("Knoten zum L�schen hat linken Sohn");
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
			//System.out.println("Knoten zum L�schen hat rechten Sohn");
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
			//System.out.println("Knoten zum L�schen :"+ del.getKey());
			int max = del.lSohn.getMax();
			del.setKey(max);
			Knoten k = this.search(del.lSohn,max);
			Knoten klinks = k.lSohn;
			//System.out.println("links von 40 "+k.lSohn);
			//System.out.println("rechts von 40 "+k.rSohn);
			
			Knoten vaterVonK = k.vater;
			
			if(vaterVonK.getKey() < k.getKey()){
				vaterVonK.setrSohn(klinks);
			}else{
				vaterVonK.setlSohn(klinks);
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
			 * im Command wird die Batch datei ausgef�hrt
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
	 * Liefert den Knoten mit dem �bergebenen elem
	 */
	public Knoten search(int elem){
		return this.search(wurzel,elem);
	}
	public Knoten search(Knoten wurzel,int elem){
		Knoten k = wurzel;
		while(k!=null){
			if(elem == k.getKey()){
				return k;
			}
			if(elem < k.getKey()){
				k = k.lSohn;
			}else{
				k = k.rSohn;
			}
		}
		return null;
	}
	
	public void leftRotate(Knoten unbal){
		rot++;
		//System.out.println("links");
		if(unbal.vater != null){
			if(unbal == unbal.vater.lSohn){
				unbal.vater.setlSohn(unbal.rSohn);
			}else{
				unbal.vater.setrSohn(unbal.rSohn);
			}
		}
		
		int lsohnheight = HilfsFunktion.hoehe(unbal.lSohn);
		int rsohnlsohnheight = HilfsFunktion.hoehe(unbal.rSohn.lSohn);
		int rsohnrsohnheight = HilfsFunktion.hoehe(unbal.rSohn.rSohn);
		Knoten unbalrsohn = unbal.rSohn;
		
		unbal.rSohn.setVater(unbal.vater);
		unbal.setVater(unbal.rSohn);
		Knoten templeft = unbal.rSohn.lSohn;
		unbal.rSohn.setlSohn(unbal);
		unbal.setrSohn(templeft);
		if(templeft != null) templeft.setVater(unbal);
	
		if(unbal == wurzel){
			wurzel = unbal.vater;
		}
		
		unbal.setHoehe(1 + HilfsFunktion.max(lsohnheight,rsohnlsohnheight));
		unbalrsohn.setHoehe(1 + HilfsFunktion.max(rsohnrsohnheight,HilfsFunktion.hoehe(unbal)));
	}
	
	public void rightRotate(Knoten unbal){
		rot++;
		//System.out.println("rechts");
		if(unbal.vater != null){
			if(unbal == unbal.vater.lSohn){
				unbal.vater.setlSohn(unbal.lSohn);
			}else{
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
		}
		
		unbal.setHoehe(1 + Math.max(rsohnheight, lsohnrsohnheight));
		unballsohn.setHoehe(1 + HilfsFunktion.max(lsohnlsohnheight,HilfsFunktion.hoehe(unbal)));
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
		int balThis = unbal.getBalance();
		int balLinks = 0;
		int balRechts = 0;
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
	
	public static void avlTreeRotationKlauck(){
		AvlTreeRotation tree = AvlTreeRotation.create();
		AdtArray arr = adtTree.generate.Generator.importNums("zahlen_klauck_avl.dat");
		//System.out.println(arr.length());
		long x = 0;
		for(int i = 0; i<=arr.length(); i++){
			
			x = tree.insert(arr.get(i));
		}
		
		long[] testErgRotation = new long[1];
		testErgRotation[0] = tree.getRot();
						
		exportArray("AvlTree", "Rotation", arr.length(), testErgRotation);
		
	}
	
	public static void exportArray(String methode, String art, int numberamount, long[] array){
		/* 1. methode
		 * 2. l�nge des Arrays
		 * 3. Rotationen Gesamt
		 */
		final String leer = " ";
		final String semicolon = ";";
		final String dateiTyp = ".csv";
		String filename = ("Benchmark "+methode+leer+art+leer+numberamount+dateiTyp);
		File file = new File(filename);
		
		try(BufferedWriter out= new BufferedWriter(new FileWriter(file))) {
			file.createNewFile();
			int wt = 0; //written Tupels
			out.write(art+semicolon+numberamount);
			wt = wt+2;
			
			int startPos = 0;
			if(art == "Steps"){
				wt = wt+2;
				startPos = 2;
				while(startPos > 0){
					out.write(semicolon+0);
					startPos--;
				}
			}
			
			for(int i = 0;i <= array.length-1; i++){
				if(i >= array.length) {out.write(semicolon+0); wt = wt+1;}
				else {out.write(semicolon+array[i]); wt=wt+1;}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}