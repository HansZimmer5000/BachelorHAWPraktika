package sort;

import java.util.Random;

import javax.swing.plaf.synth.SynthSpinnerUI;

import adt.implementations.AdtContainerFactory;
import adt.interfaces.AdtArray;

public class Sorter{
	
	@FunctionalInterface
	public interface PivotMethod{
		int getPivotIndex(AdtArray arr, int start, int end);
	}
	
	

	private static final int toIns = 80;
	
	private static long qstime = 0;
	private static long qetime = 0;
	private static long qendtime = 0;
	
	private static long istime = 0;
	private static long ietime = 0;
	private static long iendtime = 0;
	
	private static long qread = 0;
	private static long qwrite = 0;
	private static long iread = 0;
	private static long iwrite = 0;
	
	private Sorter(){
		//nicht instanziierbar
	}
	
	public static void insertionsort(AdtArray array1, int startPos, int endPos) {
		int elem2;
		int whilePos;
		int whileElem1;
		
		for(int actualPos = startPos; actualPos <= endPos; actualPos++){
			whilePos = actualPos;
			
			elem2 = array1.get(actualPos);
			
			while(array1.get(whilePos-1) > elem2 && whilePos > startPos){
				whileElem1 = array1.get(whilePos-1);
				
				array1.set(whilePos, whileElem1);
				
				whilePos--;
			}
			
			array1.set(whilePos, elem2);
		}
	}
	
	public static void swap(AdtArray array, int pos1, int pos2){
		if(pos1 != pos2){
			int tmp = array.get(pos1);
			array.set(pos1, array.get(pos2));
			array.set(pos2, tmp);
		}
	}
	
	private static boolean checkSort(AdtArray array1){
		for(int i = 1; i <= array1.length(); i++){
			if(array1.get(i) < array1.get(i-1)){
				return false;
			}
		}
		return true;
	}
	
	public static void quicksort(AdtArray array, PivotMethod pm){
		sort(array,pm, 0,array.length());
	}

	private static void sort(AdtArray array,PivotMethod pm, int l, int r) {
		if((r-l) < toIns){
			insertionsort(array, l, r);
		}else if(l < r){
			int pivotIndex = pm.getPivotIndex(array,l, r);
			if(pivotIndex > r || pivotIndex <l) throw new IllegalArgumentException();
			int i = quickSwap(array,pivotIndex, l, r);
			
			sort(array,pm,l,i-1);
			sort(array,pm,i+1,r);
		}
		
	}

	private static int quickSwap(AdtArray array, int pivotIndex, int l, int r) {
		int i = l;
		int j = r-1;
		swap(array,pivotIndex,r);
		int pivot = array.get(r);
		
		while(i<=j){
			while((array.get(i) <= pivot) && (i<r)){
				i++;
			}
			while((l<=j) && (array.get(j) > pivot)){
				j--;
			}
			if(i < j){
				swap(array,i,j);
			}
		}
		swap(array,i,r);
		return i;
	}
	
	public static int random(int l, int r){
		int i = l+1 + (int)(Math.random() * (r-l));
		return i;
	}
	
	public static int median(AdtArray arr, int l, int r){
		int mid = (l+r) / 2;
		int a = arr.get(l);
		int b = arr.get(mid);
		int c = arr.get(r);
		
		if(a>b)
		{
		    if(b>c)
		    {
		        return mid;
		    }
		    else if(c>a)
		    {
		        return a;
		    }
		    else
		    {
		        return c;
		    }
		}
		else
		{
		    if(b<c)
		    {
		        return mid;
		    }
		    else if(c<a)
		    {
		        return a;
		    }
		    else
		    {
		        return r;
		    }
		}
	}
	
	
	/*#####################################################################################################################################*/
	//									Laufzeit Messen
	/*#####################################################################################################################################*/
	public static long insertionsortTime(AdtArray array1, int startPos, int endPos) {
		istime = System.currentTimeMillis();
		int elem2;
		int whilePos;
		int whileElem1;
		
		for(int actualPos = startPos; actualPos <= endPos; actualPos++){
			whilePos = actualPos;
			
			elem2 = array1.get(actualPos);
			
			while(array1.get(whilePos-1) > elem2 && whilePos > startPos){
				whileElem1 = array1.get(whilePos-1);
				
				array1.set(whilePos, whileElem1);
				
				whilePos--;
			}
			
			array1.set(whilePos, elem2);
		}
		ietime = System.currentTimeMillis();
		return (ietime - istime);
	}
	
