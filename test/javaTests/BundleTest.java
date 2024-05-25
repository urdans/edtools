package javaTests;

import eecalcs.conductors.CableType;
import eecalcs.conductors.*;
import eecalcs.bundle.Bundle;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.systems.NECEdition;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void complyWith310_15_B_3_a_4(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        bundle = new Bundle(86);
        Conductor conductor = new Conductor();
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(cable);
        bundle.add(cable.copy());
        assertEquals(9, bundle.getCurrentCarryingCount());

        bundle.setBundlingLength(25);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(1, cable.getAdjustmentFactor()); //even if d>24", cables have this exception.

        cable.setNeutralAsCurrentCarrying();
        assertEquals(10, bundle.getCurrentCarryingCount());
        assertEquals(0.5, conductor.getAdjustmentFactor()); //because d>24", #ccc=10 and conductors don't have exceptions
        assertEquals(0.5, cable.getAdjustmentFactor()); //because d>24" and the exception is not

        // has more than 3 ccc.
        cable.setNeutralAsNonCurrentCarrying();
        cable.setJacketed();
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (one cable
        // is jacketed)

        cable.setNonJacketed();
        cable.setType(CableType.NM);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(0.7, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (this
        // cable is not AC nor MC type.

        cable.setType(CableType.AC);
        conductor.setMetal(Metal.ALUMINUM);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(1.0, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (AL)

        conductor.setMetal(Metal.COPPER);
        conductor.setSize(Size.AWG_8);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(1.0, cable.getAdjustmentFactor(), cable.toJSON()); //because d>24" and the exception is not


        conductor.setSize(Size.AWG_12);
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

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void complyWith310_15_B_3_a_5(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
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
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.60, cable.getAdjustmentFactor());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void complyWith310_15_B_3_a_5_01(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        bundle = new Bundle(86);
        bundle.setBundlingLength(25);
        Conductor conductor = new Conductor();
        bundle.add(conductor);
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
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        bundle.add(cable);
        bundle.add(cable.copy());
        cable.setNonJacketed();
        cable.setType(CableType.NM);
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.45, cable.getAdjustmentFactor());

        cable.setType(CableType.MC);
        assertEquals(0.45, conductor.getAdjustmentFactor());
        assertEquals(0.6, cable.getAdjustmentFactor());

        bundle.setBundlingLength(20);
        assertEquals(1.0, conductor.getAdjustmentFactor());
        assertEquals(1.0, cable.getAdjustmentFactor());
    }
}