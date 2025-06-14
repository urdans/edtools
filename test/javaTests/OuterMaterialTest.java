package javaTests;

import eecalcs.conduits.OuterMaterial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OuterMaterialTest {

	@Test
	void isMagnetic() {
		assertFalse(OuterMaterial.PVC.isMagnetic());
		assertFalse(OuterMaterial.ALUMINUM.isMagnetic());
		assertTrue(OuterMaterial.STEEL.isMagnetic());
	}
}