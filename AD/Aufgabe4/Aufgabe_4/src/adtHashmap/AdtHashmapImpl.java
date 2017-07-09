package adtHashmap;

import java.util.ArrayList;
import java.util.Objects;

import adtHashmap.strategie.IStrategie;
import adtHashmap.strategie.LinearesSondieren;

public class AdtHashmapImpl implements AdtHashmap{
	//Für strategie 
	private IStrategie strategie;

	private int mySize;
//	private String myStrategy;
	private AdtStringInt[] myStringIntCouples;
	
	private int[] tag;
	private final int frei = 0;
	private final int belegt = 1;
	private final int entfernt = 2;
	
	// The first Element is the default strategy if the inputed strategy isnt implemented
//	private String[] myLegalStrategies = new String[]{"L", "Q", "B"};
	
	/*/////////////////
	 * CONSTUCOTR
	 */////////////////
	public static AdtHashmap valueOf(int size, IStrategie strategie){
		return new AdtHashmapImpl(size, strategie);
	}
	
	private AdtHashmapImpl(int size, IStrategie strategie){
		myStringIntCouples = new AdtStringInt[size];
		mySize = size;
		this.strategie = strategie;
		
		//Initialisierung der Hashtabelle auf Null
		for(int i = 0; i<size; i++){
			myStringIntCouples[i] = null;
		}
		
//		boolean strategyIsOK = false;
//		int i = 0;
//		while(i < myLegalStrategies.length && !strategyIsOK){
//			if(strategy.equals(myLegalStrategies[i])) strategyIsOK = true;
//			i++;
//		}
//		
//		if(strategyIsOK){
//			myStrategy = strategy;
//		} else {
//			myStrategy = myLegalStrategies[0];
//		}
	}
	
	/*/////////////////
	 * PROBE // SONDIERUNGEN
	 */////////////////
	
//	private void sondierungLinear(AdtStringInt newElem){
//		int myLength = myStringIntCouples.size()-1, diff = mySize - myLength;
//		
//		if(diff > 0){
//			myStringIntCouples.add(diff, newElem);
//		} else {
//			System.out.println("Voll.");
//		}
//	}
	
	/*/////////////////
	 * METHODS
	 */////////////////
	@Override
	public AdtHashmap insert(String word) {
		int j = 1;
		int i = h(word);
		while(j < mySize && tag[i] == belegt){
			i = (h(word) - s(j,word)) % mySize;
			j++;
		}
		if(tag[i] == frei){
			myStringIntCouples[i] = AdtStringInt.valueOf(word, 0);
		}else{
			System.out.println("Tabelle ist Voll!!!");
		}
		
//		int wordIndex = find(word);
//		AdtStringInt tmpCouple;
//		boolean containsKey = false;
//		
//		if(wordIndex >= 0) containsKey = true;
//		
//		if(!containsKey){
//			// Add new couple
//			AdtStringInt newCouple = AdtStringInt.valueOf(word, 1);
//			myStringIntCouples.add(newCouple);
//		} else {
//			// Add +1 on the Value of the couple
//			tmpCouple = myStringIntCouples.get(wordIndex);
//			tmpCouple.setMyCount(tmpCouple.getMyCount()+1);
//		}
		return this;
	}

	@Override
	public int find(String word) {
		int j = 1;
		int i = h(word);		//hashadresse
		
		while((j < mySize) && (tag[i] != frei || Objects.equals(myStringIntCouples[i], word))){
			i = (h(word) - s(j,word)) % mySize;
			j++;
		}
		if(Objects.equals(myStringIntCouples[i], word)){
			return myStringIntCouples[i].getMyCount();			
		}
		
		return -1;		// falls nicht gefunden
		
//		int i, myStringIntCoupleslength = myStringIntCouples.size();
//		AdtStringInt tmpCouple;
//		
//		for(i = 0; i < myStringIntCoupleslength; i++){
//			tmpCouple = myStringIntCouples.get(i);
//			if(word.equals(tmpCouple.getMyWord())){
//				return i;
//			}
//		}
//		
//		return -1;
	}

	@Override
	public AdtHashmap startProgramm(String strategy, String filename) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int s(int j, String k){
		return this.strategie.s(j, k);
	}
	
	public int h(String k){
		int ascii = 0;
		for(int i = 0; i<k.length(); i++){
			ascii += k.charAt(i);
		}
		return ascii % mySize;
	}
	
	public int h1(String k){
		int ascii = 0;
		for(int i = 0; i<k.length(); i++){
			ascii += k.charAt(i);
		}
		return 1 + (ascii % (mySize - 2));
	}
	
	
	
}