	public static long[] quicksortTime(AdtArray array, PivotMethod pm){
		qstime = System.currentTimeMillis();
		sortTime(array,pm,0,array.length());
		qetime = System.currentTimeMillis();
		long[] end = new long[2];
		end[1] = iendtime;
		end[0] = qetime - qstime - iendtime;
		
		return end;
	}
	
	private static void sortTime(AdtArray array,PivotMethod pm, int l, int r) {
		if((r-l) < toIns){
			iendtime = insertionsortTime(array, l, r)+iendtime;
		}else if(l < r){
			int pivotIndex = pm.getPivotIndex(array,l, r);
			int i = quickSwap(array,pivotIndex, l, r);
			
			sortTime(array,pm,l,i-1);
			sortTime(array,pm,i+1,r);
		}
		
	}
	
	/*############################################################################################*/
	//					Zugriffe zählen
	/*############################################################################################*/
	

	public static long[] insertionsortSteps(AdtArray array1, int startPos, int endPos){
		int elem2;
		int whilePos;
		int whileElem1;
		
		for(int actualPos = startPos; actualPos <= endPos; actualPos++){
			whilePos = actualPos;
			
			elem2 = array1.get(actualPos);
			iread++;
			
			while(array1.get(whilePos-1) > elem2 && whilePos > startPos){
				iread++;
				whileElem1 = array1.get(whilePos-1);
				iread++;
				
				array1.set(whilePos, whileElem1);
				iwrite++;
				
				whilePos--;
			}
			iread++;					//Im while wird gelesen... für den Fall dass die while nicht ausgeführt wird
			
			array1.set(whilePos, elem2);
			iwrite++;
		}
		long[] end = new long[2];
		end[0] = iread;
		end[1] = iwrite;
		return end;
	}
	
	public static long[] quicksortSteps(AdtArray array, PivotMethod pm){
		sortSteps(array,pm, 0,array.length());
		long[] end = new long[4];
		end[0] = qread;
		end[1] = qwrite;
		end[2] = iread;
		end[3] = iwrite;
		return end;
	}
	
	public static void sortSteps(AdtArray array,PivotMethod pm, int l, int r){
		if((r-l) < toIns){
			insertionsortSteps(array, l, r);
		}else if(l < r){
			int pivotIndex = pm.getPivotIndex(array,l, r);
			int i = quickSwapSteps(array,pivotIndex, l, r);
			
			sortSteps(array,pm,l,i-1);
			sortSteps(array,pm,i+1,r);
		}
	}
	
	public static int quickSwapSteps(AdtArray array, int pivotIndex, int l, int r){
		int i = l;
		int j = r-1;
		swapSteps(array,pivotIndex,r);
		int pivot = array.get(r);
		qread++;
		
		while(i<=j){
			while((array.get(i) <= pivot) && (i<r)){
				qread++;
				i++;
			}
			qread++;
			while((l<=j) && (array.get(j) > pivot)){
				qread++;
				j--;
			}
			qread++;
			if(i < j){
				swapSteps(array,i,j);
			}
		}
		swapSteps(array,i,r);
		return i;
	}
	
	private static void swapSteps(AdtArray array, int pos1, int pos2){
		if(pos1 != pos2){
			int tmp = array.get(pos1);
			qread++;
			array.set(pos1, array.get(pos2));
			qread++;
			qwrite++;
			array.set(pos2, tmp);
			qwrite++;
		}
	}
}
