package test.java;

import eecalcs.voltagedrop.VoltageDropAC;
import eecalcs.conductors.*;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import eecalcs.voltagedrop.VoltageDropDC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VoltageDropTest {
	Conductor conductor = new Conductor();
	VoltageDropAC voltDrop = new VoltageDropAC(conductor);
	VoltageDropDC voltDropDC = new VoltageDropDC(conductor);

	void change1(){
		conductor.setCopperCoating(Coating.COATED)
				.setSize(Size.AWG_1$0)
				.setLength(350);

		voltDrop.setSourceVoltageSystem(VoltageSystemAC.v208_3ph_3w)
				.setConduitMaterial(Material.ALUMINUM)
				.setNumberOfSets(2)
				.setLoadCurrent(130)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(3.0);

		voltDropDC.setDcVoltage(208)
				.setNumberOfSets(2)
				.setLoadCurrent(130)
				.setMaxVoltageDropPercent(3.0);
	}

	void change2(){
		conductor.setCopperCoating(Coating.COATED)
				.setSize(Size.AWG_4$0)
				.setLength(250);

		voltDrop.setSourceVoltageSystem(VoltageSystemAC.v480_3ph_4w)
				.setConduitMaterial(Material.ALUMINUM)
				.setNumberOfSets(2)
				.setLoadCurrent(460)
				.setPowerFactor(0.85)
				.setMaxVoltageDropPercent(2.0);

		voltDropDC.setDcVoltage(480)
				.setNumberOfSets(2)
				.setLoadCurrent(460)
				.setMaxVoltageDropPercent(2.0);
	}

	@Test
	void getACVoltageDrop01() {
		assertEquals(3.9999, voltDrop.getVoltageDropVolts(), 0.0001);
	}

	@Test
	void getACVoltageDrop02() {
		change1();
		assertEquals(5.3649, voltDrop.getVoltageDropVolts(), 0.0001);
	}

	@Test
	void getACVoltageDrop03() {
		change2();
		assertEquals(7.8228, voltDrop.getVoltageDropVolts(), 0.0001);
		assertEquals(480-7.8228, voltDrop.getVoltageAtLoad(), 0.0001);
		assertEquals(100*7.8228/480, voltDrop.getVoltageDropPercentage(), 0.0001);
	}

	@Test
	void getDCVoltageDrop01() {
		assertEquals(3.8600, voltDropDC.getVoltageDropVolts(), 0.0001);
	}

	@Test
	void getDCVoltageDrop02() {
		change1();
		assertEquals(5.7785, voltDropDC.getVoltageDropVolts(), 0.0001);
	}

	@Test
	void getDCVoltageDrop03() {
		change2();
		assertEquals(7.1990, voltDropDC.getVoltageDropVolts(), 0.0001);
		assertEquals(480-7.1990, voltDropDC.getVoltageAtLoad(), 0.0001);
		assertEquals(100*7.1990/480, voltDropDC.getVoltageDropPercentage(),0.0001);
	}

	@Test
	void getCalculatedSizeAC01() {
		assertEquals(Size.AWG_10, voltDrop.getMinSizeForMaxVD());
	}

	@Test
	void getCalculatedSizeAC02() {
		change1();
		assertEquals(Size.AWG_1$0, voltDrop.getMinSizeForMaxVD());
	}

	@Test
	void getCalculatedSizeAC03() {
		change2();
		assertEquals(Size.AWG_4$0, voltDrop.getMinSizeForMaxVD());
	}

	@Test
	void getCalculatedSizeDC01() {
		assertEquals(Size.AWG_10, voltDropDC.getMinSizeForMaxVD());
	}

	@Test
	void getCalculatedSizeDC02() {
		change1();
		assertEquals(Size.AWG_1$0, voltDropDC.getMinSizeForMaxVD());
	}

	@Test
	void getCalculatedSizeDC03() {
		change2();
		assertEquals(Size.AWG_4$0, voltDropDC.getMinSizeForMaxVD());
	}

	@Test
	void getMaxLengthAC01(){
		assertEquals(150.0046, voltDrop.getMaxLengthForMinSize(), 0.0001);
	}

	@Test
	void getMaxLengthAC02(){
		change1();
		assertEquals(407.1041, voltDrop.getMaxLengthForMinSize(), 0.0001);
	}

	@Test
	void getMaxLengthAC03(){
		change2();
		assertEquals(306.7943, voltDrop.getMaxLengthForMinSize(), 0.0001);
	}

	@Test
	void getMaxLengthDC01() {
		assertEquals(148.7603,	voltDropDC.getMaxLengthForMinSize(),0.0001);
	}

	@Test
	void getMaxLengthDC02() {
		change1();
		assertEquals(377.9528, voltDropDC.getMaxLengthForMinSize(), 0.0001);
	}

	@Test
	void getMaxLengthDC03() {
		change2();
		assertEquals(333.3796, voltDropDC.getMaxLengthForMinSize(), 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageAC01(){
		assertEquals(2.0000, voltDrop.getVdPercentForMinSize(), 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageAC02(){
		change1();
		assertEquals(2.5793, voltDrop.getVdPercentForMinSize(), 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageAC03(){
		change2();
		assertEquals(1.6298, voltDrop.getVdPercentForMinSize(), 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageDC01(){
		assertEquals(2.0167,
				voltDropDC.getVdPercentForMinSize(),0.0001);
	}

	@Test
	void getActualVoltageDropPercentageDC02(){
		change1();
		assertEquals(2.7781, voltDropDC.getVdPercentForMinSize(), 0.0001);
	}

	@Test
	void getActualVoltageDropPercentageDC03(){
		change2();
		assertEquals(1.4997, voltDropDC.getVdPercentForMinSize(), 0.0001);
	}

	@Test
	void getErrorMessages01() {
		VoltageDropAC voltDrop2 =
				new VoltageDropAC(new Conductor());
		assertEquals(3.9999, voltDrop2.getVoltageDropVolts(), 0.0001);
	}

	@Test
	void getErrorMessages02() {
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-9));
	}

	@Test
	void getErrorMessages03() {
		Conductor conductor2 = new Conductor()
				.setSize(null)
				.setMetal(Metal.COPPER)
				.setInsulation(Insul.THW)
				.setLength(0);

		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-3));
		assertTrue(voltDrop2.getResultMessages().containsMessage(-5));
	}

	@Test
	void getErrorMessages04() {
		Conductor conductor2 = new Conductor()
				.setSize(Size.AWG_3)
				.setMetal(Metal.COPPER)
				.setInsulation(Insul.THW)
				.setLength(80);

		VoltageDropAC voltDrop2 =
				new VoltageDropAC(conductor2);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-3));
		assertFalse(voltDrop2.getResultMessages().containsMessage(-5));
	}

	@Test
	void getErrorMessages05() {
		Conductor conductor2 = new Conductor()
				.setSize(Size.AWG_3)
				.setMetal(Metal.COPPER)
				.setInsulation(Insul.THW)
				.setLength(80);
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setSourceVoltageSystem(null);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-10));
	}

	@Test
	void getErrorMessages06() {
		Conductor conductor2 = new Conductor()
				.setSize(Size.AWG_3)
				.setMetal(Metal.COPPER)
				.setInsulation(Insul.THW)
				.setLength(80);
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setSourceVoltageSystem(VoltageSystemAC.v277_1ph_2w);
		assertEquals(0.3999, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-1));
	}

	@Test
	void getErrorMessages07() {
		Conductor conductor2 = new Conductor()
				.setMetal(Metal.COPPER)
				.setInsulation(Insul.THW)
				.setLength(80);
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setSourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.setNumberOfSets(11);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-4));
	}

	@Test
	void getErrorMessages08() {
		Conductor conductor2 = new Conductor()
				.setSize(Size.AWG_3)
				.setMetal(Metal.COPPER)
				.setInsulation(Insul.THW)
				.setLength(80);
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setSourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.setNumberOfSets(8);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-4));
	}

	@Test
	void getErrorMessages09() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(2);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-21));
	}

	@Test
	void getErrorMessages10() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1);
		assertEquals(3.9999, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-21));
	}

	@Test
	void getErrorMessages11() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(0);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-6));
	}

	@Test
	void getErrorMessages12() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(35);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-20));
	}

	@Test
	void getErrorMessages13() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20);
		assertEquals(7.9997, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-20));
	}

	@Test
	void getErrorMessages14() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.69);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-7));
	}

	@Test
	void getErrorMessages15() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(1.1);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-7));
	}

	@Test
	void getErrorMessages16() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9);
		assertEquals(7.2460, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-7));
	}

	@Test
	void getErrorMessages17() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(0.4);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-8));
	}

	@Test
	void getErrorMessages18() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(26);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-8));
	}

	@Test
	void getErrorMessages19() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(6);
		assertEquals(7.2460, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-8));
	}

	@Test
	void getErrorMessages20() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(6)
				.setConduitMaterial(null);
		assertEquals(0, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertTrue(voltDrop2.getResultMessages().containsMessage(-2));
	}

	@Test
	void getErrorMessages21() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(6)
				.setConduitMaterial(Material.STEEL);
		assertEquals(7.2719, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-2));
	}

	@Test
	void getErrorMessages22() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setSourceVoltageSystem(VoltageSystemAC.v277_1ph_2w)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(6)
				.setConduitMaterial(Material.STEEL);
		assertFalse(voltDrop2.getResultMessages().hasErrors());
		assertEquals(7.2991, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertEquals(100*7.2991/277, voltDrop2.getVoltageDropPercentage(),0.0001);
	}

	@Test
	void usingCopper_100ft_120v1ph_200Amps(){
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2);
		voltDrop2.setNumberOfSets(1)
				.setLoadCurrent(200)
				.setPowerFactor(1.0)
				.setConduitMaterial(Material.STEEL)
				.setConduitMaterial(null);
		//result message should contain the current state of the class. So
		//this assertion should be true. The ResultMessages object must
		//contain the messages -20 indicating the size is too small for the
		//current and -2 indicating the conduit material cannot be null.
		assertTrue(voltDrop2.getResultMessages().containsMessage(-20));
		//this also should be true.
		assertTrue(voltDrop2.getResultMessages().hasErrors());
		//This JSON shows the messages are present in the ResultMessage object.
		System.out.println(voltDrop2.toJSON());
		//this removes the error -2 from the class level
		voltDrop2.setConduitMaterial(Material.PVC);
		//which is shown here
		System.out.println(voltDrop2.toJSON());
		//conductor2.setSize(Size.KCMIL_1000);
		//This assertion fails because getMinSizeForMaxVD checks if there are
		//errors for the wrong context (the class level, the default context)
		//but it could determine the result with the current state of
		//the voltDrop2 object.
		//So, calling a calculator method should not clear the messages of the
		//class level (the default context), it should only clear the
		//messages of its own context. The setter of the class should update
		//the ResultMessage object after setting the value.
		//The calculator method should only check for errors related to its
		//context.
		assertEquals(Size.AWG_3$0, voltDrop2.getMinSizeForMaxVD());
		System.out.println(voltDrop2.toJSON());
		//the error should still be there but it's not if I do not call toJSON()
