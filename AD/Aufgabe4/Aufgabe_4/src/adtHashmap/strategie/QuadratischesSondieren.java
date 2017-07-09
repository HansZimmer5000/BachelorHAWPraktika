package adtHashmap.strategie;

import adtHashmap.AdtHashmap;

public class QuadratischesSondieren implements IStrategie{

	@Override
	public int s(int j, String k){
		int x =(int) Math.ceil((double) j / 2.0);
		return (x * x) * ((int) Math.pow(-1.0, (double) j));
	}
}
