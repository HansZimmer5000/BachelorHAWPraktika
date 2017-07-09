package adtTree;

import adt.interfaces.AdtArray;
import adt.interfaces.AdtContainer;
import sort.Generator;

public class Benchmark {
	public static void main(String[] args){
		
		//Random sorts
		AdtArray arr100 = adt.implementations.AdtContainerFactory.adtArray();
		AdtArray arr1000 = adt.implementations.AdtContainerFactory.adtArray();
		AdtArray arr10000 = adt.implementations.AdtContainerFactory.adtArray();
//		AdtArray arr100000 = adt.implementations.AdtContainerFactory.adtArray();
		
		AdtArray leftarr100 = adtTree.generate.Generator.importNums("sortnumleft100.dat");
		AdtArray leftarr1000 = adtTree.generate.Generator.importNums("sortnumleft1000.dat");
		AdtArray leftarr10000 = adtTree.generate.Generator.importNums("sortnumleft10000.dat");
//		AdtArray leftarr100000 = adtTree.generate.Generator.importNums("sortnumleft100000.dat");
		
		AdtArray rightarr100 = adtTree.generate.Generator.importNums("sortnumright100.dat");
		AdtArray rightarr1000 = adtTree.generate.Generator.importNums("sortnumright1000.dat");
		AdtArray rightarr10000 = adtTree.generate.Generator.importNums("sortnumright10000.dat");
//		AdtArray rightarr100000 = adtTree.generate.Generator.importNums("sortnumright100000.dat");
		
		AdtArray hawzahlen = adtTree.generate.Generator.importNums("zahlen_klauck_avl.dat");
		
		//LaufzeitMessungen
		System.out.println("#######  Laufzeit für LeftSorted  ######");
		long x = 0;
		AvlTreeTime lefttime100 = AvlTreeTime.create();
		for(int i = 0; i<=leftarr100.length();i++) x = x + lefttime100.insert(leftarr100.get(i));
		System.out.println("Leftsort : "+(leftarr100.length()+1)+" ELEMS : Time = "+x);
		x = 0;
		AvlTreeTime lefttime1000 = AvlTreeTime.create();
		for(int i = 0; i<=leftarr1000.length();i++) x = x+lefttime1000.insert(leftarr1000.get(i));
		System.out.println("Leftsort : "+(leftarr1000.length()+1)+" ELEMS : Time = "+x);	
		x = 0;
		AvlTreeTime lefttime10000 = AvlTreeTime.create();
		for(int i = 0; i<=leftarr10000.length();i++) x = x+lefttime10000.insert(leftarr10000.get(i));
		System.out.println("Leftsort : "+(leftarr10000.length()+1)+" ELEMS : Time = "+x);
		x = 0;
//		AvlTreeTime lefttime100000 = AvlTreeTime.create();
//		for(int i = 0; i<=leftarr100000.length();i++) x = lefttime100000.insert(leftarr100000.get(i));
//		System.out.println("Leftsort : 100000 ELEMS : Time = "+x);
		
		System.out.println("");
		System.out.println("#######  Laufzeit für RightSorted  ######");
		x = 0;
		AvlTreeTime righttime100 = AvlTreeTime.create();
		for(int i = 0; i<=rightarr100.length();i++) x = x + righttime100.insert(rightarr100.get(i));
		System.out.println("rightsort : "+(rightarr100.length()+1)+" ELEMS : Time = "+x);
		x = 0;
		AvlTreeTime righttime1000 = AvlTreeTime.create();
		for(int i = 0; i<=rightarr1000.length();i++) x = x+righttime1000.insert(rightarr1000.get(i));
		System.out.println("rightsort : "+(rightarr1000.length()+1)+" ELEMS : Time = "+x);	
		x = 0;
		AvlTreeTime righttime10000 = AvlTreeTime.create();
		for(int i = 0; i<=rightarr10000.length();i++) x = x+righttime10000.insert(rightarr10000.get(i));
		System.out.println("rightsort : "+(rightarr10000.length()+1)+" ELEMS : Time = "+x);
		x = 0;
//		AvlTreeTime righttime100000 = AvlTreeTime.create();
//		for(int i = 0; i<=rightarr100000.length();i++) x = righttime100000.insert(rightarr100000.get(i));
//		System.out.println("rightsort : 100000 ELEMS : Time = "+x);
		
		System.out.println("");
		System.out.println("");
		System.out.println("#####Leftsort Zugriffe (lesend,schreibend)#####");
		
		long[] steps = new long[2];
		AvlTreeAccess leftaccess100 = AvlTreeAccess.create();
		for(int i = 0; i<=leftarr100.length();i++) steps = leftaccess100.insert(leftarr100.get(i));
		System.out.println("Leftsort : "+(leftarr100.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
		AvlTreeAccess leftaccess1000 = AvlTreeAccess.create();
		for(int i = 0; i<=leftarr1000.length();i++) steps = leftaccess1000.insert(leftarr1000.get(i));
		System.out.println("Leftsort : "+(leftarr1000.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
		AvlTreeAccess leftaccess10000 = AvlTreeAccess.create();
		for(int i = 0; i<=leftarr10000.length();i++) steps = leftaccess10000.insert(leftarr10000.get(i));
		System.out.println("Leftsort : "+(leftarr10000.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
//		AvlTreeAccess leftaccess100000 = AvlTreeAccess.create();
//		for(int i = 0; i<=leftarr100000.length();i++) steps = leftaccess100000.insert(leftarr100000.get(i));
//		System.out.println("Leftsort : "+(leftarr100000.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
		System.out.println("");
		System.out.println("#####Rightsort Zugriffe (lesend,schreibend)#####");
		
		AvlTreeAccess rightaccess100 = AvlTreeAccess.create();
		for(int i = 0; i<=rightarr100.length();i++) steps = rightaccess100.insert(rightarr100.get(i));
		System.out.println("rightsort : "+(rightarr100.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
		AvlTreeAccess rightaccess1000 = AvlTreeAccess.create();
		for(int i = 0; i<=rightarr1000.length();i++) steps = rightaccess1000.insert(rightarr1000.get(i));
		System.out.println("rightsort : "+(rightarr1000.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
		AvlTreeAccess rightaccess10000 = AvlTreeAccess.create();
		for(int i = 0; i<=rightarr10000.length();i++) steps = rightaccess10000.insert(rightarr10000.get(i));
		System.out.println("rightsort : "+(rightarr10000.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
//		AvlTreeAccess rightaccess100000 = AvlTreeAccess.create();
//		for(int i = 0; i<=rightarr100000.length();i++) steps = rightaccess100000.insert(rightarr100000.get(i));
//		System.out.println("rightsort : "+(rightarr100000.length()+1)+" ELEMS ; "+steps[0]+" : " + steps[1]);
		
		
		System.out.println("");
		System.out.println("");
		System.out.println("#####Leftsort Rotationen#####");
		
		long rot = 0;
		AvlTreeRotation leftrot100 = AvlTreeRotation.create();
		for(int i = 0; i<=leftarr100.length();i++) rot = leftrot100.insert(leftarr100.get(i));
		System.out.println("Leftsort : "+(leftarr100.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
		AvlTreeRotation leftrot1000 = AvlTreeRotation.create();
		for(int i = 0; i<=leftarr1000.length();i++) rot = leftrot1000.insert(leftarr1000.get(i));
		System.out.println("Leftsort : "+(leftarr1000.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
		AvlTreeRotation leftrot10000 = AvlTreeRotation.create();
		for(int i = 0; i<=leftarr10000.length();i++) rot = leftrot10000.insert(leftarr10000.get(i));
		System.out.println("Leftsort : "+(leftarr10000.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
//		AvlTreeRotation leftrot100000 = AvlTreeRotationt.create();
//		for(int i = 0; i<=leftarr100000.length();i++) steps = leftrot100000.insert(leftarr100000.get(i));
//		System.out.println("Leftsort : "+(leftarr100000.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
		System.out.println("");
		System.out.println("#####Rightsort Rotationen#####");
		
		AvlTreeRotation rightrot100 = AvlTreeRotation.create();
		for(int i = 0; i<=rightarr100.length();i++) rot = rightrot100.insert(rightarr100.get(i));
		System.out.println("rightsort : "+(rightarr100.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
		AvlTreeRotation rightrot1000 = AvlTreeRotation.create();
		for(int i = 0; i<=rightarr1000.length();i++) rot = rightrot1000.insert(rightarr1000.get(i));
		System.out.println("rightsort : "+(rightarr1000.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
		AvlTreeRotation rightrot10000 = AvlTreeRotation.create();
		for(int i = 0; i<=rightarr10000.length();i++) rot = rightrot10000.insert(rightarr10000.get(i));
		System.out.println("rightsort : "+(rightarr10000.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
//		AvlTreeRotation rightrot100000 = AvlTreeRotationt.create();
//		for(int i = 0; i<=rightarr100000.length();i++) steps = rightrot100000.insert(rightarr100000.get(i));
//		System.out.println("rightsort : "+(rightarr100000.length()+1)+" ELEMS ; Anzahl Rotationen: "+rot);
		
		System.out.println("");
		System.out.println("");
		System.out.println("#####  Zahlen.dat HAW-Klauck Seite (ca. 20000 Zahlen)  #####");
		
		long time=0;
		AvlTreeTime hawtime = AvlTreeTime.create();
		for(int i = 0; i<=hawzahlen.length();i++) time = hawtime.insert(hawzahlen.get(i));
		System.out.println("Laufzeit beträgt:	"+time);
		
		
		AvlTreeAccess hawsteps = AvlTreeAccess.create();
		for(int i = 0; i<=hawzahlen.length();i++) steps = hawsteps.insert(hawzahlen.get(i));
		System.out.println("Lesend beträgt:	"+steps[0] + "   Schreibend beträgt: "+steps[1]);
		
		AvlTreeRotation hawrot = AvlTreeRotation.create();
		for(int i = 0; i<=hawzahlen.length();i++) rot = hawrot.insert(hawzahlen.get(i));
		System.out.println("Anzahl an Rotationen : "+rot);
	}

}
