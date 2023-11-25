package test.java;

import eecalcs.circuits.DSProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DSPropertiesTest {

	@Test
	void getRating() {
		assertEquals(30, DSProperties.getRating(0));
		assertEquals(30, DSProperties.getRating(-5));
		assertEquals(60, DSProperties.getRating(-35));
		assertEquals(600, DSProperties.getRating(401));
		assertEquals(1200, DSProperties.getRating(801));
		assertEquals(1200, DSProperties.getRating(1200));
		assertEquals(0, DSProperties.getRating(1201));
	}
}