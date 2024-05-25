package javaTests;

import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import eecalcs.conduits.Material;
import org.junit.jupiter.api.Test;

import static eecalcs.voltagedrop.VDAC.*;
import static org.junit.jupiter.api.Assertions.*;

class VDACTest {

	@Test
		//that we get the right voltage drop percentage
	void test_getVoltageDrop() {
		assertEquals(2.74, getVoltageDropPercent(120.0, 1, 10.0, 0.8, true, Size.AWG_12, 100.0, 1, Metal.COPPER, Material.STEEL), 0.01);
		assertEquals(2.61, getVoltageDropPercent(120.0, 1, 10.0, 0.8, false, Size.AWG_12, 100.0, 1, Metal.COPPER, Material.STEEL), 0.01);

		assertEquals(3.80, getVoltageDropPercent(277.0, 3, 35.0, 0.75, true, Size.AWG_10, 225.0, 2, Metal.ALUMINUM, Material.PVC), 0.01);
		assertEquals(3.64, getVoltageDropPercent(277.0, 3, 35.0, 0.75, false, Size.AWG_10, 225.0, 2, Metal.ALUMINUM, null), 0.01); //free air

		assertEquals(2.83, getVoltageDropPercent(480.0, 3, 500.0, 0.88, true, Size.KCMIL_250, 225.0, 1, Metal.COPPER, Material.ALUMINUM), 0.01);
		assertEquals(1.26, getVoltageDropPercent(480.0, 3, 500.0, 0.88, false, Size.KCMIL_250, 225.0, 1, Metal.COPPER, Material.ALUMINUM), 0.01);

		assertEquals(3.98, getVoltageDropPercent(240.0, 1, 185.0, 1.0, true, Size.KCMIL_1000, 1350.0, 1, Metal.COPPER, Material.STEEL), 0.01);
		assertEquals(3.98, getVoltageDropPercent(240.0, 1, 185.0, 1.0, false, Size.KCMIL_1000, 1350.0, 1, Metal.COPPER, Material.STEEL), 0.01);

		assertEquals(-1.0, getVoltageDropPercent(120.0, 1, 100.0, 1.0, true, Size.AWG_1, 4680, 1, Metal.ALUMINUM, Material.STEEL), 0.01);
	}

