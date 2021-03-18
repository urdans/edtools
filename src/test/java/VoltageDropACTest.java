package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import eecalcs.voltagedrop.VoltageDropAC;
import eecalcs.voltagedrop.VoltageDropDC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VoltageDropACTest {
	Conductor conductor = new Conductor();
	VoltageDropAC voltDrop;
	VoltageDropDC voltDropDC;

	void change1(){
		voltDrop = new VoltageDropAC.Builder().conductor(conductor).build();
		voltDropDC = new VoltageDropDC.Builder().conductor(conductor).build();
	}

	void change2(){
		conductor.setCopperCoated(Coating.COATED)
				.setSize(Size.AWG_1$0)
				.setLength(350);
		voltDrop = new VoltageDropAC.Builder()
				.sourceVoltageSystem(VoltageSystemAC.v208_3ph_3w)
				.conduitMaterial(Material.ALUMINUM)
				.numberOfSets(2)
				.loadCurrent(130)
				.powerFactor(0.9)
				.maxVoltageDropPercent(3.0)
				.conductor(conductor)
				.build();
		voltDropDC = new VoltageDropDC.Builder()
				.dcVoltage(208)
				.numberOfSets(2)
				.loadCurrent(130)
				.maxVoltageDropPercent(3.0)
				.conductor(conductor)
				.build();
	}

	void change3(){
		conductor.setCopperCoated(Coating.COATED)
				.setSize(Size.AWG_4$0)
				.setLength(250);
		voltDrop = new VoltageDropAC.Builder()
				.sourceVoltageSystem(VoltageSystemAC.v480_3ph_4w)
				.conduitMaterial(Material.ALUMINUM)
				.numberOfSets(2)
				.loadCurrent(460)
				.powerFactor(0.85)
				.maxVoltageDropPercent(2.0)
				.conductor(conductor)
				.build();
		voltDropDC = new VoltageDropDC.Builder()
				.dcVoltage(480)
				.numberOfSets(2)
				.loadCurrent(460)
				.maxVoltageDropPercent(2.0)
				.conductor(conductor)
				.build();
	}

	@Test
	void getACVoltageDrop01() {
		change1();
		assertEquals(3.9999, voltDrop.resultsVD.voltageDropVolts, 0.0001);
	}

	@Test
	void getACVoltageDrop02() {
		change2();
		assertEquals(5.3649, voltDrop.resultsVD.voltageDropVolts, 0.0001);
	}

	@Test
	void getACVoltageDrop03() {
		change3();
		assertEquals(7.8228, voltDrop.resultsVD.voltageDropVolts, 0.0001);
		assertEquals(480-7.8228, voltDrop.resultsVD.voltageAtLoad, 0.0001);
		assertEquals(100*7.8228/480, voltDrop.resultsVD.voltageDropPercentage, 0.0001);
	}

	@Test
	void getDCVoltageDrop01() {
		change1();
		assertEquals(3.8600, voltDropDC.results.voltageDropVolts, 0.0001);
	}

	@Test
	void getDCVoltageDrop02() {
		change2();
		assertEquals(5.7785, voltDropDC.results.voltageDropVolts, 0.0001);
	}

	@Test
	void getDCVoltageDrop03() {
		change3();
		assertEquals(7.1990, voltDropDC.results.voltageDropVolts, 0.0001);
		assertEquals(480-7.1990, voltDropDC.results.voltageAtLoad,0.0001);
		assertEquals(100*7.1990/480, voltDropDC.results.voltageDropPercentage,0.0001);
	}

	@Test
	void getCalculatedSizeAC01() {
		change1();
		assertEquals(Size.AWG_10, voltDrop.resultsSize.minSizeForMaxVD);
	}

	@Test
	void getCalculatedSizeAC02() {
		change2();
		assertEquals(Size.AWG_1$0, voltDrop.resultsSize.minSizeForMaxVD);
	}

	@Test
	void getCalculatedSizeAC03() {
		change3();
		assertEquals(Size.AWG_4$0, voltDrop.resultsSize.minSizeForMaxVD);
	}

	@Test
	void getCalculatedSizeDC01() {
		change1();
		assertEquals(Size.AWG_10, voltDropDC.results.minSizeForMaxVD);
	}

	@Test
	void getCalculatedSizeDC02() {
		change2();
		assertEquals(Size.AWG_1$0, voltDropDC.results.minSizeForMaxVD);
	}

	@Test
	void getCalculatedSizeDC03() {
		change3();
		assertEquals(Size.AWG_3$0, voltDropDC.results.minSizeForMaxVD);
	}

	@Test
	void getMaxLengthAC01(){
		change1();
		assertEquals(150.0046, voltDrop.resultsSize.maxLengthForMinSize, 0.0001);
	}

	@Test
	void getMaxLengthAC02(){
		change2();
		assertEquals(407.1041, voltDrop.resultsSize.maxLengthForMinSize, 0.0001);
	}

	@Test
	void getMaxLengthAC03(){
		change3();
		assertEquals(306.7943, voltDrop.resultsSize.maxLengthForMinSize, 0.0001);
	}

	@Test
	void getMaxLengthDC01() {
		change1();
		assertEquals(148.7603, voltDropDC.results.maxLengthForMinSize,0.0001);
	}

	@Test
	void getMaxLengthDC02() {
		change2();
		assertEquals(377.9528, voltDropDC.results.maxLengthForMinSize, 0.0001);
	}

	@Test
	void getMaxLengthDC03() {
		change3();
		assertEquals(261.8515, voltDropDC.results.maxLengthForMinSize, 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageAC01(){
		change1();
		assertEquals(2.0000, voltDrop.resultsSize.vdPercentForMinSize, 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageAC02(){
		change2();
		assertEquals(2.5793, voltDrop.resultsSize.vdPercentForMinSize, 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageAC03(){
		change3();
		assertEquals(1.6298, voltDrop.resultsSize.vdPercentForMinSize, 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageDC01(){
		change1();
		assertEquals(2.0167,voltDropDC.results.vdPercentForMinSize,0.0001);
	}

	@Test
	void getActualVoltageDropPercentageDC02(){
		change2();
		assertEquals(2.7781, voltDropDC.results.vdPercentForMinSize, 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageDC03(){
		change3();
		assertEquals(1.9095, voltDropDC.results.vdPercentForMinSize, 0.0001);
	}

	@Test
	void getErrorMessages01() {
		VoltageDropAC voltDrop2 =
				new VoltageDropAC.Builder().conductor(null).build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-9));
	}

	@Test
	void getErrorMessages02() {
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder().build();
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-9));
	}

	@Test
	void getErrorMessages03() {
		Conductor conductor2 = new Conductor(null, Metal.COPPER, Insul.THW, 0);
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder().conductor(conductor2).build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-3));
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-5));
	}

	@Test
	void getErrorMessages04() {
		Conductor conductor2 = new Conductor(Size.AWG_3, Metal.COPPER,
				Insul.THW, 80);
		VoltageDropAC voltDrop2 =
				new VoltageDropAC.Builder().conductor(conductor2).build();
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-3));
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-5));
	}

	@Test
	void getErrorMessages05() {
		Conductor conductor2 = new Conductor(Size.AWG_3, Metal.COPPER,
				Insul.THW, 80);
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.sourceVoltageSystem(null)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-1));
	}

	@Test
	void getErrorMessages06() {
		Conductor conductor2 = new Conductor(Size.AWG_3, Metal.COPPER,
				Insul.THW, 80);
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.sourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.build();
		assertEquals(0.3999, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-1));
	}

	@Test
	void getErrorMessages07() {
		Conductor conductor2 = new Conductor(Size.AWG_3, Metal.COPPER,
				Insul.THW, 80);
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.sourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.numberOfSets(11)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-4));
	}

	@Test
	void getErrorMessages08() {
		Conductor conductor2 = new Conductor(Size.AWG_3, Metal.COPPER,
				Insul.THW, 80);
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.sourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.numberOfSets(8)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-4));
	}

	@Test
	void getErrorMessages09() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(2)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-21));
	}

	@Test
	void getErrorMessages10() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.build();
		assertEquals(3.9999, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-21));
	}

	@Test
	void getErrorMessages11() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(0)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-6));
	}

	@Test
	void getErrorMessages12() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(35)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-20));
	}

	@Test
	void getErrorMessages13() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.build();
		assertEquals(7.9997, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-20));
	}

	@Test
	void getErrorMessages14() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.69)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-7));
	}

	@Test
	void getErrorMessages15() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(1.1)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-7));
	}

	@Test
	void getErrorMessages16() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.build();
		assertEquals(7.2460, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-7));
	}

	@Test
	void getErrorMessages17() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.maxVoltageDropPercent(0.4)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-8));
	}

	@Test
	void getErrorMessages18() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.maxVoltageDropPercent(26)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-8));
	}

	@Test
	void getErrorMessages19() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.maxVoltageDropPercent(6)
				.build();
		assertEquals(7.2460, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-8));
	}

	@Test
	void getErrorMessages20() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.maxVoltageDropPercent(6)
				.conduitMaterial(null)
				.build();
		assertEquals(0, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertTrue(voltDrop2.resultsVD.resultMessages.containsMessage(-2));
	}

	@Test
	void getErrorMessages21() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.maxVoltageDropPercent(6)
				.conduitMaterial(Material.STEEL)
				.build();
		assertEquals(7.2719, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertFalse(voltDrop2.resultsVD.resultMessages.containsMessage(-2));
	}

	@Test
	void getErrorMessages22() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.sourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.numberOfSets(1)
				.loadCurrent(20)
				.powerFactor(0.9)
				.maxVoltageDropPercent(6)
				.conduitMaterial(Material.STEEL)
				.build();
		assertFalse(voltDrop2.resultsVD.resultMessages.hasErrors());
		assertEquals(7.2991, voltDrop2.resultsVD.voltageDropVolts, 0.0001);
		assertEquals(100*7.2991/277, voltDrop2.resultsVD.voltageDropPercentage,0.0001);
	}

	@Test
	void usingCopper_100ft_120v1ph_200Amps(){
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC.Builder()
				.conductor(conductor2)
				.numberOfSets(1)
				.loadCurrent(200)
				.powerFactor(1.0)
				.conduitMaterial(Material.STEEL)
				.build();

		assertFalse(voltDrop2.resultsSize.resultMessages.hasErrors());
		assertEquals(Size.AWG_3$0, voltDrop2.resultsSize.minSizeForMaxVD);
	}
}