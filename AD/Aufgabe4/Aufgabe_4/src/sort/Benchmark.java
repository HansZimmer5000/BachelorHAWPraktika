package sort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import adt.implementations.AdtContainerFactory;
import adt.interfaces.*;;

public class Benchmark {
	
	public static void main(String args[]){	

		//		AdtArray itime100 = AdtContainerFactory.adtArray();
		AdtArray itime1000 = AdtContainerFactory.adtArray();
//		AdtArray itime10000 = AdtContainerFactory.adtArray();

//		AdtArray isteps100 = AdtContainerFactory.adtArray();
		AdtArray isteps1000 = AdtContainerFactory.adtArray();
//		AdtArray isteps10000 = AdtContainerFactory.adtArray();

		AdtArray qtime100 = AdtContainerFactory.adtArray();
		
		AdtArray qtime1000 = AdtContainerFactory.adtArray();
		AdtArray lqtime1000 = AdtContainerFactory.adtArray();
		AdtArray rqtime1000 = AdtContainerFactory.adtArray();
		
		AdtArray qtime1000m = AdtContainerFactory.adtArray();
		AdtArray lqtime1000m = AdtContainerFactory.adtArray();
		AdtArray rqtime1000m = AdtContainerFactory.adtArray();
		
		AdtArray qtime1000r = AdtContainerFactory.adtArray();
		AdtArray lqtime1000r = AdtContainerFactory.adtArray();
		AdtArray rqtime1000r = AdtContainerFactory.adtArray();
		
		AdtArray qtime1000l = AdtContainerFactory.adtArray();
		AdtArray lqtime1000l = AdtContainerFactory.adtArray();
		AdtArray rqtime1000l = AdtContainerFactory.adtArray();
		
		AdtArray qtime10000 = AdtContainerFactory.adtArray();
		
		AdtArray qsteps100 = AdtContainerFactory.adtArray();
		AdtArray qsteps1000 = AdtContainerFactory.adtArray();
		AdtArray qsteps10000 = AdtContainerFactory.adtArray();
				
		final int amountofNumbers100 = 100;
		final int amountofNumbers1000 = 1000;
		final int amountofNumbers10000 = 10000;
		
		
//		Generator.sortnum(amountofNumbers100);
//		itime100 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers1000);
		itime1000 = Generator.importNums("zahlen.dat");
		
//		Generator.sortnum(amountofNumbers10000);
//		itime10000 = Generator.importNums("zahlen.dat");
		
//		Generator.sortnum(amountofNumbers100);
//		isteps100 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers1000);
		isteps1000 = Generator.importNums("zahlen.dat");
		
//		Generator.sortnum(amountofNumbers10000);
//		isteps10000 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers100);
		qtime100 = Generator.importNums("zahlen.dat");

//		##########################################################
		/*quicksort time mit jeweils random,left,right sort und links , rechts, median, ranodm pivot*/

		Generator.sortnum(amountofNumbers1000);
		qtime1000 = Generator.importNums("zahlen.dat");
		
		Generator.sortnumLeft(amountofNumbers1000);
		lqtime1000 = Generator.importNums("zahlen.dat");
		
		Generator.sortnumRight(amountofNumbers1000);
		rqtime1000 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers1000);
		qtime1000m = Generator.importNums("zahlen.dat");
		
		Generator.sortnumLeft(amountofNumbers1000);
		lqtime1000m = Generator.importNums("zahlen.dat");
		
		Generator.sortnumRight(amountofNumbers1000);
		rqtime1000m = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers1000);
		qtime1000r = Generator.importNums("zahlen.dat");
		
		Generator.sortnumLeft(amountofNumbers1000);
		lqtime1000r = Generator.importNums("zahlen.dat");
		
		Generator.sortnumRight(amountofNumbers1000);
		rqtime1000r = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers1000);
		qtime1000l = Generator.importNums("zahlen.dat");
		
		Generator.sortnumLeft(amountofNumbers1000);
		lqtime1000l = Generator.importNums("zahlen.dat");
		
		Generator.sortnumRight(amountofNumbers1000);
		rqtime1000l = Generator.importNums("zahlen.dat");
//##########################################################
		
		Generator.sortnum(amountofNumbers10000);
		qtime10000 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers100);
		qsteps100 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers1000);
		qsteps1000 = Generator.importNums("zahlen.dat");
		
		Generator.sortnum(amountofNumbers10000);
		qsteps10000 = Generator.importNums("zahlen.dat");
		
		int startPos = 0;
		
