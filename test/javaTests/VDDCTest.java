package javaTests;

import eecalcs.conductors.Metal;
import org.junit.jupiter.api.Test;
import eecalcs.conductors.Size;
import static eecalcs.voltagedrop.VDDC.*;
import static org.junit.jupiter.api.Assertions.*;

class VDDCTest {

	@Test
	void test_getVoltageDrop() {
		assertEquals(3.22, getVoltageDropPercent(120.0, 10.0, Size.AWG_12, 100.0, 1, Metal.COPPER), 0.01);
		assertEquals(2.10, getVoltageDropPercent(120.0, 10.0, Size.AWG_10, 100.0, 1, Metal.COPPERCOATED), 0.01);
		assertEquals(0.42, getVoltageDropPercent(120.0, 10.0, Size.AWG_1, 100.0, 1, Metal.ALUMINUM), 0.01);
		assertEquals(1.46, getVoltageDropPercent(480.0, 460.0, Size.AWG_4$0, 250.0, 2, Metal.COPPER), 0.01);

	}

	@Test
	void test_getMinSizeForMaxVD() {
		assertEquals(Size.AWG_12, getMinSizeForMaxVD(120.0, 10.0, 3.22, 100.0, 1, Metal.COPPER));
		assertEquals(Size.AWG_10, getMinSizeForMaxVD(120.0, 10.0, 2.10, 100.0, 1, Metal.COPPERCOATED));
		assertEquals(Size.AWG_1, getMinSizeForMaxVD(120.0, 10.0, 0.42, 100.0, 1, Metal.ALUMINUM));
		assertEquals(Size.AWG_4$0, getMinSizeForMaxVD(480.0, 460.0, 1.46, 250.0, 2, Metal.COPPER));
		assertEquals(Size.AWG_14, getMinSizeForMaxVD(100.0, 15.0, 3.0, 32.57, 1, Metal.COPPER));
		assertNull(getMinSizeForMaxVD(100, 600.0, 7.0, 1000.0, 1, Metal.COPPER));
	}

	@Test
	void test_getMaxLengthForVD() {
		assertEquals(38.86, getMaxLengthForVD(100.0, 20.0, Size.AWG_12, 3.0, 1, Metal.COPPER), 0.01);
		assertEquals(65.36, getMaxLengthForVD(100.0, 60.0, Size.AWG_6, 2.0, 2, Metal.COPPERCOATED), 0.01);
		assertEquals(237.15, getMaxLengthForVD(240.0, 100.0, Size.AWG_1, 5.0, 1, Metal.ALUMINUM), 0.01);
		assertEquals(933.13, getMaxLengthForVD(480.0, 2000.0, Size.KCMIL_2000, 5.0, 1, Metal.COPPER), 0.01);
	}

}