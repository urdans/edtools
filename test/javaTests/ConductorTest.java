package javaTests;

import eecalcs.conductors.*;
import eecalcs.bundle.Bundle;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.Type;
import eecalcs.conductors.TempRating;
import eecalcs.systems.NECEdition;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


import static org.junit.jupiter.api.Assertions.*;

class ConductorTest {
    Conductor conductor;
    Bundle bundle;
    Conduit conduit;

    //@Test
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void testCopy(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor cond1 = new Conductor()
                .setSize(Size.AWG_8)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.XHHW)
                .setLength(123);
        Conduit conduit = new Conduit(105)
                .setType(Type.EMT)
                .setNipple();
        conduit.add(cond1);
//        cond1.setCopperCoating(Coating.COATED);
        cond1.setRole(Conductor.Role.GND);
        String cond1S = cond1.hasConduit()+", "+/*cond1.getCopperCoating()+
                ", "+*/cond1.getCurrentCarryingCount()+", "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        //--cloning
        Conductor cond2 = cond1.copy();
        String cond2S = cond2.hasConduit()+", "+/*cond2.getCopperCoating()+", "+*/cond2.getCurrentCarryingCount()+", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();
        cond2.setAmbientTemperatureF(155);
//        cond2.setCopperCoating(Coating.UNCOATED);
        cond2.setRole(Conductor.Role.HOT);
        cond2.setSize(Size.AWG_4$0);
        cond2.setMetal(Metal.COPPER);
        cond2.setInsulation(Insul.TW);
        cond2.setLength(78);
        String cond1SS = cond1.hasConduit()+", "+/*cond1.getCopperCoating()+", "+*/cond1.getCurrentCarryingCount()+"," +
                " "+cond1.getDescription()
                +", "+cond1.getInsulatedAreaIn2()+", "+cond1.getInsulation().getName()+", "+cond1.getLength()+", "+cond1.getMetal().getSymbol()+", "+ cond1.getRole()+", "+cond1.getSize().getName()+", "+cond1.getTemperatureRating();
        String cond2SS = cond2.hasConduit()+", "+/*cond2.getCopperCoating()+", "+*/cond2.getCurrentCarryingCount()+
                ", "+cond2.getDescription()
                +", "+cond2.getInsulatedAreaIn2()+", "+cond2.getInsulation().getName()+", "+cond2.getLength()+", "+cond2.getMetal().getSymbol()+", "+ cond2.getRole()+", "+cond2.getSize().getName()+", "+cond2.getTemperatureRating();
        assertEquals(cond1S, cond1SS);
        assertNotEquals(cond2S, cond2SS);
    }

    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getTemperatureRating(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        assertSame(conductor.getTemperatureRating(), TempRating.T75);

        conductor.setInsulation(Insul.XHHW2);
        assertSame(conductor.getTemperatureRating(), TempRating.T90);

        conductor.setInsulation(Insul.TW);
        assertSame(conductor.getTemperatureRating(), TempRating.T60);
    }

    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void setAmbientTemperatureF(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Bundle bundle1 = new Bundle(86);
        Cable cable1 = new Cable(VoltageAC.v120_1ph_2w);
        Cable cable2 = new Cable(VoltageAC.v120_1ph_2w);
        Cable cable3 = new Cable(VoltageAC.v120_1ph_2w);
        bundle1.setBundlingLength(30);
        bundle1.add(cable1);
        bundle1.add(cable2);
        bundle1.add(cable3);
        assertEquals(86, cable1.getAmbientTemperatureF());
        assertEquals(86, cable2.getAmbientTemperatureF());
        assertEquals(86, cable3.getAmbientTemperatureF());

        cable2 = cable2.copy().setAmbientTemperatureF(95);
        assertEquals(86, cable1.getAmbientTemperatureF());
        assertEquals(95, cable2.getAmbientTemperatureF());
        assertEquals(86, cable3.getAmbientTemperatureF());
    }

    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor()
                .setSize(Size.AWG_12)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THHN)
                .setLength(125);
        conduit = new Conduit(100)
                .setType(Type.PVC80)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        assertEquals(30*0.91*0.8, conductor.getCorrectedAndAdjustedAmpacity());

        bundle = new Bundle(100);
        bundle.setBundlingLength(25);
        conductor = conductor.copy();
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(30*0.91*0.7, conductor.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void getCorrectionFactorNEC2014() {
        NECEdition.setDefault(NECEdition.NEC2014);

        conductor = new Conductor()
                .setSize(Size.KCMIL_250)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.THHW)
                .setLength(125);
        assertEquals(1, conductor.getCorrectionFactor());

        conductor.setAmbientTemperatureF(100);
        assertEquals(0.91, conductor.getCorrectionFactor(), 0.001);

        //nipple state should not affect correction factor
        conduit = new Conduit(108).setType(Type.PVC80);
        conduit.add(conductor);
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);
        conduit.setNipple();
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);


        conduit.setRooftopDistance(10);
        assertEquals(0.71, conductor.getCorrectionFactor(), 0.001);

        conduit.setRooftopDistance(0.5);
        assertEquals(0.41, conductor.getCorrectionFactor(), 0.001);

        conductor.setInsulation(Insul.XHHW2);
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class, names = {"NEC2017", "NEC2020"})
    void getCorrectionFactorNEC2017_2020(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);

        conductor = new Conductor()
                .setSize(Size.KCMIL_250)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.THHW)
                .setLength(125);
        assertEquals(1, conductor.getCorrectionFactor());

        conductor.setAmbientTemperatureF(100);
        assertEquals(0.91, conductor.getCorrectionFactor(), 0.001);

        //nipple state should not affect correction factor
        conduit = new Conduit(108).setType(Type.PVC80);
        conduit.add(conductor);
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);
        conduit.setNipple();
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);

        conduit.setRooftopDistance(10);
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);

        conduit.setRooftopDistance(0.5);
        assertEquals(0.41, conductor.getCorrectionFactor(), 0.001);

        conductor.setInsulation(Insul.XHHW2);
        assertEquals(0.87, conductor.getCorrectionFactor(), 0.001);
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor01(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);

        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        assertEquals(0.8, conductor.getAdjustmentFactor());
        assertEquals(0.8, conduit.getConduitables().get(2).getAdjustmentFactor());

        conduit.setNipple();
        assertEquals(1, conductor.getAdjustmentFactor());

        conduit.setNonNipple();
        assertEquals(0.8, conductor.getAdjustmentFactor());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor02(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(3, conduit.getCurrentCarryingCount());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor03(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);

        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor);
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        Conductor conductor1 = conductor.copy();
        conduit.add(conductor1);
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        assertEquals(7, conduit.getCurrentCarryingCount());
        assertEquals(0.7, conductor.getAdjustmentFactor());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor04(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        conduit.add(conductor.copy());
        conduit.add(conductor.copy());
        Conductor conductor1 = conductor.copy();
        conduit.add(conductor1);
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        conduit.add(conductor1.copy());
        assertEquals(6, conduit.getCurrentCarryingCount());
        assertEquals(0.8, conductor1.getAdjustmentFactor());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor05(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        conduit = new Conduit(86)
                .setType(Type.EMT)
                .setNonNipple();
        Conductor conductor1 = conductor.copy();
        assertEquals(1, conductor.getAdjustmentFactor());
        assertEquals(1, conductor1.getAdjustmentFactor());
        assertFalse(conductor.hasConduit());
        assertFalse(conductor1.hasConduit());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor06(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        bundle = new Bundle(86);
        bundle.setBundlingLength(25);
        bundle.add(conductor);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(25, bundle.getBundlingLength());
        assertEquals(0.8, conductor.getAdjustmentFactor());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAdjustmentFactor07(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor()
                .setSize(Size.AWG_4)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THW)
                .setLength(70);
        Conductor conductor1 = conductor.copy();
        bundle = new Bundle(86);
        bundle.setBundlingLength(25);
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        bundle.add(conductor.copy());
        assertEquals(3, bundle.getCurrentCarryingCount());
        assertEquals(1, conductor.getAdjustmentFactor());

        bundle.setBundlingLength(24);
        assertEquals(1, conductor.getAdjustmentFactor());
        assertFalse(conductor1.hasConduit());
        assertFalse(conductor1.hasBundle());
    }

    @Test
    void illustrateGuideExample1Page101(){
        Conductor conductor2 =
        new Conductor().setSize(Size.AWG_2)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THWN2)
                .setLength(10);
        Conduit raceway = new Conduit(110)
                .setType(Type.PVC40)
                .setNonNipple();
        raceway.setRooftopDistance(4);
        raceway.add(conductor2);
        raceway.add(conductor2.copy());

        NECEdition.setDefault(NECEdition.NEC2014);
        assertEquals(1.0, conductor2.getAdjustmentFactor());
        assertEquals(0.71, conductor2.getCorrectionFactor());
        assertEquals(92.3, conductor2.getCorrectedAndAdjustedAmpacity());

        NECEdition.setDefault(NECEdition.NEC2020);
        assertEquals(1.0, conductor2.getAdjustmentFactor());
        assertEquals(0.87, conductor2.getCorrectionFactor());
        assertEquals(113.1, conductor2.getCorrectedAndAdjustedAmpacity());

    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void illustrateGuideContinuousLoadPage102_case1(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);

        Conductor conductor2 = new Conductor()
                .setSize(Size.AWG_12)
                .setMetal(Metal.COPPER)
                .setInsulation(Insul.THHW)
                .setLength(10);
        Conduit raceway = new Conduit(TempRating.getFahrenheit(43))
                .setType(Type.PVC40)
                .setNonNipple();
        raceway.add(conductor2);
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        raceway.add(conductor2.copy());
        assertEquals(6, raceway.getCurrentCarryingCount());
        assertEquals(0.87, conductor2.getCorrectionFactor());
        assertEquals(0.8, conductor2.getAdjustmentFactor());
        assertEquals(0.696, conductor2.getCompoundFactor(), 0.0001);
        assertEquals(0.568, conductor2.getCompoundFactor(TempRating.T60));
        assertEquals(0.656, conductor2.getCompoundFactor(TempRating.T75));
        assertEquals(0.696, conductor2.getCompoundFactor(TempRating.T90), 0.0001);
        assertEquals(20.880, conductor2.getCorrectedAndAdjustedAmpacity(), 0.0001);
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void copyFrom(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor2 = new Conductor()
                .setSize(Size.KCMIL_350)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.TBS)
                .setLength(1.1234)
                .setAmbientTemperatureF(132)
//                .setCopperCoating(Coating.COATED)
                .setRole(Conductor.Role.NCONC);

        Bundle bundle = new Bundle(110);
        bundle.add(conductor2);

        conductor = new Conductor();
        Conduit conduit = new Conduit(111);
        conduit.add(conductor);
        assertEquals(Size.AWG_12, conductor.getSize());

        conductor.copyFrom(conductor2);
        assertEquals(conductor2.getSize(), conductor.getSize());
        assertEquals(conductor2.getMetal(), conductor.getMetal());
        assertEquals(conductor2.getInsulation(), conductor.getInsulation());
        assertEquals(conductor2.getLength(), conductor.getLength());
        assertEquals(conductor2.getAmbientTemperatureF(), conductor.getAmbientTemperatureF());
//        assertEquals(conductor2.getCopperCoating(), conductor.getCopperCoating());
        assertEquals(conductor2.getRole(), conductor.getRole());

        assertTrue(conductor2.hasBundle());
        assertTrue(conductor.hasConduit());

        assertFalse(conductor2.hasConduit());
        assertFalse(conductor.hasBundle());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void copyAndClone(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor phaseA = new Conductor()
                .setSize(Size.KCMIL_350)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.TBS)
                .setLength(1.1234)
                .setAmbientTemperatureF(132)
//                .setCopperCoating(Coating.COATED)
                .setRole(Conductor.Role.NEUCC);

        Conduit conduit = new Conduit(110);
        conduit.add(phaseA);

        Conductor phaseB = phaseA.copy();

        assertEquals(phaseA.getSize(), phaseB.getSize());
        assertEquals(phaseA.getMetal(), phaseB.getMetal());
        assertEquals(phaseA.getInsulation(), phaseB.getInsulation());
        assertEquals(phaseA.getLength(), phaseB.getLength());
        assertEquals(phaseA.getAmbientTemperatureF(), phaseB.getAmbientTemperatureF());
//        assertEquals(phaseA.getCopperCoating(), phaseB.getCopperCoating());
        assertEquals(phaseA.getRole(), phaseB.getRole());
        assertTrue(phaseA.hasConduit());
        assertFalse(phaseA.hasBundle());

        assertFalse(phaseB.hasBundle());
        assertFalse(phaseB.hasConduit());
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_setting_nulls_for_coating_and_role(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        conductor = new Conductor();

//        assertThrows(IllegalArgumentException.class, () -> conductor.setCopperCoating(null));
//        assertEquals(Coating.UNCOATED, conductor.getCopperCoating());

        assertThrows(IllegalArgumentException.class, () -> conductor.setRole(null));
    }


    //---------------------------------------------------------------------
    // Generated by CodiumAI
    //---------------------------------------------------------------------

    // can create a conductor with default values

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_create_conductor_with_default_values(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        assertEquals(Size.AWG_12, conductor.getSize());
        assertEquals(Metal.COPPER, conductor.getMetal());
        assertEquals(Insul.THW, conductor.getInsulation());
        assertEquals(100, conductor.getLength(), 0.001);
        assertEquals(86, conductor.getAmbientTemperatureF());
//        assertEquals(Coating.UNCOATED, conductor.getCopperCoating());
        assertEquals(Conductor.Role.HOT, conductor.getRole());
        assertFalse(conductor.hasConduit());
        assertFalse(conductor.hasBundle());
    }

    // can set conductor properties
    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_set_conductor_properties(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        conductor.setSize(Size.AWG_10);
        conductor.setMetal(Metal.ALUMINUM);
        conductor.setInsulation(Insul.THHW);
        conductor.setLength(50);
        conductor.setAmbientTemperatureF(90);
//        conductor.setCopperCoating(Coating.COATED);
        conductor.setRole(Conductor.Role.GND);

        Conduit conduit = new Conduit(110);
        conduit.add(conductor);

        Conductor conductor2 = conductor.copy();
        Bundle bundle = new Bundle(111);
        bundle.add(conductor2);

        assertEquals(Size.AWG_10, conductor.getSize());
        assertEquals(Metal.ALUMINUM, conductor.getMetal());
        assertEquals(Insul.THHW, conductor.getInsulation());
        assertEquals(50, conductor.getLength(), 0.001);
        assertEquals(110, conductor.getAmbientTemperatureF());
//        assertEquals(Coating.COATED, conductor.getCopperCoating());
        assertEquals(Conductor.Role.GND, conductor.getRole());
	    assertTrue(conductor.hasConduit());
        assertTrue(conductor2.hasBundle());
    }

    // can get conductor properties
    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_get_conductor_properties(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        conductor.setSize(Size.AWG_8);
        conductor.setMetal(Metal.COPPER);
        conductor.setInsulation(Insul.THW);
        conductor.setLength(75);
        conductor.setAmbientTemperatureF(80);
//        conductor.setCopperCoating(Coating.UNCOATED);
        conductor.setRole(Conductor.Role.NEUNCC);

        assertEquals(Size.AWG_8, conductor.getSize());
        assertEquals(Metal.COPPER, conductor.getMetal());
        assertEquals(Insul.THW, conductor.getInsulation());
        assertEquals(75, conductor.getLength(), 0.001);
        assertEquals(80, conductor.getAmbientTemperatureF());
//        assertEquals(Coating.UNCOATED, conductor.getCopperCoating());
        assertEquals(Conductor.Role.NEUNCC, conductor.getRole());
    }

    // throws an exception when setting length to zero or negative
    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_set_length_zero_or_negative(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        assertThrows(IllegalArgumentException.class, () -> conductor.setLength(0));
        assertThrows(IllegalArgumentException.class, () -> conductor.setLength(-10));
    }

    // throws an exception when setting ambient temperature outside valid range
    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_set_ambient_temperature_outside_valid_range(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        assertThrows(IllegalArgumentException.class, () -> conductor.setAmbientTemperatureF(186));
        assertThrows(IllegalArgumentException.class, () -> conductor.setAmbientTemperatureF(-77));
    }

    // throws an exception when setting ambient temperature to a conductor that belongs to a conduit or bundle
    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void test_set_ambient_temperature_to_conductor_in_conduit_or_bundle(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Conductor conductor = new Conductor();
        Conduit conduit = new Conduit(110);
        conduit.add(conductor);
        assertThrows(IllegalArgumentException.class, () -> conductor.setAmbientTemperatureF(90));

        Conductor conductor2 = new Conductor();
        Bundle bundle = new Bundle(111);
        bundle.add(conductor2);
        assertThrows(IllegalArgumentException.class, () -> conductor.setAmbientTemperatureF(80));
    }

    @Test //that we cannot set the conduit of a conductor by calling setConduit() from a different class other than
        // Conduit.
    void test_setConduit() {
        Conductor conductor = new Conductor();
        Conduit conduit = new Conduit(86);
        assertThrows(IllegalCallerException.class, () -> conductor.setConduit(conduit));
        conduit.add(conductor);
        assertThrows(IllegalCallerException.class, () -> conductor.setConduit(conduit));
    }
}