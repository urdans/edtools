package test.java;

import eecalcs.conductors.*;
import eecalcs.conductors.raceways.Bundle;
import eecalcs.conductors.raceways.Cable;
import eecalcs.conductors.raceways.Conductor;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BundleTest {
    private Bundle bundle;

    @Test
    void add() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertEquals(0, bundle.getConduitables().size());

        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertEquals(5, bundle.getConduitables().size());
    }

    @Test
    void remove() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable.copy());
        assertEquals(3, bundle.getConduitables().size());
    }

    @Test
    void empty() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertEquals(5, bundle.getConduitables().size());
    }

    @Test
    void isEmpty() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertTrue(bundle.isEmpty());

        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertFalse(bundle.isEmpty());
    }

    @Test
    void hasConduitable() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertTrue(bundle.isEmpty());

        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertTrue(bundle.hasConduitable(cable));
        assertTrue(bundle.hasConduitable(conductor));
        assertFalse(bundle.hasConduitable(conductor.copy()));
        assertFalse(bundle.hasConduitable(null));
    }

    @Test
    void getCurrentCarryingNumber() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertEquals(9, bundle.getCurrentCarryingCount());

        cable.setNeutralAsCurrentCarrying();
        assertEquals(10, bundle.getCurrentCarryingCount());
    }

    @Test
    void complyWith310_15_B_3_a_4() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertEquals(9, bundle.getCurrentCarryingCount());
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 1
        bundle.setBundlingLength(25);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(1, cable.getAdjustmentFactor()); //even if d>24", cables have this exception.


        //case2
        cable.setNeutralAsCurrentCarrying();
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(0.5, conductor.getAdjustmentFactor()); //because d>24", #ccc=10 and conductors don't have exceptions
        assertEquals(0.5, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (one cable

        // has more than 3 ccc.
        cable.setNeutralAsNonCurrentCarrying();
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 3
        cable.setJacketed();
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (one cable
        // is jacketed)

        cable.setNonJacketed();
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 4
        cable.setType(CableType.NM);
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (this
        // cable is not AC nor MC type.

        cable.setType(CableType.AC);
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 5
        conductor.setMetal(Metal.ALUMINUM);
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (AL)

        conductor.setMetal(Metal.COPPER);
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 6
        conductor.setSize(Size.AWG_8);
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (not #12AWG)

        conductor.setSize(Size.AWG_12);
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 7
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(20, bundle.getCurrentCarryingCount());
        assertEquals(0.5, conductor.getAdjustmentFactor()); //because d>24", #ccc=20 and conductors don't have
        // exceptions
        assertEquals(1.0, cable.getAdjustmentFactor()); //because d>24" and the exception is satisfied

        bundle.add(conductor.copy());
        assertEquals(0.45, conductor.getAdjustmentFactor()); //because d>24", #ccc=21 and conductors don't have
        // exceptions
        assertEquals(0.60, cable.getAdjustmentFactor()); //because d>24" and the exception is NOT satisfied (#ccc>20)
        //but the exception 310_15_B_3_a_5 is!
        assertFalse(bundle.compliesWith310_15_B_3_a_4());


        bundle = new Bundle(86);
        cable = new Cable(VoltageAC.v480_3ph_4w);
        cable.setType(CableType.AC);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertTrue(bundle.compliesWith310_15_B_3_a_4());

        //case 8
        bundle.setBundlingLength(24);
        bundle.add(conductor.copy());
        conductor = new Conductor();
        conductor.setSize(Size.AWG_8);
        conductor.setMetal(Metal.ALUMINUM);
        //at this point there are 21 ccc, one is #8AWG AL but because d<=24 no adjustment factor is applied,
        //making the bundle to behave as a free air
        assertEquals(21, bundle.getCurrentCarryingCount());
        assertEquals(24, bundle.getBundlingLength());
        assertEquals(1.0, conductor.getAdjustmentFactor()); //because conductor is alone
        assertEquals(1.0, cable.getAdjustmentFactor()); //because d<=24"
    }

    @Test
    void complyWith310_15_B_3_a_5() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        bundle.setBundlingLength(25);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(21, bundle.getCurrentCarryingCount());
        assertEquals(25, bundle.getBundlingLength());

        //case 1
        assertTrue(bundle.compliesWith310_15_B_3_a_5());
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.60, cable.getAdjustmentFactor());
    }

    @Test
    void complyWith310_15_B_3_a_5_01() {
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        bundle.setBundlingLength(25);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertFalse(bundle.compliesWith310_15_B_3_a_5());

        bundle.add(conductor);
        assertTrue(bundle.compliesWith310_15_B_3_a_5());

        //case 3
        cable.setJacketed();
        assertFalse(bundle.compliesWith310_15_B_3_a_5());

        cable.setNonJacketed();
        assertTrue(bundle.compliesWith310_15_B_3_a_5());

        //case 4
        cable.setType(CableType.NM);
        assertFalse(bundle.compliesWith310_15_B_3_a_5());
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.45, cable.getAdjustmentFactor());

        cable.setType(CableType.MC);
        assertTrue(bundle.compliesWith310_15_B_3_a_5());

        //case 5
        bundle.setBundlingLength(20);
        assertFalse(bundle.compliesWith310_15_B_3_a_5());
        assertEquals(1.0, conductor.getAdjustmentFactor());
        assertEquals(1.0, cable.getAdjustmentFactor());

        bundle.setBundlingLength(25);
        assertTrue(bundle.compliesWith310_15_B_3_a_5());
    }

}