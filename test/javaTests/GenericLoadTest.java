package javaTests;

import eecalcs.circuits.CircuitType;
import eecalcs.loads.GenericLoad;
import eecalcs.loads.Load;
import eecalcs.loads.LoadType;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericLoadTest {

	@Test
	void fromNominalCurrent() {
		//1φ 2W Neutral
		Load aLoad1 = GenericLoad.fromNominalCurrent(VoltageAC.v120_1ph_2w, 0.9, 10);
		assertEquals(aLoad1.getNominalCurrent(), 10);
		assertEquals(aLoad1.getApparentPower(), 120*10);
		assertEquals(aLoad1.getRealPower(), 120*10*0.9);
		assertEquals(aLoad1.getNeutralCurrent(), 10);
		//Extrinsic parameters
		assertEquals(aLoad1.getMaxOCPDRating(), 0);
		assertEquals(aLoad1.getMaxOLPDRating(), 0);
		assertEquals(aLoad1.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad1.getMCA(), 10);
		assertEquals(aLoad1.getMinDSRating(), 0);
		assertFalse(aLoad1.isNonLinear());
		assertTrue(aLoad1.isNeutralCurrentCarrying());
		assertTrue(aLoad1.NHSRRuleApplies());
		assertEquals(aLoad1.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//1φ 2W no neutral
		Load aLoad2 = GenericLoad.fromNominalCurrent(VoltageAC.v208_1ph_2w, 0.9, 10);
		assertEquals(aLoad2.getNominalCurrent(), 10);
		assertEquals(aLoad2.getApparentPower(), 208*10);
		assertEquals(aLoad2.getRealPower(), 208*10*0.9);
		assertEquals(aLoad2.getNeutralCurrent(), 0);
		//Extrinsic parameters
		assertEquals(aLoad2.getMaxOCPDRating(), 0);
		assertEquals(aLoad2.getMaxOLPDRating(), 0);
		assertEquals(aLoad2.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad2.getMCA(), 10);
		assertEquals(aLoad2.getMinDSRating(), 0);
		assertFalse(aLoad2.isNonLinear());
		assertFalse(aLoad2.isNeutralCurrentCarrying());
		assertTrue(aLoad2.NHSRRuleApplies());
		assertEquals(aLoad2.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//3φ 4W neutral
		Load aLoad3 = GenericLoad.fromNominalCurrent(VoltageAC.v480_3ph_4w, 0.8, 20);
		assertEquals(aLoad3.getNominalCurrent(), 20);
		assertEquals(aLoad3.getApparentPower(), 480*20*Math.sqrt(3));
		assertEquals(aLoad3.getRealPower(), 480*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad3.getNeutralCurrent(), 20);
		//Extrinsic parameters
		assertEquals(aLoad3.getMaxOCPDRating(), 0);
		assertEquals(aLoad3.getMaxOLPDRating(), 0);
		assertEquals(aLoad3.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad3.getMCA(), 20);
		assertEquals(aLoad3.getMinDSRating(), 0);
		assertFalse(aLoad3.isNonLinear());
		assertFalse(aLoad3.isNeutralCurrentCarrying());
		assertTrue(aLoad3.NHSRRuleApplies());
		assertEquals(aLoad3.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//3φ 3W no neutral
		Load aLoad4 = GenericLoad.fromNominalCurrent(VoltageAC.v240_3ph_3w, 0.8, 20);
		assertEquals(aLoad4.getNominalCurrent(), 20);
		assertEquals(aLoad4.getApparentPower(), 240*20*Math.sqrt(3));
		assertEquals(aLoad4.getRealPower(), 240*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad4.getNeutralCurrent(), 0);
		//Extrinsic parameters
		assertEquals(aLoad4.getMaxOCPDRating(), 0);
		assertEquals(aLoad4.getMaxOLPDRating(), 0);
		assertEquals(aLoad4.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad4.getMCA(), 20);
		assertEquals(aLoad4.getMinDSRating(), 0);
		assertFalse(aLoad4.isNonLinear());
		assertFalse(aLoad4.isNeutralCurrentCarrying());
		assertTrue(aLoad4.NHSRRuleApplies());
		assertEquals(aLoad4.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
	}

	@Test
	void fromApparentPower() {
		//1φ 2W Neutral
		Load aLoad1 = GenericLoad.fromApparentPower(VoltageAC.v120_1ph_2w, 0.9, 1200);
		assertEquals(aLoad1.getNominalCurrent(), 1200.0/120);
		assertEquals(aLoad1.getApparentPower(), 120*10);
		assertEquals(aLoad1.getRealPower(), 120*10*0.9);
		assertEquals(aLoad1.getNeutralCurrent(), 10);
		//Extrinsic parameters
		assertEquals(aLoad1.getMaxOCPDRating(), 0);
		assertEquals(aLoad1.getMaxOLPDRating(), 0);
		assertEquals(aLoad1.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad1.getMCA(), 10);
		assertEquals(aLoad1.getMinDSRating(), 0);
		assertFalse(aLoad1.isNonLinear());
		assertTrue(aLoad1.isNeutralCurrentCarrying());
		assertTrue(aLoad1.NHSRRuleApplies());
		assertEquals(aLoad1.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//1φ 2W no neutral
		Load aLoad2 = GenericLoad.fromApparentPower(VoltageAC.v208_1ph_2w, 0.9, 2080);
		assertEquals(aLoad2.getNominalCurrent(), 2080.0/208);
		assertEquals(aLoad2.getApparentPower(), 208*10);
		assertEquals(aLoad2.getRealPower(), 208*10*0.9);
		assertEquals(aLoad2.getNeutralCurrent(), 0);
		//Extrinsic parameters
		assertEquals(aLoad2.getMaxOCPDRating(), 0);
		assertEquals(aLoad2.getMaxOLPDRating(), 0);
		assertEquals(aLoad2.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad2.getMCA(), 10);
		assertEquals(aLoad2.getMinDSRating(), 0);
		assertFalse(aLoad2.isNonLinear());
		assertFalse(aLoad2.isNeutralCurrentCarrying());
		assertTrue(aLoad2.NHSRRuleApplies());
		assertEquals(aLoad2.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//3φ 4W neutral
		Load aLoad3 = GenericLoad.fromApparentPower(VoltageAC.v480_3ph_4w, 0.8, 480*20*Math.sqrt(3));
		assertEquals(aLoad3.getNominalCurrent(), 20, 0.00001);
		assertEquals(aLoad3.getApparentPower(), 480*20*Math.sqrt(3));
		assertEquals(aLoad3.getRealPower(), 480*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad3.getNeutralCurrent(), 20, 0.00001);
		//Extrinsic parameters
		assertEquals(aLoad3.getMaxOCPDRating(), 0);
		assertEquals(aLoad3.getMaxOLPDRating(), 0);
		assertEquals(aLoad3.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad3.getMCA(), 20, 0.00001);
		assertEquals(aLoad3.getMinDSRating(), 0);
		assertFalse(aLoad3.isNonLinear());
		assertFalse(aLoad3.isNeutralCurrentCarrying());
		assertTrue(aLoad3.NHSRRuleApplies());
		assertEquals(aLoad3.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//3φ 3W no neutral
		Load aLoad4 = GenericLoad.fromApparentPower(VoltageAC.v240_3ph_3w, 0.8, 240*20*Math.sqrt(3));
		assertEquals(aLoad3.getNominalCurrent(), 20, 0.00001);
		assertEquals(aLoad4.getApparentPower(), 240*20*Math.sqrt(3));
		assertEquals(aLoad4.getRealPower(), 240*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad4.getNeutralCurrent(), 0);
		//Extrinsic parameters
		assertEquals(aLoad4.getMaxOCPDRating(), 0);
		assertEquals(aLoad4.getMaxOLPDRating(), 0);
		assertEquals(aLoad4.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad4.getMCA(), 20, 0.00001);
		assertEquals(aLoad4.getMinDSRating(), 0);
		assertFalse(aLoad4.isNonLinear());
		assertFalse(aLoad4.isNeutralCurrentCarrying());
		assertTrue(aLoad4.NHSRRuleApplies());
		assertEquals(aLoad4.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
	}

	@Test
	void fromRealPower() {
		//1φ 2W Neutral
		Load aLoad1 = GenericLoad.fromRealPower(VoltageAC.v120_1ph_2w, 0.9, 1200*0.9);
		assertEquals(aLoad1.getNominalCurrent(), 1200.0/120);
		assertEquals(aLoad1.getApparentPower(), 120*10);
		assertEquals(aLoad1.getRealPower(), 120*10*0.9);
		assertEquals(aLoad1.getNeutralCurrent(), 10);
		//Extrinsic parameters
		assertEquals(aLoad1.getMaxOCPDRating(), 0);
		assertEquals(aLoad1.getMaxOLPDRating(), 0);
		assertEquals(aLoad1.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad1.getMCA(), 10);
		assertEquals(aLoad1.getMinDSRating(), 0);
		assertFalse(aLoad1.isNonLinear());
		assertTrue(aLoad1.isNeutralCurrentCarrying());
		assertTrue(aLoad1.NHSRRuleApplies());
		assertEquals(aLoad1.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//1φ 2W no neutral
		Load aLoad2 = GenericLoad.fromRealPower(VoltageAC.v208_1ph_2w, 0.9, 2080*0.9);
		assertEquals(aLoad2.getNominalCurrent(), 2080.0/208, 0.00001);
		assertEquals(aLoad2.getApparentPower(), 208*10);
		assertEquals(aLoad2.getRealPower(), 208*10*0.9);
		assertEquals(aLoad2.getNeutralCurrent(), 0);
		//Extrinsic parameters
		assertEquals(aLoad2.getMaxOCPDRating(), 0);
		assertEquals(aLoad2.getMaxOLPDRating(), 0);
		assertEquals(aLoad2.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad2.getMCA(), 10, 0.00001);
		assertEquals(aLoad2.getMinDSRating(), 0);
		assertFalse(aLoad2.isNonLinear());
		assertFalse(aLoad2.isNeutralCurrentCarrying());
		assertTrue(aLoad2.NHSRRuleApplies());
		assertEquals(aLoad2.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//3φ 4W neutral
		Load aLoad3 = GenericLoad.fromRealPower(VoltageAC.v480_3ph_4w, 0.8, 480*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad3.getNominalCurrent(), 20, 0.00001);
		assertEquals(aLoad3.getApparentPower(), 480*20*Math.sqrt(3));
		assertEquals(aLoad3.getRealPower(), 480*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad3.getNeutralCurrent(), 20, 0.00001);
		//Extrinsic parameters
		assertEquals(aLoad3.getMaxOCPDRating(), 0);
		assertEquals(aLoad3.getMaxOLPDRating(), 0);
		assertEquals(aLoad3.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad3.getMCA(), 20, 0.00001);
		assertEquals(aLoad3.getMinDSRating(), 0);
		assertFalse(aLoad3.isNonLinear());
		assertFalse(aLoad3.isNeutralCurrentCarrying());
		assertTrue(aLoad3.NHSRRuleApplies());
		assertEquals(aLoad3.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
		//3φ 3W no neutral
		Load aLoad4 = GenericLoad.fromRealPower(VoltageAC.v240_3ph_3w, 0.8, 240*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad3.getNominalCurrent(), 20, 0.00001);
		assertEquals(aLoad4.getApparentPower(), 240*20*Math.sqrt(3));
		assertEquals(aLoad4.getRealPower(), 240*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad4.getNeutralCurrent(), 0);
		//Extrinsic parameters
		assertEquals(aLoad4.getMaxOCPDRating(), 0);
		assertEquals(aLoad4.getMaxOLPDRating(), 0);
		assertEquals(aLoad4.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad4.getMCA(), 20, 0.00001);
		assertEquals(aLoad4.getMinDSRating(), 0);
		assertFalse(aLoad4.isNonLinear());
		assertFalse(aLoad4.isNeutralCurrentCarrying());
		assertTrue(aLoad4.NHSRRuleApplies());
		assertEquals(aLoad4.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
	}

	@Test
	void getACopy() {
		Load aLoad = GenericLoad.fromRealPower(VoltageAC.v480_3ph_4w, 0.8, 480*20*Math.sqrt(3)*0.8);
		Load aLoad3 = aLoad.getACopy();
		assertEquals(aLoad3.getNominalCurrent(), 20, 0.00001);
		assertEquals(aLoad3.getApparentPower(), 480*20*Math.sqrt(3));
		assertEquals(aLoad3.getRealPower(), 480*20*Math.sqrt(3)*0.8);
		assertEquals(aLoad3.getNeutralCurrent(), 20, 0.00001);
		//Extrinsic parameters
		assertEquals(aLoad3.getMaxOCPDRating(), 0);
		assertEquals(aLoad3.getMaxOLPDRating(), 0);
		assertEquals(aLoad3.getLoadType(), LoadType.NONCONTINUOUS);
		assertEquals(aLoad3.getMCA(), 20, 0.00001);
		assertEquals(aLoad3.getMinDSRating(), 0);
		assertFalse(aLoad3.isNonLinear());
		assertFalse(aLoad3.isNeutralCurrentCarrying());
		assertTrue(aLoad3.NHSRRuleApplies());
		assertEquals(aLoad3.getRequiredCircuitType(), CircuitType.DEDICATED_BRANCH);
	}
}