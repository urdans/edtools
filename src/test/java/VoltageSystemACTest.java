package test.java;

import eecalcs.systems.VoltageSystemAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoltageSystemACTest {
    VoltageSystemAC v120_1ph_2w = VoltageSystemAC.v120_1ph_2w;
    VoltageSystemAC v208_1ph_2w = VoltageSystemAC.v208_1ph_2w;
    VoltageSystemAC v240_1ph_3w = VoltageSystemAC.v240_1ph_3w;
    VoltageSystemAC v480_3ph_3w = VoltageSystemAC.v480_3ph_3w;
    VoltageSystemAC v480_3ph_4w = VoltageSystemAC.v480_3ph_4w;

    @Test
    void getName() {
        assertEquals("120v 1Ø 2W", v120_1ph_2w.getName());
        assertEquals("208v 1Ø 2W", v208_1ph_2w.getName());
        assertEquals("240v 1Ø 3W", v240_1ph_3w.getName());
        assertEquals("480v 3Ø 3W", v480_3ph_3w.getName());
        assertEquals("480v 3Ø 4W", v480_3ph_4w.getName());
    }

    @Test
    void getNames() {
        assertEquals("120v 1Ø 2W",  VoltageSystemAC.getNames()[0]);
        assertEquals("208v 1Ø 2W",  VoltageSystemAC.getNames()[1]);
        assertEquals("240v 1Ø 3W",  VoltageSystemAC.getNames()[7]);
        assertEquals("480v 3Ø 3W",  VoltageSystemAC.getNames()[13]);
        assertEquals("480v 3Ø 4W",  VoltageSystemAC.getNames()[14]);
    }

    @Test
    void getVoltage() {
        assertEquals(120, v120_1ph_2w.getVoltage());
        assertEquals(208, v208_1ph_2w.getVoltage());
        assertEquals(240, v240_1ph_3w.getVoltage());
        assertEquals(480, v480_3ph_3w.getVoltage());
        assertEquals(480, v480_3ph_4w.getVoltage());
    }

    @Test
    void getPhases() {
        assertEquals(1, v120_1ph_2w.getPhases());
        assertEquals(1, v208_1ph_2w.getPhases());
        assertEquals(1, v240_1ph_3w.getPhases());
        assertEquals(3, v480_3ph_3w.getPhases());
        assertEquals(3, v480_3ph_4w.getPhases());
    }

    @Test
    void getWires() {
        assertEquals(2, v120_1ph_2w.getWires());
        assertEquals(2, v208_1ph_2w.getWires());
        assertEquals(3, v240_1ph_3w.getWires());
        assertEquals(3, v480_3ph_3w.getWires());
        assertEquals(4, v480_3ph_4w.getWires());
    }

    @Test
    void getFactor() {
        assertEquals(1,   v120_1ph_2w.getFactor());
        assertEquals(1,   v208_1ph_2w.getFactor());
        assertEquals(1,   v240_1ph_3w.getFactor());
        assertEquals(Math.sqrt(3), v480_3ph_3w.getFactor());
        assertEquals(Math.sqrt(3), v480_3ph_4w.getFactor());
    }

	@Test
	void setCustom() {
        //checks that the values for a custom voltage system are set.
        VoltageSystemAC v_575_3_3 = VoltageSystemAC.v_other.setValues(575,3,3);
        assertEquals("575V 3Ø 3W",v_575_3_3.getName());

        //checks that a predefined voltage system remains immutable
        assertThrows(UnsupportedOperationException.class,
                ()-> {
                    VoltageSystemAC v140 =
                            VoltageSystemAC.v120_1ph_2w.setValues(140,5,-2);
                });

        //checks that a null voltage system remains null when trying to
        // initialize it from a predefined one.
        VoltageSystemAC v160 = null;
        try {
             v160 = VoltageSystemAC.v120_1ph_2w.setValues(160,1,1);
        }
        catch (Exception e) {
            assertNull(v160);
        }

        //voltage system does not change because wrong parameters
        VoltageSystemAC v115_1_2 =  VoltageSystemAC.v_other.setValues(0,0,0);
        assertEquals("575V 3Ø 3W", v115_1_2.setValues(0,0,0).getName());
        assertEquals("575V 3Ø 3W", v115_1_2.setValues(115,1,1).getName());
        assertEquals("575V 3Ø 3W", v115_1_2.setValues(120,1,6).getName());

        //check that a custom voltage system changes after correct values are set
        assertEquals("200V 3Ø 4W", v115_1_2.setValues(200,3,4).getName());
        assertEquals("120V 3Ø 4W", v115_1_2.setValues(120,3,4).getName());
        assertEquals("90V 1Ø 3W", v115_1_2.setValues(90,1,3).getName());
    }

    @Test
    void setSeveralCustom(){
        /*
        This test is to show that the voltage system as conceived up to now
        9/6/21 does not work well when we want to use several custom voltages.
         */
        VoltageSystemAC v100 = VoltageSystemAC.v_other.setValues(100,1,2);
        assertEquals(100, v100.getVoltage());
        assertEquals(1, v100.getFactor());
        assertEquals(1, v100.getPhases());
        assertEquals(2, v100.getWires());
        assertEquals("100V 1Ø 2W", v100.getName());

        VoltageSystemAC v120 = VoltageSystemAC.v_other.setValues(120,3,4);
        assertEquals(120, v120.getVoltage());
        assertEquals(Math.sqrt(3), v120.getFactor());
        assertEquals(3, v120.getPhases());
        assertEquals(4, v120.getWires());
        assertEquals("120V 3Ø 4W", v100.getName());

        //so far, so good, but now this fails:
        assertEquals(100, v100.getVoltage(),"This should fail for now");
    }
}