//		long aitime100 = Sorter.insertionsortTime(itime100, startPos, itime100.length());
		long aitime1000 = Sorter.insertionsortTime(itime1000, startPos, itime1000.length());
//		long aitime10000 = Sorter.insertionsortTime(itime10000, startPos, itime10000.length());
		
//		long[] aisteps100 = Sorter.insertionsortSteps(isteps100, startPos, isteps100.length());
		long[] aisteps1000 = Sorter.insertionsortSteps(isteps1000, startPos, isteps1000.length());
//		long[] aisteps10000 = Sorter.insertionsortSteps(isteps10000, startPos, isteps10000.length());

		/*
		long[] aqtime100 = Sorter.quicksortTime(qtime100, (a,l,r)->Sorter.median(a, l, r));	

		long[] aqtime1000 = Sorter.quicksortTime(qtime1000, (a,l,r)->Sorter.random(l, r));
		long[] alqtime1000 = Sorter.quicksortTime(lqtime1000, (a,l,r)->Sorter.random(l, r));
		long[] arqtime1000 = Sorter.quicksortTime(rqtime1000, (a,l,r)->Sorter.random(l, r));
		
		long[] aqtime1000m = Sorter.quicksortTime(qtime1000m, (a,l,r)->Sorter.median(a,l, r));
		long[] alqtime1000m = Sorter.quicksortTime(lqtime1000m, (a,l,r)->Sorter.median(a,l, r));
		long[] arqtime1000m = Sorter.quicksortTime(rqtime1000m, (a,l,r)->Sorter.median(a,l, r));
		
		long[] aqtime1000r = Sorter.quicksortTime(qtime1000r, (a,l,r)->r);
		long[] alqtime1000r = Sorter.quicksortTime(lqtime1000r, (a,l,r)->r);
		long[] arqtime1000r = Sorter.quicksortTime(rqtime1000r, (a,l,r)->r);
		
		long[] aqtime1000l = Sorter.quicksortTime(qtime1000l, (a,l,r)->l);
		long[] alqtime1000l = Sorter.quicksortTime(lqtime1000l, (a,l,r)->l);
		long[] arqtime1000l = Sorter.quicksortTime(rqtime1000l, (a,l,r)->l);

		long[] aqtime10000 = Sorter.quicksortTime(qtime10000, (a,l,r)->Sorter.random(l, r));
		
		long[] aqsteps100 = Sorter.quicksortSteps(qsteps100, (a,l,r)->Sorter.random(l, r));
		long[] aqsteps1000 = Sorter.quicksortSteps(qsteps1000, (a,l,r)->Sorter.random(l, r));
		long[] aqsteps10000 = Sorter.quicksortSteps(qsteps10000, (a,l,r)->Sorter.random(l, r));
		

		exportSingleTime("Insertion","Time",amountofNumbers1000,aitime1000);		
		exportArray("Insertion","Steps", amountofNumbers1000,aisteps1000);
		
		exportArray("Quick", "Time", amountofNumbers100, aqtime100);
		exportArray("Quick", "Time", amountofNumbers1000, aqtime1000);
		exportArray("Quick", "Time", amountofNumbers10000, aqtime10000);
		
		exportArray("Quick", "Steps", amountofNumbers100, aqsteps100);
		exportArray("Quick", "Steps", amountofNumbers1000, aqsteps1000);
		exportArray("Quick", "Steps", amountofNumbers10000, aqsteps10000);
		
		exportArray("Quick", "Time", amountofNumbers1000, aqtime1000);
		exportArray("Quick(leftsort)", "Time", amountofNumbers1000, alqtime1000);
		exportArray("Quick(rightsort)", "Time", amountofNumbers1000, arqtime1000);
		
		exportArray("Quick(median)", "Time", amountofNumbers1000, aqtime1000m);
		exportArray("Quick(leftsort,median)", "Time", amountofNumbers1000, alqtime1000m);
		exportArray("Quick(rightsort,median)", "Time", amountofNumbers1000, arqtime1000m);
		
		exportArray("Quick(rechts)", "Time", amountofNumbers1000, aqtime1000r);
		exportArray("Quick(leftsort,rechts)", "Time", amountofNumbers1000, alqtime1000r);
		exportArray("Quick(rightsort,rechts)", "Time", amountofNumbers1000, arqtime1000r);
		
		exportArray("Quick(links)", "Time", amountofNumbers1000, aqtime1000l);
		exportArray("Quick(leftsort,links)", "Time", amountofNumbers1000, alqtime1000l);
		exportArray("Quick(rightsort,links)", "Time", amountofNumbers1000, arqtime1000l);
		
		exportArray("Quick", "Steps", amountofNumbers100, aqsteps100);
		exportArray("Quick", "Steps", amountofNumbers1000, aqtime1000);
		exportArray("Quick", "Steps", amountofNumbers10000, aqtime10000);
		*/
		/*
		AdtArray klauckTest = AdtContainerFactory.adtArray();
		klauckTest = Generator.importNums("zahlen_klauck.dat");
		
		System.out.println("Länge des Arrays: "+klauckTest.length());
		
		long isKlauckTest = Sorter.insertionsortTime(klauckTest, startPos, klauckTest.length());
		exportSingleTime("Klauck IS", "Time", 20013, isKlauckTest);
		
		System.out.println("Insertionsort (IS) für Test von Klauck braucht: "+isKlauckTest+"ms");
		
		System.out.println("Ready for Excel Import");
		*/
		/*
		AdtArray klauckTest1 = AdtContainerFactory.adtArray();
		klauckTest1 = Generator.importNums("zahlen_klauck.dat");
		long start1 = System.currentTimeMillis();
		long[] isKlauckTest1 = Sorter.quicksortTime(klauckTest1, (a,l,r)->l);
		exportArray("Klauck QS Links", "Time", 20013, isKlauckTest1);
		long end1 = System.currentTimeMillis();
		System.out.println("Quicksort Links für Test von Klauck braucht: "+(end1-start1)+"ms");
		
		AdtArray klauckTest2 = AdtContainerFactory.adtArray();
		klauckTest2 = Generator.importNums("zahlen_klauck.dat");
		long start2 = System.currentTimeMillis();
		System.out.println("Länge des Arrays: "+klauckTest2.length());
		long[] isKlauckTest2 = Sorter.quicksortTime(klauckTest2, (a,l,r)->r);
		exportArray("Klauck QS Rechts", "Time", 20013, isKlauckTest2);
		long end2 = System.currentTimeMillis();
		System.out.println("Quicksort Links für Test von Klauck braucht: "+(end2-start2)+"ms");
		
		AdtArray klauckTest3 = AdtContainerFactory.adtArray();
		klauckTest3 = Generator.importNums("zahlen_klauck.dat");
		long start3 = System.currentTimeMillis();
		System.out.println("Länge des Arrays: "+klauckTest3.length());
		long[] isKlauckTest3 = Sorter.quicksortTime(klauckTest3, (a,l,r)->Sorter.median(a, l, r));
		exportArray("Klauck QS Median", "Time", 20013, isKlauckTest3);
		long end3 = System.currentTimeMillis();
		System.out.println("Quicksort Links für Test von Klauck braucht: "+(end3-start3)+"ms");

		AdtArray klauckTest4 = AdtContainerFactory.adtArray();
		klauckTest4 = Generator.importNums("zahlen_klauck.dat");
		long start4 = System.currentTimeMillis();
		System.out.println("Länge des Arrays: "+klauckTest4.length());
		long[] isKlauckTest4 = Sorter.quicksortTime(klauckTest4, (a,l,r)->Sorter.random(l, r));
		exportArray("Klauck QS Random", "Time", 20013, isKlauckTest4);
		long end4 = System.currentTimeMillis();
		System.out.println("Quicksort Links für Test von Klauck braucht: "+(end4-start4)+"ms");
		
		System.out.println("Ready for Excel Import");
		*/
	}
	
	public static void exportSingleTime(String methode, String art, int numberamount, Long time){
		final String leer = " ";
		final String semicolon = ";";
		final String dateiTyp = ".csv";
		String filename = ("Benchmark "+methode+leer+art+leer+numberamount+dateiTyp);
		File file = new File(filename);
		
		try(BufferedWriter out= new BufferedWriter(new FileWriter(file))) {
			file.createNewFile();
			out.write(methode+semicolon+numberamount+semicolon+time+semicolon+0+semicolon+0+semicolon+0);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void exportArray(String methode, String art, int numberamount, long[] array){
		final String leer = " ";
		final String semicolon = ";";
		final String dateiTyp = ".csv";
		String filename = ("Benchmark "+methode+leer+art+leer+numberamount+dateiTyp);
		File file = new File(filename);
		
		try(BufferedWriter out= new BufferedWriter(new FileWriter(file))) {
			file.createNewFile();
			int wt = 0; //written Tupels
			out.write(methode+semicolon+numberamount);
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
			
			for(int i = 0;i <= 6; i++){
				if(i >= array.length) {out.write(semicolon+0); wt = wt+1;}
				else {out.write(semicolon+array[i]); wt=wt+1;}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
