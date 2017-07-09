package adtHashmap;

import adtHashmap.strategie.IStrategie;

public class AdtFactory {

	private AdtFactory(){}
	
	public static AdtHashmap newAdtHashmap(int size, IStrategie strategie){
		return AdtHashmapImpl.valueOf(size, strategie);
	}
}
