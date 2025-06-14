package javaTests;

import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static eecalcs.systems.VoltageAC.*;
import static org.junit.jupiter.api.Assertions.*;

class VoltACTest {

	@Test
	void custom_1() {
		//that I can create an exotic voltage, single phase
		VoltageAC vac = VoltageAC.custom("Custom voltage", 125, 1,1,1, VoltageAC.WindingType.C);
		System.out.println(vac);
		assertEquals("Custom voltage", vac.getName());
		assertEquals(125, vac.getVoltage());
		assertEquals(1, vac.getPhases());
		assertEquals(1, vac.getHots());
		assertTrue(vac.hasNeutral());
		assertEquals(2, vac.getWires());
		assertEquals(1, vac.getFactor());
	}

	@Test
	void custom_2() {
		//that I can create an exotic voltage, 3 phase
		VoltageAC vac = VoltageAC.custom("Custom voltage 2", 217, 3,3,1, VoltageAC.WindingType.Y);
		System.out.println(vac);
		assertEquals("Custom voltage 2", vac.getName());
		assertEquals(217, vac.getVoltage());
		assertEquals(3, vac.getPhases());
		assertEquals(3, vac.getHots());
		assertTrue(vac.hasNeutral());
		assertEquals(4, vac.getWires());
		assertEquals(Math.sqrt(3), vac.getFactor(),0.0000001);
	}

	@Test
	void custom_3() {
		//that I create a custom voltage system that exist, the existing one is returned.
		VoltageAC vac = VoltageAC.custom("Custom voltage 3", 208, 1,2,1, VoltageAC.WindingType.Y);
		System.out.println(vac);
		assertEquals(VoltageAC.v208_1ph_3w, vac);
		assertEquals("208/120V 1Ø 3W", vac.getName());
		assertEquals(208, vac.getVoltage());
		assertEquals(1, vac.getPhases());
		assertEquals(2, vac.getHots());
		assertTrue(vac.hasNeutral());
		assertEquals(3, vac.getWires());
		assertEquals(1, vac.getFactor(),0.0000001);
	}

	@Test
	void hasHotAndNeutralOnly() {
		assertTrue(v120_1ph_2w.hasHotAndNeutralOnly());
		assertTrue(VoltageAC.v277_1ph_2w.hasHotAndNeutralOnly());
		assertFalse(VoltageAC.v480_1ph_3w.hasHotAndNeutralOnly());
	}

	@Test
	void has2HotsOnly() {
		assertTrue(VoltageAC.v480_1ph_2w.has2HotsOnly());

	}

	@Test
	void has2HotsAndNeutralOnly() {
		assertTrue(VoltageAC.v480_1ph_3w.has2HotsAndNeutralOnly());
	}

	@Test
	void testToString() {
		System.out.println(VoltageAC.v208_3ph_4w);
	}

	@Test
	void isNeutralPossiblyCurrentCarrying() {
		assertTrue(v120_1ph_2w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v208_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertTrue(v208_1ph_2wN.isNeutralPossiblyCurrentCarrying());
		assertTrue(v208_1ph_3w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v208_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertFalse(v208_3ph_4w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v240_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertFalse(v240_1ph_3w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v240_3ph_3w::isNeutralPossiblyCurrentCarrying);

		assertFalse(v240_3ph_4w.isNeutralPossiblyCurrentCarrying());
		assertTrue(v277_1ph_2w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v480_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertTrue(v480_1ph_3w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v480_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertFalse(v480_3ph_4w.isNeutralPossiblyCurrentCarrying());
		assertTrue(v347_1ph_2w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v600_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertTrue(v600_1ph_3w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v600_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertFalse(v600_3ph_4w.isNeutralPossiblyCurrentCarrying());
		assertTrue(v115_1ph_2w.isNeutralPossiblyCurrentCarrying());
		assertThrows(IllegalStateException.class, v115_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v200_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v200_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v230_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v230_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v460_1ph_2w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v460_3ph_3w::isNeutralPossiblyCurrentCarrying);
		assertThrows(IllegalStateException.class, v575_3ph_3w::isNeutralPossiblyCurrentCarrying);
	}

	@Test
	void getVoltageToNeutral() {
		assertEquals(120, v120_1ph_2w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v208_1ph_2w::getVoltageToNeutral);
		assertEquals(208, v208_1ph_2wN.getVoltageToNeutral());
		assertEquals(120, v208_1ph_3w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v208_3ph_3w::getVoltageToNeutral);
		assertEquals(120, v208_3ph_4w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v240_1ph_2w::getVoltageToNeutral);
		assertEquals(120, v240_1ph_3w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v240_3ph_3w::getVoltageToNeutral);
		assertEquals(120, v240_3ph_4w.getVoltageToNeutral());
		assertEquals(277, v277_1ph_2w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v480_1ph_2w::getVoltageToNeutral);
		assertEquals(277, v480_1ph_3w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v480_3ph_3w::getVoltageToNeutral);
		assertEquals(277, v480_3ph_4w.getVoltageToNeutral());
		assertEquals(347, v347_1ph_2w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v600_1ph_2w::getVoltageToNeutral);
		assertEquals(347, v600_1ph_3w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v600_3ph_3w::getVoltageToNeutral);
		assertEquals(347, v600_3ph_4w.getVoltageToNeutral());
		assertEquals(115, v115_1ph_2w.getVoltageToNeutral());
		assertThrows(IllegalStateException.class, v115_3ph_3w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v200_1ph_2w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v200_3ph_3w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v230_1ph_2w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v230_3ph_3w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v460_1ph_2w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v460_3ph_3w::getVoltageToNeutral);
		assertThrows(IllegalStateException.class, v575_3ph_3w::getVoltageToNeutral);
	}
}