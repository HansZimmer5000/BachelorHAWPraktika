package sort.test;

import static org.junit.Assert.*;

import org.junit.Test;

import adt.implementations.AdtContainerFactory;
import adt.interfaces.AdtArray;
import sort.Generator;

public class GeneratorTest {

	@Test
	public void newSortnumTest(){
		Generator.sortnum(100);
		
		AdtArray array1 = AdtContainerFactory.adtArray();
		
		array1 = Generator.importNums("zahlen.dat");
		
		//mit while alle elem durchgehen
		// isin auch noch testen
		Generator.isin(array1, 1);
	}

}
