package javaTests;

import eecalcs.conductors.*;
import eecalcs.conduits.OuterMaterial;
import eecalcs.loads.PowerFactorType;
import eecalcs.systems.VoltageAC;
import eecalcs.voltagedrop.VoltageDropAC;
import org.junit.jupiter.api.Test;
import static eecalcs.voltagedrop.VoltageDropAC.*;
import static eecalcs.voltagedrop.VoltageDropAC.getMaxLengthForVD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VoltageDropACTest {

	@Test
	void test_getVoltageDropPercent() {
		VoltageDropAC voltageDropAC = new VoltageDropAC();
		voltageDropAC
				.setPowerFactor(0.8)
				.setConductorLength(100)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(2.7, voltageDropAC.getVoltageDropPercent(), 0.01);

		voltageDropAC.increaseNumberOfSets();
		voltageDropAC.increaseNumberOfSets();
		assertEquals(2.7/3, voltageDropAC.getVoltageDropPercent(), 0.01);
		voltageDropAC.decreaseNumberOfSets();
		assertEquals(2.7/2, voltageDropAC.getVoltageDropPercent(), 0.1);
		voltageDropAC.decreaseNumberOfSets();

		voltageDropAC
				.setVoltageAC(VoltageAC.v480_3ph_3w)
				.setLoadCurrent(500)
				.setPowerFactor(0.88)
				.setPowerFactorType(PowerFactorType.LEADING)
				.setConductorSize(Size.KCMIL_250)
				.setConductorLength(225.0)
				.setNumberOfSets(2)
				.setConduitMaterial(OuterMaterial.ALUMINUM);
		assertEquals(0.6, voltageDropAC.getVoltageDropPercent(), 0.01);

		voltageDropAC
				.setVoltageAC(VoltageAC.v120_1ph_2w)
				.setLoadCurrent(100)
				.setPowerFactor(1.0)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setConductorSize(Size.AWG_1)
				.setConductorLength(4680)
				.setNumberOfSets(1)
				.setConductorMetal(ConductiveMetal.ALUMINUM)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(-1, voltageDropAC.getVoltageDropPercent(), 0.01);

		voltageDropAC
				.setVoltageAC(VoltageAC.v120_1ph_2w)
				.setLoadCurrent(10)
				.setPowerFactor(1.0)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setConductorSize(Size.AWG_12)
				.setConductorLength(90.0)
				.setNumberOfSets(1)
				.setConductorMetal(ConductiveMetal.COPPER)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(3.0, voltageDropAC.getVoltageDropPercent(), 0.001);
	}

	@Test
	void test_AC_getVoltageDrop() {
		assertEquals(2.7, getVoltageDropPercent(120.0, 1, 10.0, 0.8, true, Size.AWG_12, 100.0, 1,
				ConductiveMetal.COPPER, OuterMaterial.STEEL), 0.01);
		assertEquals(2.61, getVoltageDropPercent(120.0, 1, 10.0, 0.8, false, Size.AWG_12, 100.0, 1,
				ConductiveMetal.COPPER, OuterMaterial.STEEL), 0.01);

		assertEquals(3.8, getVoltageDropPercent(277.0, 3, 35.0, 0.75, true, Size.AWG_10, 225.0, 2,
				ConductiveMetal.ALUMINUM, OuterMaterial.PVC), 0.01);
		assertEquals(3.6, getVoltageDropPercent(277.0, 3, 35.0, 0.75, false, Size.AWG_10, 225.0, 2,
				ConductiveMetal.ALUMINUM, null), 0.01); //free air

		assertEquals(2.8, getVoltageDropPercent(480.0, 3, 500.0, 0.88, true, Size.KCMIL_250, 225.0, 1,
				ConductiveMetal.COPPER, OuterMaterial.ALUMINUM), 0.01);
		assertEquals(1.3, getVoltageDropPercent(480.0, 3, 500.0, 0.88, false, Size.KCMIL_250, 225.0, 1,
				ConductiveMetal.COPPER, OuterMaterial.ALUMINUM), 0.01);

		assertEquals(4.0, getVoltageDropPercent(240.0, 1, 185.0, 1.0, true, Size.KCMIL_1000, 1350.0, 1,
				ConductiveMetal.COPPER, OuterMaterial.STEEL), 0.01);
		assertEquals(4.0, getVoltageDropPercent(240.0, 1, 185.0, 1.0, false, Size.KCMIL_1000, 1350.0, 1,
				ConductiveMetal.COPPER, OuterMaterial.STEEL), 0.01);

		assertEquals(-1.0, getVoltageDropPercent(120.0, 1, 100.0, 1.0, true, Size.AWG_1, 4680, 1,
				ConductiveMetal.ALUMINUM, OuterMaterial.STEEL), 0.01);
	}

	@Test
	void test_getMinSizeForMaxVD() {
		VoltageDropAC voltageDropAC = new VoltageDropAC();
		voltageDropAC
				.setLoadCurrent(15)
				.setPowerFactor(1)
				.setMaxVDropPercent(3.001)
				.setConductorLength(38.71)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(Size.AWG_14, voltageDropAC.getMinSizeForMaxVD());

		voltageDropAC
				.setVoltageAC(VoltageAC.v480_3ph_3w)
				.setLoadCurrent(665.0)
				.setPowerFactor(0.8)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setMaxVDropPercent(3.0)
				.setConductorLength(347)
				.setNumberOfSets(1)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(Size.KCMIL_2000, voltageDropAC.getMinSizeForMaxVD());

		voltageDropAC
				.setVoltageAC(VoltageAC.v208_1ph_2w)
				.setLoadCurrent(200)
				.setPowerFactor(0.8)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setMaxVDropPercent(3.001)
				.setConductorLength(165.25)
				.setNumberOfSets(1)
				.setConductorMetal(ConductiveMetal.COPPER)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(Size.AWG_3$0, voltageDropAC.getMinSizeForMaxVD());

		voltageDropAC.increaseNumberOfSets();
		assertEquals(Size.AWG_1, voltageDropAC.getMinSizeForMaxVD());
		voltageDropAC.decreaseNumberOfSets();
		assertEquals(Size.AWG_3$0, voltageDropAC.getMinSizeForMaxVD());
		voltageDropAC.decreaseNumberOfSets();//does not decrease to less than 1
		assertEquals(Size.AWG_3$0, voltageDropAC.getMinSizeForMaxVD());

		voltageDropAC
				.setVoltageAC(VoltageAC.v120_1ph_2w)
				.setLoadCurrent(10)
				.setPowerFactor(1.0)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setMaxVDropPercent(3.0)
				.setConductorSize(Size.AWG_12)
				.setConductorLength(90.0)
				.setNumberOfSets(1)
				.setConductorMetal(ConductiveMetal.COPPER)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(Size.AWG_12, voltageDropAC.getMinSizeForMaxVD());
	}

	@Test
	void test_AC_getMinSizeForMaxVD() {
		assertEquals( Size.AWG_14, getMinSizeForMaxVD(120.0, 1, 15.0, 1.0, true, 3.001, 38.71, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_12, getMinSizeForMaxVD(120.0, 1, 20.0, 1.0, true, 3.001,     45.0, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_10, getMinSizeForMaxVD(120.0, 1, 30.0, 1.0, true, 3.001,     50.0, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_8, getMinSizeForMaxVD(120.0, 1, 40.0, 1.0, true, 3.001,  57.69, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_6, getMinSizeForMaxVD(208.0, 1, 55.0, 1.0, true, 3.001,  115.76, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_4, getMinSizeForMaxVD(208.0, 1, 70.0, 1.0, true, 3.001,  143.74, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_3, getMinSizeForMaxVD(208.0, 1, 85.0, 1.0, true, 3.001,  146.76, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_2, getMinSizeForMaxVD(208.0, 1, 95.0, 1.0, true, 3.001, 164.11, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_1, getMinSizeForMaxVD(208.0, 1, 130.0, 0.8, true, 3.001, 147.86, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_1$0, getMinSizeForMaxVD(208.0, 1, 150.0, 0.8, true, 3.001, 161.18, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_2$0, getMinSizeForMaxVD(208.0, 1, 175.0, 0.8, true, 3.001, 158.59, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_3$0, getMinSizeForMaxVD(208.0, 1, 200.0, 0.8, true, 3.001, 165.25, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.AWG_4$0, getMinSizeForMaxVD(208.0, 1, 230.0, 0.8, true, 3.001, 167.47, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_250, getMinSizeForMaxVD(480.0, 3, 255.0, 0.8, true, 3.001, 438.16, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_300, getMinSizeForMaxVD(480.0, 3, 285.0, 0.8, true, 3.001, 437.85, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_350, getMinSizeForMaxVD(480.0, 3, 310.0, 0.8, true, 3.001, 437.94, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_400, getMinSizeForMaxVD(480.0, 3, 335.0, 0.8, true, 3.001, 431.98, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_500, getMinSizeForMaxVD(480.0, 3, 380.0, 0.8, true, 3.001, 420.15, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_600, getMinSizeForMaxVD(480.0, 3, 420.0, 0.8, true, 3.001, 404.83, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_700, getMinSizeForMaxVD(480.0, 3, 460.0, 0.8, true, 3.001, 389.19, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_700, getMinSizeForMaxVD(480.0, 3, 475.0, 0.8, true, 3.0, 382.7757, 1,
				ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_750, getMinSizeForMaxVD(480.0, 3, 490.0, 0.8, true, 3.001, 376.97, 1,
				ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_900, getMinSizeForMaxVD(480.0, 3, 520.0, 0.8, true, 3.001, 366.92, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_1000, getMinSizeForMaxVD(480.0, 3, 545.0, 0.8, true, 3.001, 362.01, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_1250, getMinSizeForMaxVD(480.0, 3, 590.0, 0.8, true, 3.001, 357.72, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_1500, getMinSizeForMaxVD(480.0, 3, 625.0, 0.8, true, 3.001, 352.99, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_1750, getMinSizeForMaxVD(480.0, 3, 650.0, 0.8, true, 3.001, 345.58, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
		assertEquals( Size.KCMIL_2000, getMinSizeForMaxVD(480.0, 3, 665.0, 0.8, true, 3.001, 347.73, 1, ConductiveMetal.COPPER, OuterMaterial.STEEL));
	}

	@Test
	void test_getMaxLengthForVD() {
		VoltageDropAC voltageDropAC = new VoltageDropAC();
		voltageDropAC
				.setLoadCurrent(15)
				.setPowerFactor(1)
				.setConductorSize(Size.AWG_14)
				.setMaxVDropPercent(3.00)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(38.71, voltageDropAC.getMaxLengthForVD(),0.01);

		voltageDropAC.increaseNumberOfSets();
		assertEquals(2, voltageDropAC.getNumberOfSets());
		assertEquals(77.4, voltageDropAC.getMaxLengthForVD(),0.01);
		voltageDropAC.increaseNumberOfSets();
		assertEquals(3, voltageDropAC.getNumberOfSets());
		assertEquals(116.1, voltageDropAC.getMaxLengthForVD(),0.01);
		voltageDropAC.decreaseNumberOfSets();
		voltageDropAC.decreaseNumberOfSets();
		assertEquals(38.71, voltageDropAC.getMaxLengthForVD(),0.01);

		voltageDropAC
				.setVoltageAC(VoltageAC.v480_3ph_3w)
				.setLoadCurrent(335.0)
				.setPowerFactor(0.8)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setConductorSize(Size.KCMIL_400)
				.setMaxVDropPercent(3.0)
				.setNumberOfSets(1)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(432.0, voltageDropAC.getMaxLengthForVD(), 0.001);

		voltageDropAC
				.setVoltageAC(VoltageAC.v480_3ph_3w)
				.setLoadCurrent(490.0)
				.setPowerFactor(0.8)
				.setPowerFactorType(PowerFactorType.LAGGING)
				.setConductorSize(Size.KCMIL_800)
				.setMaxVDropPercent(3.0)
				.setNumberOfSets(1)
				.setConductorMetal(ConductiveMetal.COPPER)
				.setConduitMaterial(OuterMaterial.STEEL);
		assertEquals(377.0, voltageDropAC.getMaxLengthForVD(), 0.001);
	}

	@Test
	void test_AC_getMaxLengthForVD() {
		assertEquals( 38.71, getMaxLengthForVD(120.0, 1, 15.0, 1.0, true, Size.AWG_14,3.00, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(45.0 , getMaxLengthForVD(120.0, 1, 20.0, 1.0, true, Size.AWG_12, 3.0,     1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(50.0 , getMaxLengthForVD(120.0, 1, 30.0, 1.0, true, Size.AWG_10, 3.0,     1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(57.7 , getMaxLengthForVD(120.0, 1, 40.0, 1.0, true, Size.AWG_8, 3.0,  1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(115.8 , getMaxLengthForVD(208.0, 1, 55.0, 1.0, true, Size.AWG_6, 3.0,  1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(143.7 , getMaxLengthForVD(208.0, 1, 70.0, 1.0, true, Size.AWG_4, 3.0,  1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(146.8 , getMaxLengthForVD(208.0, 1, 85.0, 1.0, true, Size.AWG_3, 3.0,  1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(164.1 , getMaxLengthForVD(208.0, 1, 95.0, 1.0, true, Size.AWG_2, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(147.9 , getMaxLengthForVD(208.0, 1, 130.0, 0.8, true, Size.AWG_1, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(161.2 , getMaxLengthForVD(208.0, 1, 150.0, 0.8, true, Size.AWG_1$0, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(158.6 , getMaxLengthForVD(208.0, 1, 175.0, 0.8, true, Size.AWG_2$0, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(165.2 , getMaxLengthForVD(208.0, 1, 200.0, 0.8, true, Size.AWG_3$0, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(167.5 , getMaxLengthForVD(208.0, 1, 230.0, 0.8, true, Size.AWG_4$0, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(438.2 , getMaxLengthForVD(480.0, 3, 255.0, 0.8, true, Size.KCMIL_250, 3.0, 1,
				ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(437.8 , getMaxLengthForVD(480.0, 3, 285.0, 0.8, true, Size.KCMIL_300, 3.0, 1,
				ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(437.9 , getMaxLengthForVD(480.0, 3, 310.0, 0.8, true, Size.KCMIL_350, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(432.0 , getMaxLengthForVD(480.0, 3, 335.0, 0.8, true, Size.KCMIL_400, 3.0, 1,
				ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(420.1 , getMaxLengthForVD(480.0, 3, 380.0, 0.8, true, Size.KCMIL_500, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(404.8 , getMaxLengthForVD(480.0, 3, 420.0, 0.8, true, Size.KCMIL_600, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(389.19 , getMaxLengthForVD(480.0, 3, 460.0, 0.8, true, Size.KCMIL_700, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(382.8 , getMaxLengthForVD(480.0, 3, 475.0, 0.8, true, Size.KCMIL_750, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(377.0 , getMaxLengthForVD(480.0, 3, 490.0, 0.8, true, Size.KCMIL_800, 3.0, 1,
				ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(366.9 , getMaxLengthForVD(480.0, 3, 520.0, 0.8, true, Size.KCMIL_900, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(362.01 , getMaxLengthForVD(480.0, 3, 545.0, 0.8, true, Size.KCMIL_1000, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(357.7 , getMaxLengthForVD(480.0, 3, 590.0, 0.8, true, Size.KCMIL_1250, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(352.99 , getMaxLengthForVD(480.0, 3, 625.0, 0.8, true, Size.KCMIL_1500, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(345.6 , getMaxLengthForVD(480.0, 3, 650.0, 0.8, true, Size.KCMIL_1750, 3.0, 1,
				ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
		assertEquals(347.7 , getMaxLengthForVD(480.0, 3, 665.0, 0.8, true, Size.KCMIL_2000, 3.0, 1, ConductiveMetal.COPPER,
				OuterMaterial.STEEL), 0.01);
	}


}