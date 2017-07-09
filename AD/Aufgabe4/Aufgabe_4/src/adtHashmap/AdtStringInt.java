package adtHashmap;

public class AdtStringInt {

	private String myWord;
	private int myCount;
	
	/*///////////////////
	 * CONSTRUCTOR
	 *///////////////////
	
	public static AdtStringInt valueOf(String word, int count){
		return new AdtStringInt(word,count);
	}
	
	private AdtStringInt(String word, int count){
		this.myWord = word;
		this.myCount = count;
	}
	
	/*///////////////////
	 * GETTER AND SETTER
	 *///////////////////
	
	public String getMyWord(){return this.myWord;}
	
	public void setMyWord(String word){this.myWord = word;}
	
	public int getMyCount(){return this.myCount;}
	
	public void setMyCount(int count){this.myCount = count;}
	
	/*///////////////////
	 * METHODS
	 *///////////////////	
	
	public String toString(){
		return("Key: " + this.getMyWord() + " Value: " + this.getMyCount());
	}
}