//		assertTrue(voltDrop2.getResultMessages().hasErrors());
/*Todo:
There is an issue with the way the results messages are returned.
A class usually provides several calculation methods. Each calculation method
 can have its own associated result messages. The ResultMessage class is
 designed to provide result messages at the class level, not a the method
 level. At the class level, the ResultMessages class can provide with useful
 information related to the state of the class, specially in relation to the
 the well-conformity of the parameters upon which the class was created or
 the actual set of values. For example, a setter could provide a value that
 put the class "out of the business logic". An example of this is when a
 conductor size 1/0 AWG or less is used in parallel.
 The problem arises when requesting serializing the class into JSON to send
 it back to the client. We want all the calculations to be performed upon the
 current state of the class and be sent to the client, but the current
 implementation of ResultMessages does not handle this well.
 How to solve it?
 1. ResultMessage must implement a sort of context indicator. So,
 ResultMessage will contain the messages at the class level into the
 "Default" context. Every calculation method that can produce a result
 message must add a context which could be the name of the method. The
 ResultMessage object returned by the calculator class will contain messages
 for both levels, the class and the method levels.
 2. The JSON representation of the class (probably) will need to be built by
 hand, calling the methods in a certain order. This has to be tested to see
 if this can be avoided, so leaving all the burden to the JSON helper class.
 */

	}

	@Test
	void toStringTest(){
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(200)
				.setPowerFactor(1.0)
				.setConduitMaterial(Material.STEEL);
		assertTrue(voltDrop.getResultMessages().hasErrors());
		assertEquals(1, voltDrop.getResultMessages().errorCount());
		assertEquals(0, voltDrop.getVoltageDropVolts());
/*
		System.out.println(voltDrop);
		System.out.println(voltDrop.toJSON());

		change2();
		System.out.println(voltDropDC);
		System.out.println(voltDropDC.toJSON());*/
	}


}