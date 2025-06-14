package javaTests;

import eecalcs.conductors.CableType;
import eecalcs.conductors.*;
import eecalcs.bundle.Bundle;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.TradeSize;
import eecalcs.conduits.Type;
import eecalcs.conductors.TempRating;
import eecalcs.systems.NECEdition;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class CableTest {
    @BeforeAll
    static void setConditions(){
    }


    //NEC Edition independent
    @Test
    void getInsulatedAreaIn2() {
        double diameter = 2;
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(diameter);
        assertEquals(diameter*diameter*0.25*Math.PI, cable.getInsulatedAreaIn2());

        diameter = 0.5;
        cable.setOuterDiameter(diameter);
        assertEquals(diameter*diameter*0.25*Math.PI, cable.getInsulatedAreaIn2());

        diameter = 0;
        cable.setOuterDiameter(diameter);
        assertEquals(Cable.MINIMUM_OUTER_DIAMETER*Cable.MINIMUM_OUTER_DIAMETER*0.25*Math.PI,
            cable.getInsulatedAreaIn2());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount() {
        Cable cable2 = new Cable(VoltageAC.v120_1ph_2w);
        assertEquals(2, cable2.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_00() {
        Cable cable = new Cable(VoltageAC.v480_3ph_4w).setOuterDiameter(1);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setNeutralAsCurrentCarrying();
        assertEquals(4, cable.getCurrentCarryingCount());

        cable.setNeutralAsNonCurrentCarrying();
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_01() {
        Cable cable = new Cable(VoltageAC.v240_3ph_3w);
        assertThrows(IllegalArgumentException.class, cable::setNeutralAsCurrentCarrying);
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_02() {
        Cable cable = new Cable(VoltageAC.v208_3ph_4w);
        cable.setNeutralAsCurrentCarrying();
        assertEquals(4, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_03() {
        Cable cable = new Cable(VoltageAC.v240_1ph_2w);
        assertThrows(IllegalArgumentException.class, cable::setNeutralAsCurrentCarrying);
        assertEquals(2, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_04() {
        Cable cable = new Cable(VoltageAC.v240_1ph_3w);
        cable.setNeutralAsCurrentCarrying();
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_05() {
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        cable.setNeutralAsCurrentCarrying();
        assertEquals(2, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @Test
    void getCurrentCarryingCount_06() {
        Cable cable = new Cable(VoltageAC.v240_3ph_4w);
        cable.setNeutralAsCurrentCarrying();
        assertEquals(4, cable.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void roofTopDistanceAndCondition_01(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertEquals(-1, cable.getRooftopDistance());
        assertFalse(cable.isRoofTopCondition());
        cable.setRoofTopDistance(5);
        assertEquals(5, cable.getRooftopDistance());

        if(NECEdition.getDefault() == NECEdition.NEC2014) {
            assertTrue(cable.isRoofTopCondition());
            cable.setRoofTopDistance(36);
            assertEquals(36, cable.getRooftopDistance());
            assertTrue(cable.isRoofTopCondition());

            cable.setRoofTopDistance(36.1);
            assertEquals(36.1, cable.getRooftopDistance());
            assertFalse(cable.isRoofTopCondition());

            cable.setRoofTopDistance(7.0/8.0);
            assertEquals(7.0/8.0, cable.getRooftopDistance());
            assertTrue(cable.isRoofTopCondition());

        }
        else {//NEC2017, NEC2020
            assertFalse(cable.isRoofTopCondition());
            cable.setRoofTopDistance(36);
            assertEquals(36, cable.getRooftopDistance());
            assertFalse(cable.isRoofTopCondition());

            cable.setRoofTopDistance(36.1);
            assertEquals(36.1, cable.getRooftopDistance());
            assertFalse(cable.isRoofTopCondition());

            cable.setRoofTopDistance(7.0/8.0);
            assertEquals(7.0/8.0, cable.getRooftopDistance());
            assertTrue(cable.isRoofTopCondition());

            cable.setRoofTopDistance(8.0/8.0);
            assertEquals(8.0/8.0, cable.getRooftopDistance());
            assertFalse(cable.isRoofTopCondition());
        }

        cable.resetRoofTopCondition();
        assertEquals(-1, cable.getRooftopDistance());
    }

    //NEC Edition independent
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void roofTopDistanceAndCondition_02(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        //by default, a cable is not in rooftop condition
        assertFalse(cable.isRoofTopCondition());

        /*by default, a conduit is not in rooftop condition, neither any of
         its cables.*/
        Conduit conduit = new Conduit(86);
        conduit.add(cable);
        assertFalse(cable.isRoofTopCondition());
        assertFalse(conduit.isRoofTopCondition());

        /*if the rooftop condition of the conduit is set, so is the cable.*/
        conduit.setRooftopDistance(0.5);
        assertTrue(cable.isRoofTopCondition());
        assertTrue(conduit.isRoofTopCondition());

        /*the cable rooftop condition cannot be reset if the cable belongs to
         a conduit. Rooftop condition of both conduit and cable remains
         unchanged.*/
        assertThrows(IllegalArgumentException.class, cable::resetRoofTopCondition);
        assertTrue(cable.isRoofTopCondition());
        assertEquals(0.5, cable.getRooftopDistance());
        assertTrue(conduit.isRoofTopCondition());
        assertEquals(0.5, conduit.getRooftopDistance());


        /*if a conduit rooftop condition is reset, so is the cable.*/
        conduit.setRooftopDistance(-1);
        assertFalse(cable.isRoofTopCondition());
        assertFalse(conduit.isRoofTopCondition());

        /*if a cable rooftop condition is set, nothing happens to either the
         cable or the conduit, because rooftop condition of cable is the
         rooftop condition of its conduit.*/
        assertThrows(IllegalArgumentException.class, () -> cable.setRoofTopDistance(5));
        assertFalse(cable.isRoofTopCondition());
        assertFalse(conduit.isRoofTopCondition());
    }

    //NEC Edition independent
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(1);
        cable.setPhaseConductorSize(Size.KCMIL_300);
        assertEquals(285, cable.getCorrectedAndAdjustedAmpacity());

        cable.setAmbientTemperatureF(100);
        assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(38);
        assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(36);
        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.67, cable.getCorrectedAndAdjustedAmpacity());
        else //NECEdition.getDefault == NECEdition.NEC2017 ||  NECEdition.NEC2020
            assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());


        cable.setRoofTopDistance(6);
        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.67, cable.getCorrectedAndAdjustedAmpacity());
        else //NECEdition.getDefault == NECEdition.NEC2017 ||  NECEdition.NEC2020
            assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(2);
        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.58, cable.getCorrectedAndAdjustedAmpacity());
        else //NECEdition.getDefault == NECEdition.NEC2017 ||  NECEdition.NEC2020
            assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(0.25);
        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0, cable.getCorrectedAndAdjustedAmpacity());
        else //NECEdition.getDefault == NECEdition.NEC2017 ||  NECEdition.NEC2020
            assertEquals(285 * 0, cable.getCorrectedAndAdjustedAmpacity());

        cable.resetRoofTopCondition();
        assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());
    }

    //NEC Edition independent
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity01(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300);
        Conduit conduit = new Conduit(95).setType(Type.ENT).setNonNipple();
        conduit.add(cable);
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        assertThrows(IllegalArgumentException.class, () -> cable.setRoofTopDistance(20));
        assertFalse(cable.isRoofTopCondition());
        assertEquals(TempRating.T75, cable.getTemperatureRating());
        assertEquals(7, conduit.getCurrentCarryingCount());
        //this ignores the rooftop condition since the conduit is not in that condition.
        assertEquals(285 * 0.94 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.setRooftopDistance(10);
        if(NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.67 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //NEC2017 or NEC2020
            assertEquals(285 * 0.94 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);


        conduit.setRooftopDistance(-1);
        assertEquals(285 * 0.94 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    //NEC Edition independent
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity02(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300)
                .setRoofTopDistance(20);
        cable.setAmbientTemperatureF(95);
        if(NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.75 * 1.0, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //NEC2017 and NEC2020
            assertEquals(285 * 0.94 * 1.0, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        Conduit conduit = new Conduit(95).setType(Type.ENT).setNonNipple();
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        conduit.add(new Conductor());
        assertEquals(5, conduit.getCurrentCarryingCount());

        conduit.add(cable);
        assertEquals(7, conduit.getCurrentCarryingCount());
    }

    //NEC Edition independent
    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity03(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300)
                .setRoofTopDistance(20);
        cable.setAmbientTemperatureF(95);
        Conduit conduit = new Conduit(95)
                .setType(Type.ENT)
                .setNonNipple()
                .setMinimumTradeSize(TradeSize.T1$2)
                .setRooftopDistance(20)
                .add(new Conductor())
                .add(new Conductor())
                .add(new Conductor())
                .add(new Conductor())
                .add(cable);

        assertEquals(6, conduit.getCurrentCarryingCount());
        if(NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.75 * 0.8, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //NEC2017 and NEC2020
            assertEquals(285 * 0.94 * 0.8, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.setNipple();//nipple does not affect cables because it's in a conduit with others conductors.
        if(NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(285 * 0.75 * 0.8, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //NEC2017 and NEC2020
            assertEquals(285 * 0.94 * 0.8, cable.getCorrectedAndAdjustedAmpacity(), 0.01);


        cable.setMetalForPhaseAndNeutral(ConductiveMaterial.ALUMINUM);
        cable.setInsulation(Insulation.THHN);
        assertEquals(6, conduit.getCurrentCarryingCount());

        conduit.setRooftopDistance(-1);
        assertEquals(260 * 0.96 * 0.8, cable.getCorrectedAndAdjustedAmpacity());

        Conduitable fifthConductor = new Conductor();
        conduit.add(fifthConductor);
        conduit.setNonNipple();
        assertEquals(260 * 0.96 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity04(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300)
                .setType(CableType.MC)
                .setMetalForPhaseAndNeutral(ConductiveMaterial.ALUMINUM)
                .setInsulation(Insulation.THHN).
                setAmbientTemperatureF(95);
        assertEquals(260 * 0.96 * 1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setPhaseConductorSize(Size.AWG_12);
        cable.setMetalForPhaseAndNeutral(ConductiveMaterial.COPPER);
        assertEquals(30 * 0.96 * 1.0, cable.getCorrectedAndAdjustedAmpacity());

        cable.setType(CableType.NM).setAmbientTemperatureF(65);
        cable.setInsulation(Insulation.RHW);
        cable.setJacketed();
        assertEquals(25 * 1.11 * 1.0, cable.getCorrectedAndAdjustedAmpacity());
        assertTrue(cable.isJacketed());

        cable.setType(CableType.AC);
        cable.setJacketed();
        assertTrue(cable.isJacketed());
    }

    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity05(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setType(CableType.AC)
                .setInsulation(Insulation.RHW)
                .setJacketed()
                .setAmbientTemperatureF(65);
        Conduit conduit = new Conduit(65).setType(Type.ENT).setNonNipple();
        conduit.add(cable);
        assertEquals(2, conduit.getCurrentCarryingCount());
        assertEquals(25 * 1.11 * 1.0, cable.getCorrectedAndAdjustedAmpacity());

        conduit.add(cable.copy());
        assertEquals(4, conduit.getCurrentCarryingCount());
        assertEquals(25 * 1.11 * 0.8, cable.getCorrectedAndAdjustedAmpacity());

        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        conduit.add(cable.copy());
        assertEquals(22, conduit.getCurrentCarryingCount());
        assertEquals(25 * 1.11 * 0.45, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAmpacity06(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setType(CableType.AC)
                .setInsulation(Insulation.RHW)
                .setJacketed()
                .setAmbientTemperatureF(65);
        Conduit conduit = new Conduit(65).setType(Type.ENT).setNonNipple();
        assertEquals(0, conduit.getCurrentCarryingCount());
        assertEquals(25 * 1.11 * 1.0, cable.getCorrectedAndAdjustedAmpacity());

        Bundle bundle = new Bundle(65);
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.setBundlingLength(10);
        assertEquals(10, bundle.getCurrentCarryingCount());

        bundle.add(cable);
        assertEquals(12, bundle.getCurrentCarryingCount());
        assertTrue(cable.isJacketed());
        assertTrue(((Cable) bundle.getConduitables().get(4)).isJacketed());
        assertEquals(65, cable.getAmbientTemperatureF());
        assertEquals(65, bundle.getConduitables().get(4).getAmbientTemperatureF());
        assertEquals(1.11, Factors.getTemperatureCorrectionF(cable.getAmbientTemperatureF(), cable.getTemperatureRating()));
        assertEquals(1, cable.getAdjustmentFactor());
        assertEquals(25 * 1.11 * 1, cable.getCorrectedAndAdjustedAmpacity());

        bundle.getConduitables().forEach(conduitable -> ((Cable) conduitable).setJacketed());
        assertTrue(cable.isJacketed());
        assertEquals(Size.AWG_12, cable.getPhaseConductor().getSize());
        assertEquals(ConductiveMaterial.COPPER, cable.getMetal());
        assertEquals(2, cable.getCurrentCarryingCount());
        assertEquals(12, bundle.getCurrentCarryingCount());
        assertEquals(25 * 1.11 * 1, cable.getCorrectedAndAdjustedAmpacity());
        bundle.setBundlingLength(25);

        assertEquals(25 * 1.11 * 0.5, cable.getCorrectedAndAdjustedAmpacity());


        bundle.getConduitables().forEach(conduitable -> ((Cable) conduitable).setNonJacketed());
        assertEquals(25 * 1.11 * 1, cable.getCorrectedAndAdjustedAmpacity());

        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        assertEquals(22, bundle.getCurrentCarryingCount());

        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(25 * 1.11 * 0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //(NECEdition.getDefault() == NECEdition.NEC2017 || NEC2020)
            assertEquals(25 * 1.11 * 0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setType(CableType.MC);
        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(25 * 1.11 * 0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //(NECEdition.getDefault() == NECEdition.NEC2017 || NEC2020)
            assertEquals(25 * 1.11 * 0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);


        cable.setType(CableType.NMS);
        assertEquals(25 * 1.11 * 0.45, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @ParameterizedTest
    @EnumSource(value = NECEdition.class)
    void getAmpacity07(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v480_1ph_3w)
                .setOuterDiameter(1)
                .setType(CableType.MC)
                .setInsulation(Insulation.THHN)
                .setAmbientTemperatureF(86);
        Bundle bundle = new Bundle(86).setBundlingLength(25);
        assertEquals(86, cable.getAmbientTemperatureF());
        assertEquals(3, cable.getCurrentCarryingCount());

        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable);
        assertEquals(21, bundle.getCurrentCarryingCount());
        assertEquals(86, cable.getAmbientTemperatureF());
        assertEquals(1, Factors.getTemperatureCorrectionF(cable.getAmbientTemperatureF(), cable.getTemperatureRating()));

        if (NECEdition.getDefault() == NECEdition.NEC2014)
            assertEquals(30 * 1 * 0.60, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        else //(NECEdition.getDefault() == NECEdition.NEC2017 || NEC2020)
            assertEquals(30 * 1 * 0.60, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @ParameterizedTest
    @EnumSource(NECEdition.class)
    void getAmpacity08(NECEdition necEdition) {
        NECEdition.setDefault(necEdition);
        Cable cable = new Cable(VoltageAC.v480_1ph_3w)
                .setOuterDiameter(1)
                .setType(CableType.MC)
                .setInsulation(Insulation.THHN)
                .setAmbientTemperatureF(86);
        Bundle bundle = new Bundle(86).setBundlingLength(25);
        assertEquals(30*1*1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        assertEquals(0, bundle.getCurrentCarryingCount());
    }

    @Test
    void setNeutralConductorSize() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(1);
        //default conductor size is #12
        assertEquals(Size.AWG_12, cable.getGroundingConductor().getSize());
        //the size of the neutral can be set independently, but since it's a
        // 1Φ with hot and neutral, both neutral and phase are set
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.AWG_12, cable.getGroundingConductor().getSize());
        assertEquals(Size.KCMIL_300, cable.getPhaseConductor().getSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductor().getSize());
        //the size of the phase can be set independently, but since it's a
        // 1Φ with hot and neutral, both neutral and phase are set
        cable.setPhaseConductorSize(Size.KCMIL_700);
        assertEquals(Size.AWG_12, cable.getGroundingConductor().getSize());
        assertEquals(Size.KCMIL_700, cable.getPhaseConductor().getSize());
        assertEquals(Size.KCMIL_700, cable.getNeutralConductor().getSize());
        //the size of the ground can be set independently
        cable.setGroundingConductorSize(Size.KCMIL_900);
        assertEquals(Size.KCMIL_900, cable.getGroundingConductor().getSize());
        assertEquals(Size.KCMIL_700, cable.getPhaseConductor().getSize());
        assertEquals(Size.KCMIL_700, cable.getNeutralConductor().getSize());
    }

    @Test
    void setNeutralConductorSize_01(){
        Cable cable = new Cable(VoltageAC.v480_3ph_3w).setOuterDiameter(1);
        //this cable does not have neutral.
        assertThrows (IllegalStateException.class, () -> cable.getNeutralConductor());
    }

    @Test
    void getPhaseConductorSize() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(1);
        cable.setPhaseConductorSize(Size.KCMIL_250);
        assertEquals(Size.KCMIL_250, cable.getPhaseConductor().getSize());
        assertEquals(Size.KCMIL_250, cable.getNeutralConductor().getSize());

        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.KCMIL_300, cable.getNeutralConductor().getSize());
        assertEquals(Size.KCMIL_300, cable.getPhaseConductor().getSize());
    }

    @Test
    void testClone() {
        Cable cable1 = new Cable(VoltageAC.v208_3ph_3w).setOuterDiameter(1.5);
        Conduit conduit = new Conduit(86).setType(Type.EMT).setNipple();
        conduit.add(cable1);
        cable1.setType(CableType.NMS);
        cable1.setJacketed();
        cable1.setPhaseConductorSize(Size.AWG_3$0);
        cable1.setGroundingConductorSize(Size.AWG_2$0);
//        cable1.setCopperCoating(Coating.COATED);
        cable1.setType(CableType.NMS);
        cable1.setLength(123);
        conduit.setRooftopDistance(20);
        Cable cable2 = cable1.copy();
        assertEquals(cable1.getPhaseConductor().getSize(), cable2.getPhaseConductor().getSize());
        assertEquals(cable1.hasNeutral()? cable1.getNeutralConductor().getSize(): null,
                     cable2.hasNeutral()? cable2.getNeutralConductor().getSize(): null);
        assertEquals(cable1.getGroundingConductor().getSize(), cable2.getGroundingConductor().getSize());
        assertEquals(cable1.getMetal(), cable2.getMetal());
        assertEquals(cable1.getInsulation(), cable2.getInsulation());
        assertEquals(cable1.getLength(), cable2.getLength());
        assertEquals(cable1.getAmbientTemperatureF(), cable2.getAmbientTemperatureF());
//        assertEquals(cable1.getCopperCoating(), cable2.getCopperCoating());
        assertFalse(cable1.hasNeutral());
        assertFalse(cable2.hasNeutral());
        assertThrows(IllegalArgumentException.class, cable1::isNeutralCurrentCarrying);
        assertThrows(IllegalArgumentException.class, cable2::isNeutralCurrentCarrying);
        assertEquals(cable1.isJacketed(), cable2.isJacketed());
        assertEquals(cable1.getOuterDiameter(), cable2.getOuterDiameter());
        assertEquals(cable1.getRooftopDistance(), cable2.getRooftopDistance());
        assertEquals(cable1.getType(), cable2.getType());
        assertEquals(cable1.getVoltageSystemAC(), cable2.getVoltageSystemAC());
        assertNotEquals(cable1.hasConduit(), cable2.hasConduit());
        assertEquals(cable1.hasBundle(), cable2.hasBundle());
    }

    @Test
    void bundleAndConduit() {
        Cable cable1 = new Cable(VoltageAC.v120_1ph_2w);
        Bundle bundle1 = new Bundle(86);//new Bundle(cable1, 5, 60);
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.setBundlingLength(60);
        assertEquals(5, bundle1.getConduitables().size());
        assertFalse(bundle1.getConduitables().contains(cable1));

        bundle1.add(cable1);
        assertEquals(6, bundle1.getConduitables().size());
        assertTrue(bundle1.getConduitables().contains(cable1));
    }

    @Test
    void bundleAndConduit_01() {
        Cable cable1 = new Cable(VoltageAC.v120_1ph_2w);
        Bundle bundle1 = new Bundle(86);
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.add(cable1.copy());
        bundle1.setBundlingLength(60);
        assertFalse(bundle1.hasConduitable(cable1));
        assertEquals(5, bundle1.getConduitables().size());
    }

    @Test
    void bundleAndConduit_02() {
        Cable cable1 = new Cable(VoltageAC.v120_1ph_2w);
        Bundle bundle1 = new Bundle(86);
        Cable cable2 = new Cable(VoltageAC.v480_3ph_3w).setOuterDiameter(5);
        Bundle bundle2 = new Bundle(86);
        bundle2.add(cable2.copy());
        bundle2.add(cable2.copy());
        bundle2.add(cable2.copy());
        assertEquals(3, bundle2.getConduitables().size());

        bundle1.add(cable2);
        assertEquals(1, bundle1.getConduitables().size());
        assertTrue(bundle1.getConduitables().contains(cable2));
        assertFalse(bundle2.getConduitables().contains(cable2));
        assertTrue(bundle1.hasConduitable(cable2));
        assertEquals(3, bundle2.getConduitables().size());

        bundle2.add(cable1);
        assertEquals(1, bundle1.getConduitables().size());
        assertEquals(4, bundle2.getConduitables().size());
        assertTrue(bundle2.getConduitables().contains(cable1));
        assertFalse(bundle1.getConduitables().contains(cable1));
        assertTrue(bundle2.hasConduitable(cable1));

        Conduit conduit1 = new Conduit(86).setType(Type.PVC40).setNonNipple();
        //moving cable1 to conduit1
        assertThrows(IllegalArgumentException.class, () -> conduit1.add(cable1));
        assertFalse(conduit1.getConduitables().contains(cable1));
        assertTrue(bundle2.getConduitables().contains(cable1));
        assertFalse(cable1.hasConduit());
        assertTrue(cable1.hasBundle());
        assertTrue(bundle2.hasConduitable(cable1));
    }

    @Test
    void setNeutralCarryingConductor01(){
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        assertEquals(3, cable.getCurrentCarryingCount());
        cable.setNeutralAsCurrentCarrying();
        assertEquals(4, cable.getCurrentCarryingCount());
        assertTrue(cable.isNeutralCurrentCarrying());
        cable.setNeutralAsNonCurrentCarrying();
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    @Test
    void setNeutralCarryingConductor02(){
        Cable cable = new Cable(VoltageAC.v208_3ph_3w);
        assertEquals(3, cable.getCurrentCarryingCount());
        assertEquals(3, cable.getCurrentCarryingCount());
        assertThrows(IllegalArgumentException.class, cable::setNeutralAsCurrentCarrying);
        assertThrows(IllegalArgumentException.class, cable::setNeutralAsNonCurrentCarrying);
        assertThrows(IllegalArgumentException.class, cable::isNeutralCurrentCarrying);
    }

    @Test
    void errorMessages(){
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        cable.setPhaseConductorSize(Size.AWG_4$0);
        cable.setNeutralConductorSize(Size.AWG_10);
        cable.setGroundingConductorSize(Size.AWG_12);
        cable.setMetalForPhaseAndNeutral(ConductiveMaterial.ALUMINUM);
        cable.setInsulation(Insulation.TW);
        assertThrows(IllegalArgumentException.class, () -> cable.setLength(-1));

        cable.setLength(123);
        assertThrows(IllegalArgumentException.class, () ->cable.setAmbientTemperatureF(-77));

        cable.setAmbientTemperatureF(100).setType(CableType.NM);
        assertNotEquals("", cable.getDescription());
        assertEquals(0.82, cable.getCompoundFactor());
        assertEquals(0.91, cable.getCompoundFactor(TempRating.T90));
        assertEquals(0.82, cable.getCompoundFactor(TempRating.T60));
        assertEquals(0.88, cable.getCompoundFactor(TempRating.T75));
        assertFalse(cable.isNeutralCurrentCarrying());
        assertEquals(Size.AWG_4$0, cable.getSize());
        assertEquals(Size.AWG_4$0, cable.getPhaseConductor().getSize());
        assertEquals(Size.AWG_10, cable.getNeutralConductor().getSize());
        assertEquals(Size.AWG_12, cable.getGroundingConductor().getSize());

        Cable cable2 = new Cable(VoltageAC.v208_3ph_3w);
        assertThrows(IllegalArgumentException.class, cable2::setNeutralAsCurrentCarrying);
        assertThrows(IllegalArgumentException.class, cable2::setNeutralAsNonCurrentCarrying);
        assertThrows(IllegalArgumentException.class, ()-> cable2.setNeutralConductorSize(Size.AWG_12));

        Conduit conduit = new Conduit(86);
        cable2.setAmbientTemperatureF(100);
        assertEquals(100, cable2.getAmbientTemperatureF());

        conduit.add(cable2);
        assertEquals(86, cable2.getAmbientTemperatureF());
        assertThrows(IllegalArgumentException.class, ()-> cable2.setAmbientTemperatureF(100));

        Cable cable3 = new Cable(VoltageAC.v208_3ph_3w);
        Bundle bundle = new Bundle(86);
        cable3.setAmbientTemperatureF(100);
        assertEquals(100, cable3.getAmbientTemperatureF());

        bundle.add(cable3);
        assertEquals(86, cable3.getAmbientTemperatureF());
        assertThrows(IllegalArgumentException.class, ()-> cable3.setAmbientTemperatureF(100));
        assertNotEquals("", cable.toJSON());

        cable.setMetalForPhaseAndNeutral(ConductiveMaterial.COPPER);
        cable.setMetalForGrounding(ConductiveMaterial.ALUMINUM);
        assertEquals(ConductiveMaterial.COPPER,cable.getMetalForPhaseAndNeutral());
        assertEquals(ConductiveMaterial.ALUMINUM,cable.getMetalForGrounding());
    }

    @Test //that we cannot set the conduit of a cable by calling setConduit() from a different class other than Conduit.
    void test_setConduit() {
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        Conduit conduit = new Conduit(86);
        assertThrows(IllegalCallerException.class, () -> cable.setConduit(conduit));
        conduit.add(cable);
        assertThrows(IllegalCallerException.class, () -> cable.setConduit(conduit));
    }

    @Test //that we cannot set the bundle of a cable by calling setBundle() from a different class other than Bundle.
    void test_setBundle() {
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        Bundle bundle = new Bundle(86);
        assertThrows(IllegalCallerException.class, () -> cable.setBundle(bundle));
        bundle.add(cable);
        assertThrows(IllegalCallerException.class, () -> cable.setBundle(bundle));
    }
}