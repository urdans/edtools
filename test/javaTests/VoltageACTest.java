package javaTests;

import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoltageACTest {
    VoltageAC v120_1ph_2w = VoltageAC.v120_1ph_2w;
    VoltageAC v208_1ph_2w = VoltageAC.v208_1ph_2w;
    VoltageAC v240_1ph_3w = VoltageAC.v240_1ph_3w;
    VoltageAC v480_3ph_3w = VoltageAC.v480_3ph_3w;
    VoltageAC v480_3ph_4w = VoltageAC.v480_3ph_4w;

    @Test
    void getName() {
        assertEquals("120V 1Ø 2W", v120_1ph_2w.getName());
        assertEquals("208V 1Ø 2W", v208_1ph_2w.getName());
        assertEquals("240/120V 1Ø 3W", v240_1ph_3w.getName());
        assertEquals("480V 3Ø 3W", v480_3ph_3w.getName());
        assertEquals("480V 3Ø 4W", v480_3ph_4w.getName());
    }

    @Test
    void getNames() {
        assertEquals("120V 1Ø 2W",  VoltageAC.getNames()[0]);
        assertEquals("208V 1Ø 2W",  VoltageAC.getNames()[1]);
        assertEquals("240/120V 1Ø 3W",  VoltageAC.getNames()[7]);
        assertEquals("480V 3Ø 3W",  VoltageAC.getNames()[13]);
        assertEquals("480V 3Ø 4W",  VoltageAC.getNames()[14]);
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

}