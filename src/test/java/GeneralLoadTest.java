package test.java;

import eecalcs.loads.GeneralLoad;
import eecalcs.loads.Load;
import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneralLoadTest {

	@Test
	void setNonContinuous() {
		GeneralLoad generalLoad = new GeneralLoad(VoltageSystemAC.v120_1ph_2w
				,200);
		generalLoad.setNonContinuous();
		assertEquals(200, generalLoad.getNominalCurrent());
		assertEquals(200, generalLoad.getMCA());
		assertEquals(1.0, generalLoad.getMCAMultiplier());
		assertEquals(Load.Type.NONCONTINUOUS, generalLoad.getLoadType());
	}

	@Test
	void setContinuous() {
		GeneralLoad generalLoad = new GeneralLoad(VoltageSystemAC.v120_1ph_2w
				,100);
		generalLoad.setContinuous();
		assertEquals(100, generalLoad.getNominalCurrent());
		assertEquals(125, generalLoad.getMCA());
		assertEquals(125/100.0, generalLoad.getMCAMultiplier());
		assertEquals(Load.Type.CONTINUOUS, generalLoad.getLoadType());
	}

	@Test
	void setMixed() {
		GeneralLoad generalLoad = new GeneralLoad();
		generalLoad.setMixed(321.0);
		assertEquals(10, generalLoad.getNominalCurrent());
		assertEquals(321, generalLoad.getMCA());
		assertEquals(321/10.0, generalLoad.getMCAMultiplier());
		assertEquals(Load.Type.MIXED, generalLoad.getLoadType());
	}

	@Test
	void setNominalCurrent() {
		GeneralLoad generalLoad = new GeneralLoad();
		assertEquals(10, generalLoad.getNominalCurrent());
		assertEquals(Load.Type.NONCONTINUOUS, generalLoad.getLoadType());
		assertEquals(0, generalLoad.getOverloadRating());
		assertEquals(0, generalLoad.getDSRating());
		assertEquals(10, generalLoad.getMCA());

		generalLoad = new GeneralLoad(VoltageSystemAC.v120_1ph_2w,123);
		assertEquals(123, generalLoad.getNominalCurrent());
	}

	/*Testing features of the base OldLoad class*/
	@Test
	void loadConstructor() {
		GeneralLoad generalLoad = new GeneralLoad();
		assertEquals(VoltageSystemAC.v120_1ph_2w, generalLoad.getVoltageSystem());
		assertEquals(10.0, generalLoad.getNominalCurrent());
		assertEquals(10.0, generalLoad.getNeutralCurrent());
		assertEquals(1200.0, generalLoad.getVoltAmperes());
		assertEquals(1.0, generalLoad.getPowerFactor());
		assertEquals(1200.0, generalLoad.getWatts());
		assertEquals(10, generalLoad.getMCA());
		assertEquals(1, generalLoad.getMCAMultiplier());
		assertEquals(0.0, generalLoad.getMaxOCPDRating());
		assertEquals(0, generalLoad.getDSRating());
		assertEquals(0.0, generalLoad.getOverloadRating());
		assertNull(generalLoad.getDescription());
	}

	@Test
	void loadConstructor01() {
		GeneralLoad generalLoad = new GeneralLoad(VoltageSystemAC.v208_3ph_4w
				, 20);
		generalLoad.setDescription("Induction heater");
		generalLoad.setPowerFactor(-0.8);
		assertEquals(20.0, generalLoad.getNominalCurrent());
		assertEquals(20.0, generalLoad.getNeutralCurrent());
		assertEquals(208*20*Math.sqrt(3), generalLoad.getVoltAmperes());
		assertEquals(0.8, generalLoad.getPowerFactor());
		assertEquals(208*20*Math.sqrt(3)*0.8, generalLoad.getWatts());
		assertEquals(20, generalLoad.getMCA());
		assertEquals(1, generalLoad.getMCAMultiplier());
		assertEquals(0.0, generalLoad.getMaxOCPDRating());
		assertEquals(0.0, generalLoad.getDSRating());
		assertEquals(0.0, generalLoad.getOverloadRating());
		assertEquals("Induction heater", generalLoad.getDescription());

		generalLoad.setContinuous();
		assertEquals(20.0*1.25, generalLoad.getMCA());
		assertEquals(1.25, generalLoad.getMCAMultiplier());

		GeneralLoad generalLoad2 = new GeneralLoad(VoltageSystemAC.v240_1ph_3w, 25);
		assertEquals( Load.Type.NONCONTINUOUS,
				generalLoad2.getLoadType());
		assertEquals(25.0, generalLoad2.getNominalCurrent());
		assertEquals(25.0, generalLoad2.getNeutralCurrent());
		assertEquals(240*25.0, generalLoad2.getVoltAmperes());
		assertEquals(1.0, generalLoad2.getPowerFactor());
		assertEquals(240*25.0, generalLoad2.getWatts());
		assertEquals(25.0, generalLoad2.getMCA());
		assertEquals(1, generalLoad2.getMCAMultiplier());
		assertEquals(0.0, generalLoad2.getMaxOCPDRating());
		assertEquals(0, generalLoad2.getDSRating());
		assertEquals(0.0, generalLoad2.getOverloadRating());
	}

	@Test
	void getACopy() {
		GeneralLoad load = new GeneralLoad(VoltageSystemAC.v480_3ph_4w,321);
		load.setContinuous()/*.setNonlinear()*/.setPowerFactor(0.83);
		assertEquals(Load.Type.CONTINUOUS, load.getLoadType());
		assertFalse(load.isNonlinear());
		assertEquals(0.83, load.getPowerFactor());

		Load clonedLoad = load.getACopy();
		assertTrue(clonedLoad instanceof GeneralLoad);
		assertEquals(load.toJSON(), ((GeneralLoad)clonedLoad).toJSON());

		assertFalse(load.isNeutralCurrentCarrying());
	}

	@Test
	void testSetContinuous() {
		GeneralLoad load = new GeneralLoad(VoltageSystemAC.v208_1ph_2w,100);
		load.setContinuous();
		assertEquals(100, load.getNominalCurrent());
		assertEquals(125, load.getMCA());
		assertEquals(1.25, load.getMCAMultiplier());
		assertEquals(Load.Type.CONTINUOUS, load.getLoadType());
		assertFalse(load.isNeutralCurrentCarrying());
	}

	@Test
	void testSetNonContinuous() {
		GeneralLoad load = new GeneralLoad(VoltageSystemAC.v208_3ph_4w,100);
		load.setNonContinuous();
		assertEquals(100, load.getNominalCurrent());
		assertEquals(100, load.getMCA());
		assertEquals(1.0, load.getMCAMultiplier());
		assertEquals(Load.Type.NONCONTINUOUS, load.getLoadType());
		assertFalse(load.isNeutralCurrentCarrying());
	}

	@Test
	void testSetMixed() {
		GeneralLoad load = new GeneralLoad(VoltageSystemAC.v208_3ph_4w,100);
		load.setMixed(150);
		assertEquals(100, load.getNominalCurrent());
		assertEquals(150, load.getMCA());
		assertEquals(1.5, load.getMCAMultiplier());
		assertEquals(Load.Type.MIXED, load.getLoadType());
		assertFalse(load.isNeutralCurrentCarrying());
	}

/*	@Test
	void setNonlinear() {
		GeneralLoad load = new GeneralLoad(VoltageSystemAC.v208_1ph_3w,100);
		load.setNonlinear();
		assertEquals(100, load.getNominalCurrent());
		assertEquals(100, load.getMCA());
		assertEquals(1.0, load.getMCAMultiplier());
		assertEquals(Load.Type.NONCONTINUOUS, load.getLoadType());
		assertTrue(load.isNonlinear());
		assertTrue(load.isNeutralCurrentCarrying());
	}*/

	@Test
	void setPowerFactor() {
		GeneralLoad load = new GeneralLoad();
		assertEquals(1.0, load.getPowerFactor());
		assertEquals(10*120, load.getVoltAmperes());
		assertEquals(10*120, load.getWatts());

//		System.out.println(load);
		load.setPowerFactor(0.8);
//		System.out.println(load);
		assertEquals(0.8, load.getPowerFactor());
		assertEquals(10*120, load.getVoltAmperes());
		assertEquals(10*120*0.8, load.getWatts());
	}
}