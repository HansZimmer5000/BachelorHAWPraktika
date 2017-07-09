package adtHashmap.strategie;

import adtHashmap.AdtHashmap;

public class DoubleHashing implements IStrategie {
	private String k;
	private int size;
	//Erforderlich, da hier ein weiterer hashcode benutzt wird, welcher KEY und die Größe der Hashtabelle wissen muss
	public DoubleHashing(String k, int size){
		this.k = k;
		this.size = size;
	}

	@Override
	public int s(int j, String k){
		int ascii = 0;
		for(int i = 0; i<k.length(); i++){
			ascii += k.charAt(i);
		}
		int h = 1+(ascii % (this.size - 2));
		return j * h;
	}

}
