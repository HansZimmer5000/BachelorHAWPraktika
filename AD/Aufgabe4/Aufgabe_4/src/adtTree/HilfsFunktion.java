package adtTree;

import adt.implementations.*;

public class HilfsFunktion {


	public static int max(int a, int b){
		if(a < b){
			return b;
		}else{
			return a;
		}
	}
	
	public static int hoehe(Knoten x){
		if(x != null){
			return x.getHoehe();
		}
		return 0;
	}

	
	
}
