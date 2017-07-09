package sort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import adt.implementations.AdtContainerFactory;
import adt.interfaces.AdtArray;

public class Generator {
	
	public static void sortnum(int amount){
		// Methode 3
		// Liste ist in zufälliger Reihenfolge
		// Ohne doppelte Zahlen

		AdtArray array1 = AdtContainerFactory.adtArray();
		
		int zahlBis = amount; // Obergrenze für die Zufallszahlen.
		
		for(int i = 0; i < amount; i++){
			Random rand = new Random();
			int zahl = rand.nextInt(zahlBis); //Zufallszahlen sind zwischen 0 und zahlBis.
			
			if(isin(array1,zahl)){
				i--;
			} else {
				array1.set(i, zahl);
			}
		}
		System.out.println(array1.length());
		exportNums(array1,"zahlen.dat");
	}
	
	public static void main(String args[]){
		sortnum(20);
		System.out.println("Ready");
	}
	
	public static void sortnumAlt(int amount){
		// Methode 3
		// Liste ist in zufälliger Reihenfolge

		AdtArray array1 = AdtContainerFactory.adtArray();
		
		int zahlBis = 1000; // Obergrenze für die Zufallszahlen.
		
		for(int i = 0; i < amount; i++){
			Random rand = new Random();
			int zahl = rand.nextInt(zahlBis); //Zufallszahlen sind zwischen 0 und 1000.
			array1.set(i, zahl);
		}
		
		exportNums(array1,"zahlen.dat");
	}
	
	public static void sortnumLeft(int amount){
		// Methode 1
		// Liste ist in aufsteigender Reihenfolge, also best case.	
			
		AdtArray array1 = AdtContainerFactory.adtArray();
		
		int i = 0;
		while(i < amount){
			array1.set(i, i+1);
			i++;
		}
		exportNums(array1, "zahlen.dat");
	}
	
	public static void sortnumRight(int amount){
		// Methode 2
		// Liste ist in absteigender Reihenfolge, also worst case.
		
		AdtArray array1 = AdtContainerFactory.adtArray();
		
		int i = amount;
		int j = 0;
		while(j < amount){
			array1.set(j, i);
			i--;
			j++;
		}
		exportNums(array1, "zahlen.dat");
	}
	
	public static boolean isin(AdtArray array1, int num){
		// searches for num in array1
		
		int length = array1.length();
		if(length > 0){
			int actualPos = 0;
			int actualElem;
			
			while(actualPos <= length){
				actualElem = array1.get(actualPos);
				if(actualElem == num) return true;
				actualPos++;
			}
			
		}	
		return false;
	}
	
	public static AdtArray importNums(String fileName){
		// Methode setzte voraus das die einzubindende Datei im selben Ordner ist wie z.b. ".classpath" oder der Order "src".
		
		AdtArray array1 = AdtContainerFactory.adtArray();
		
		int count = (array1.length()+1); // Somit sollte kein Wert überschrieben werden. Der Input aus der externen Datei wird hinten "angehängt".
		int actualNumber;
		
		try {
			Scanner input = new Scanner(new File(fileName));
			
			while(input.hasNextInt()){
				actualNumber = input.nextInt();
				array1.set(count, actualNumber);
				count++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return array1;
		
	}
	
	public static void exportNums(AdtArray array1, String fileName){
		File file = new File(fileName);
		
		try(BufferedWriter out= new BufferedWriter(new FileWriter(file))) {
			file.createNewFile();
			
			int pos = 0;
			while(pos <= array1.length()){
				Integer outElem = array1.get(pos);
				out.write(outElem.toString()+" ");
				pos++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
