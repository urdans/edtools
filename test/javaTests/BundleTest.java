package javaTests;

import eecalcs.bundle.ROBundle;
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
        //getting the copies
        conductor = (Conductor) bundle.getConduitables().get(0);
        cable = (Cable) bundle.getConduitables().get(3);

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
        //getting the copy
        cable = (Cable) bundle.getConduitables().get(3);
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
        //getting the copies
        conductor = (Conductor) bundle.getConduitables().get(0);
        cable = (Cable) bundle.getConduitables().get(3);

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
        conductor.setMetal(ConductiveMetal.ALUMINUM);
        assertEquals(0.7, conductor.getAdjustmentFactor()); //because d>24", #ccc=9 and conductors don't have exceptions
        assertEquals(1.0, cable.getAdjustmentFactor()); //because d>24" and the exception is not satisfied (AL)

        conductor.setMetal(ConductiveMetal.COPPER);
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
        conductor.setMetal(ConductiveMetal.ALUMINUM);
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
        //getting the copies
        conductor = (Conductor) bundle.getConduitables().get(0);
        cable = (Cable) bundle.getConduitables().get(3);


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
        //getting the copies
        conductor = (Conductor) bundle.getConduitables().get(0);
        cable = (Cable) bundle.getConduitables().get(15);

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

    @Test
    void updatingAmbientTemperature(){
        Conductor conductor1 = new Conductor();
        conductor1.setSize(Size.AWG_1).setMetal(ConductiveMetal.COPPER).setInsulation(Insulation.TW);
        conductor1.setAmbientTemperatureF(81);

        Conductor conductor2 = new Conductor();
        conductor2.setSize(Size.AWG_2).setMetal(ConductiveMetal.ALUMINUM).setInsulation(Insulation.THW);
        conductor2.setAmbientTemperatureF(82);

        Conductor conductor3 = new Conductor();
        conductor3.setSize(Size.AWG_3).setMetal(ConductiveMetal.COPPERCOATED).setInsulation(Insulation.THHW);
        conductor3.setAmbientTemperatureF(83);

        Bundle bundle = new Bundle(100);

        /*That the ambient temperature of a conductor changes to the one of the bundle when added to the bundle*/
        assertEquals(81, conductor1.getAmbientTemperatureF());
        bundle.add(conductor1);
        //getting the copy
        conductor1 = (Conductor) bundle.getConduitables().get(0);

        assertEquals(100, conductor1.getAmbientTemperatureF());

        /*That the ambient temperature of the conductor changes to the one of the bundle when added to the bundle,
        but also that this addition does not affect the ambient temperature of the other conductors already in the
        bundle*/
        assertEquals(82, conductor2.getAmbientTemperatureF());
        bundle.add(conductor2);
        //getting the copy
        conductor2 = (Conductor) bundle.getConduitables().get(1);

        assertEquals(100, conductor2.getAmbientTemperatureF());
        assertEquals(100, conductor1.getAmbientTemperatureF());

        /*That the ambient temperature of the conductor changes to the one of the bundle when added to the bundle,
        but also that this addition does not affect the ambient temperature of the other conductors already in the
        bundle*/
        assertEquals(83, conductor3.getAmbientTemperatureF());
        bundle.add(conductor3);
        //getting the copy
        conductor3 = (Conductor) bundle.getConduitables().get(2);
        assertEquals(100, conductor3.getAmbientTemperatureF());
        assertEquals(100, conductor2.getAmbientTemperatureF());
        assertEquals(100, conductor1.getAmbientTemperatureF());

        /*That the ambient temperature of the conductor inside the bundle cannot be changed*/
//        assertThrows(IllegalArgumentException.class, () -> conductor1.setAmbientTemperatureF(81));
//        assertThrows(IllegalArgumentException.class, () -> conductor2.setAmbientTemperatureF(82));
//        assertThrows(IllegalArgumentException.class, () -> conductor3.setAmbientTemperatureF(83));

        /*That despite this attempt, the temperature of the conductors remains the same as of the bundle*/
        assertEquals(bundle.getAmbientTemperatureF(), conductor1.getAmbientTemperatureF());
        assertEquals(bundle.getAmbientTemperatureF(), conductor2.getAmbientTemperatureF());
        assertEquals(bundle.getAmbientTemperatureF(), conductor3.getAmbientTemperatureF());

        /*That the ambient temperature of the bundle can be set and that the same is propagated to the contained
        conductor*/
        bundle.setAmbientTemperatureF(99);
        assertEquals(bundle.getAmbientTemperatureF(), conductor1.getAmbientTemperatureF());
        assertEquals(bundle.getAmbientTemperatureF(), conductor2.getAmbientTemperatureF());
        assertEquals(bundle.getAmbientTemperatureF(), conductor3.getAmbientTemperatureF());

        /*That the conductors are still part of the bundle*/
        assertTrue(bundle.hasConduitable(conductor1));
        assertTrue(bundle.hasConduitable(conductor2));
        assertTrue(bundle.hasConduitable(conductor3));
        assertTrue(conductor1.hasBundle());
        assertTrue(conductor2.hasBundle());
        assertTrue(conductor3.hasBundle());
        assertFalse(conductor1.hasConduit());
        assertFalse(conductor2.hasConduit());
        assertFalse(conductor3.hasConduit());
    }

    @Test
    void casting() {
        bundle = new Bundle(86);
        bundle.setBundlingLength(25);
        Conductor conductor = new Conductor();
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());

        ROBundle readOnlyBundle = bundle;

        ((Bundle) readOnlyBundle).add(conductor.copy());

        assertEquals(4, readOnlyBundle.getConductorCount());
    }
}