	@Test
	void test_getMinSizeForMaxVD() {
		assertEquals( Size.AWG_14, getMinSizeForMaxVD(120.0, 1, 15.0, 1.0, true, 3.001, 38.71, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_12, getMinSizeForMaxVD(120.0, 1, 20.0, 1.0, true, 3.001,     45.0, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_10, getMinSizeForMaxVD(120.0, 1, 30.0, 1.0, true, 3.001,     50.0, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_8, getMinSizeForMaxVD(120.0, 1, 40.0, 1.0, true, 3.001,  57.69, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_6, getMinSizeForMaxVD(208.0, 1, 55.0, 1.0, true, 3.001,  115.76, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_4, getMinSizeForMaxVD(208.0, 1, 70.0, 1.0, true, 3.001,  143.74, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_3, getMinSizeForMaxVD(208.0, 1, 85.0, 1.0, true, 3.001,  146.76, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_2, getMinSizeForMaxVD(208.0, 1, 95.0, 1.0, true, 3.001, 164.11, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_1, getMinSizeForMaxVD(208.0, 1, 130.0, 0.8, true, 3.001, 147.86, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_1$0, getMinSizeForMaxVD(208.0, 1, 150.0, 0.8, true, 3.001, 161.18, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_2$0, getMinSizeForMaxVD(208.0, 1, 175.0, 0.8, true, 3.001, 158.59, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_3$0, getMinSizeForMaxVD(208.0, 1, 200.0, 0.8, true, 3.001, 165.25, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.AWG_4$0, getMinSizeForMaxVD(208.0, 1, 230.0, 0.8, true, 3.001, 167.47, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_250, getMinSizeForMaxVD(480.0, 3, 255.0, 0.8, true, 3.001, 438.16, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_300, getMinSizeForMaxVD(480.0, 3, 285.0, 0.8, true, 3.001, 437.85, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_350, getMinSizeForMaxVD(480.0, 3, 310.0, 0.8, true, 3.001, 437.94, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_400, getMinSizeForMaxVD(480.0, 3, 335.0, 0.8, true, 3.001, 431.98, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_500, getMinSizeForMaxVD(480.0, 3, 380.0, 0.8, true, 3.001, 420.15, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_600, getMinSizeForMaxVD(480.0, 3, 420.0, 0.8, true, 3.001, 404.83, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_700, getMinSizeForMaxVD(480.0, 3, 460.0, 0.8, true, 3.001, 389.19, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_750, getMinSizeForMaxVD(480.0, 3, 475.0, 0.8, true, 3.001, 382.78, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_800, getMinSizeForMaxVD(480.0, 3, 490.0, 0.8, true, 3.001, 376.97, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_900, getMinSizeForMaxVD(480.0, 3, 520.0, 0.8, true, 3.001, 366.92, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_1000, getMinSizeForMaxVD(480.0, 3, 545.0, 0.8, true, 3.001, 362.01, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_1250, getMinSizeForMaxVD(480.0, 3, 590.0, 0.8, true, 3.001, 357.72, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_1500, getMinSizeForMaxVD(480.0, 3, 625.0, 0.8, true, 3.001, 352.99, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_1750, getMinSizeForMaxVD(480.0, 3, 650.0, 0.8, true, 3.001, 345.58, 1, Metal.COPPER,
				Material.STEEL));
		assertEquals( Size.KCMIL_2000, getMinSizeForMaxVD(480.0, 3, 665.0, 0.8, true, 3.001, 347.73, 1, Metal.COPPER,
				Material.STEEL));
	}

	@Test
	void test_getMaxLengthForVD() {
		assertEquals( 38.71, getMaxLengthForVD(120.0, 1, 15.0, 1.0, true, Size.AWG_14,3.00, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(45.0 , getMaxLengthForVD(120.0, 1, 20.0, 1.0, true, Size.AWG_12, 3.0,     1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(50.0 , getMaxLengthForVD(120.0, 1, 30.0, 1.0, true, Size.AWG_10, 3.0,     1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(57.69 , getMaxLengthForVD(120.0, 1, 40.0, 1.0, true, Size.AWG_8, 3.0,  1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(115.76 , getMaxLengthForVD(208.0, 1, 55.0, 1.0, true, Size.AWG_6, 3.0,  1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(143.74 , getMaxLengthForVD(208.0, 1, 70.0, 1.0, true, Size.AWG_4, 3.0,  1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(146.76 , getMaxLengthForVD(208.0, 1, 85.0, 1.0, true, Size.AWG_3, 3.0,  1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(164.11 , getMaxLengthForVD(208.0, 1, 95.0, 1.0, true, Size.AWG_2, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(147.86 , getMaxLengthForVD(208.0, 1, 130.0, 0.8, true, Size.AWG_1, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(161.18 , getMaxLengthForVD(208.0, 1, 150.0, 0.8, true, Size.AWG_1$0, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(158.59 , getMaxLengthForVD(208.0, 1, 175.0, 0.8, true, Size.AWG_2$0, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(165.25 , getMaxLengthForVD(208.0, 1, 200.0, 0.8, true, Size.AWG_3$0, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(167.47 , getMaxLengthForVD(208.0, 1, 230.0, 0.8, true, Size.AWG_4$0, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(438.16 , getMaxLengthForVD(480.0, 3, 255.0, 0.8, true, Size.KCMIL_250, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(437.85 , getMaxLengthForVD(480.0, 3, 285.0, 0.8, true, Size.KCMIL_300, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(437.94 , getMaxLengthForVD(480.0, 3, 310.0, 0.8, true, Size.KCMIL_350, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(431.98 , getMaxLengthForVD(480.0, 3, 335.0, 0.8, true, Size.KCMIL_400, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(420.15 , getMaxLengthForVD(480.0, 3, 380.0, 0.8, true, Size.KCMIL_500, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(404.83 , getMaxLengthForVD(480.0, 3, 420.0, 0.8, true, Size.KCMIL_600, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(389.19 , getMaxLengthForVD(480.0, 3, 460.0, 0.8, true, Size.KCMIL_700, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(382.78 , getMaxLengthForVD(480.0, 3, 475.0, 0.8, true, Size.KCMIL_750, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(376.97 , getMaxLengthForVD(480.0, 3, 490.0, 0.8, true, Size.KCMIL_800, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(366.92 , getMaxLengthForVD(480.0, 3, 520.0, 0.8, true, Size.KCMIL_900, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(362.01 , getMaxLengthForVD(480.0, 3, 545.0, 0.8, true, Size.KCMIL_1000, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(357.72 , getMaxLengthForVD(480.0, 3, 590.0, 0.8, true, Size.KCMIL_1250, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(352.99 , getMaxLengthForVD(480.0, 3, 625.0, 0.8, true, Size.KCMIL_1500, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(345.58 , getMaxLengthForVD(480.0, 3, 650.0, 0.8, true, Size.KCMIL_1750, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
		assertEquals(347.73 , getMaxLengthForVD(480.0, 3, 665.0, 0.8, true, Size.KCMIL_2000, 3.0, 1, Metal.COPPER,
				Material.STEEL), 0.01);
	}
}
