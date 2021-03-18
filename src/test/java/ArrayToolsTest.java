package test.java;

import eecalcs.circuits.OCPD;
import org.junit.jupiter.api.Test;
import tools.ArrayTools;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayToolsTest {

	@Test
	void getIndexOf(){
		Integer[] arr = {2,4,6,8,10};

		assertEquals(0, ArrayTools.getIndexOf(arr, 2));
		assertEquals(4, ArrayTools.getIndexOf(arr, 10));
		assertEquals(-1, ArrayTools.getIndexOf(arr, 11));

		assertEquals(0, ArrayTools.getIndexOf(OCPD.getStandardRatings(),15));
		assertEquals(36, ArrayTools.getIndexOf(OCPD.getStandardRatings(),
				6000));
		assertEquals(-1, ArrayTools.getIndexOf(OCPD.getStandardRatings(),
				599));

	}

}