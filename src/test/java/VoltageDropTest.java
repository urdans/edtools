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
	void getMaxLengthForMaxVD_AC(){
		conductor.setLength(250).setSize(Size.AWG_1);
		voltDrop.setLoadCurrent(95).setMaxVoltageDropPercent(10);
		assertEquals(423.276, voltDrop.getMaxLengthForMaxVD(), 0.001);

		voltDrop.setLoadCurrent(110);
		conductor.setInsulation(Insul.TW);
		assertEquals(365.557, voltDrop.getMaxLengthForMaxVD(), 0.001);

		voltDrop.setLoadCurrent(145);
		conductor.setInsulation(Insul.XHHW2);
		assertEquals(277.319, voltDrop.getMaxLengthForMaxVD(), 0.001);
	}

	@Test
	void getMaxLengthForMaxVD_DC(){
		conductor.setLength(225).setSize(Size.AWG_1$0);
		voltDropDC.setLoadCurrent(95).setMaxVoltageDropPercent(15);
		assertEquals(776.532, voltDropDC.getMaxLengthForMaxVD(), 0.001);

		voltDropDC.setLoadCurrent(110);
		conductor.setInsulation(Insul.TW);
		assertEquals(670.64, voltDropDC.getMaxLengthForMaxVD(), 0.001);

		voltDropDC.setLoadCurrent(145);
		conductor.setInsulation(Insul.XHHW2);
		assertEquals(508.762, voltDropDC.getMaxLengthForMaxVD(), 0.001);
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
	void getMinSizeForMaxVD01() {
		assertEquals(Size.AWG_10, voltDropDC.getMinSizeForMaxVD());
	}

	@Test
	void getMinSizeForMaxVD02() {
		change1();
		assertEquals(Size.AWG_1$0, voltDropDC.getMinSizeForMaxVD());
	}

	@Test
	void getMinSizeForMaxVD03() {
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
		//the rule for maxVoltageDropPercent applies for getMaxLengthForMaxVD
		assertEquals(0, voltDrop2.getMaxLengthForMaxVD());
		assertTrue(voltDrop2.getResultMessages().containsMessage(-8));
		//however, the rule for maxVoltageDropPercent does not apply for
		//getVoltageDropVolts
		assertEquals(7.24606, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-8));
	}

	@Test
	void getErrorMessages18() {
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop2 = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setPowerFactor(0.9)
				.setMaxVoltageDropPercent(26);
		//the rule for maxVoltageDropPercent applies for getMaxLengthForMaxVD
		assertEquals(0, voltDrop2.getMaxLengthForMaxVD());
		assertTrue(voltDrop2.getResultMessages().containsMessage(-8));
		//however, the rule for maxVoltageDropPercent does not apply for
		//getVoltageDropVolts
		assertEquals(7.24606, voltDrop2.getVoltageDropVolts(), 0.0001);
		assertFalse(voltDrop2.getResultMessages().containsMessage(-8));

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
		//resultMessages should contain messages that correspond with the
		//current state of the class. The following assertion should be true.
		//The ResultMessages object must contain the messages -2 indicating the
		//the conduit material cannot be null.
		assertFalse(voltDrop2.getResultMessages().containsMessage(-20));
		assertTrue(voltDrop2.getResultMessages().containsMessage(-2));
		//this also should be true.
		assertTrue(voltDrop2.getResultMessages().hasErrors());
		//This JSON shows the message is present in the ResultMessage object
		//at the class level and at the methods level where the message
		//applies, that is, where the conduit material is required to be non
		//null.
		//System.out.println(voltDrop2.toJSON());
		//this removes the error -2 from the class level
		voltDrop2.setConduitMaterial(Material.PVC);
		//which is shown here. But the ampacity exceeded error persists for
		//the methods that require it to be good.
		//System.out.println(voltDrop2.toJSON());
		/*
		OLD: This assertion fails because getMinSizeForMaxVD checks if there are
		errors for the wrong context (the class level, the default context)
		but it could determine the result with the current state of
		the voltDrop2 object.
		So, calling a calculator method should not clear the messages of the
		class level (the default context), it should only clear the
		messages of its own context. The setter of the class should update
		the ResultMessage object after setting the value.
		The calculator method should only check for errors related to its
		context.*/
		/*
		UPDATE: This works now. No need for a context. Implementing a context
		is too complicated. To overcome the context requirement, we just need
		to produce a JSON object manually which contains all the
		resultMessages and the results for each calculation method.
		 */
		assertEquals(Size.AWG_3$0, voltDrop2.getMinSizeForMaxVD());
		//System.out.println(voltDrop2.toJSON());
		//the error should still be there but it's not if I do not call toJSON()

/*Done:
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
 1. THIS DOESN'T WORK, TOO COMPLICATED: ResultMessage must implement a sort of
 context indicator. So, ResultMessage will contain the messages at the class
 level into the "Default" context. Every calculation method that can produce
 a result message must add a context which could be the name of the method. The
 ResultMessage object returned by the calculator class will contain messages
 for both levels, the class and the method levels.
 2. The JSON representation of the class needs to be built by
 hand, calling the methods in a certain order. This JSON is not a
 representation of the class, but a container representing the class state
 plus all the results of the calculations performed by the class along with
 their respective result messages.
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
		//no error at the class level
		assertFalse(voltDrop.getResultMessages().hasErrors());
		assertEquals(0, voltDrop.getResultMessages().errorCount());

		//but, when calling getVoltageDropVolts, the "ampacity exceeded"
		//error appears.
		assertEquals(0, voltDrop.getVoltageDropVolts());
		assertTrue(voltDrop.getResultMessages().hasErrors());
		assertEquals(1, voltDrop.getResultMessages().errorCount());
		assertTrue(voltDrop.getResultMessages().containsMessage(-20));

		/*System.out.println(voltDrop);
		System.out.println(voltDrop.toJSON());

		change2();
		System.out.println(voltDropDC);
		System.out.println(voltDropDC.toJSON());*/
	}

	@Test
	void toJSONCustomAC(){
		Conductor conductor2 = new Conductor();
		VoltageDropAC voltDrop = new VoltageDropAC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(200)
				.setPowerFactor(1.0)
				.setConduitMaterial(Material.STEEL)
				.setMaxVoltageDropPercent(0.4);
		System.out.println(voltDrop.toJSON());
	}

	@Test
	void toJSONCustomDC(){
		Conductor conductor2 = new Conductor();
		VoltageDropDC voltDrop = new VoltageDropDC(conductor2)
				.setNumberOfSets(1)
				.setLoadCurrent(20)
				.setMaxVoltageDropPercent(0.5);
		System.out.println(voltDrop.toJSON());
	}

}