package javaTests;

import eecalcs.conduits.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialTest {

	@Test
	void isMagnetic() {
		assertFalse(Material.PVC.isMagnetic());
		assertFalse(Material.ALUMINUM.isMagnetic());
		assertTrue(Material.STEEL.isMagnetic());
	}
}