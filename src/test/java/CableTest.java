package test.java;

import eecalcs.conductors.*;
import eecalcs.conductors.raceways.Bundle;
import eecalcs.conductors.raceways.Cable;
import eecalcs.conductors.raceways.Conductor;
import eecalcs.conductors.raceways.Conduit;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageAC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CableTest {

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
        assertEquals(0.25*0.25*0.25*Math.PI, cable.getInsulatedAreaIn2());
    }

    @Test
    void getCurrentCarryingCount() {
        Cable cable2 = new Cable(VoltageAC.v120_1ph_2w);
        assertEquals(2, cable2.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_00() {
        Cable cable = new Cable(VoltageAC.v480_3ph_4w).setOuterDiameter(1);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor();
        assertEquals(4, cable.getCurrentCarryingCount());

        cable.setNeutralNonCarryingConductor();
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_01() {
        Cable cable = new Cable(VoltageAC.v240_3ph_3w);
        assertThrows(IllegalArgumentException.class, cable::setNeutralCarryingConductor);
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_02() {
        Cable cable = new Cable(VoltageAC.v208_3ph_4w);
        cable.setNeutralCarryingConductor();
        assertEquals(4, cable.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_03() {
        Cable cable = new Cable(VoltageAC.v240_1ph_2w);
        assertThrows(IllegalArgumentException.class, cable::setNeutralCarryingConductor);
        assertEquals(2, cable.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_04() {
        Cable cable = new Cable(VoltageAC.v240_1ph_3w);
        cable.setNeutralCarryingConductor();
        assertEquals(3, cable.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_05() {
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        cable.setNeutralCarryingConductor();
        assertEquals(2, cable.getCurrentCarryingCount());
    }

    @Test
    void getCurrentCarryingCount_06() {
        Cable cable = new Cable(VoltageAC.v240_3ph_4w);
        cable.setNeutralCarryingConductor();
        assertEquals(4, cable.getCurrentCarryingCount());
    }

    @Test
    void roofTopDistanceAndCondition_01(){
        Cable cable = new Cable(VoltageAC.v120_1ph_2w);
        assertEquals(-1, cable.getRooftopDistance());
        assertFalse(cable.isRoofTopCondition());

        cable.setRoofTopDistance(5);
        assertEquals(5, cable.getRooftopDistance());
        assertTrue(cable.isRoofTopCondition());

        cable.setRoofTopDistance(36);
        assertEquals(36, cable.getRooftopDistance());
        assertTrue(cable.isRoofTopCondition());

        cable.setRoofTopDistance(36.1);
        assertEquals(36.1, cable.getRooftopDistance());
        assertFalse(cable.isRoofTopCondition());

        cable.resetRoofTopCondition();
        assertEquals(-1, cable.getRooftopDistance());
    }

    @Test
    void roofTopDistanceAndCondition_02(){
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
        conduit.setRoofTopDistance(5);
        assertTrue(cable.isRoofTopCondition());
        assertTrue(conduit.isRoofTopCondition());

        /*the cable rooftop condition cannot be reset if the cable belongs to
         a conduit. Rooftop condition of both conduit and cable remains
         unchanged.*/
        assertThrows(IllegalArgumentException.class, cable::resetRoofTopCondition);
        assertTrue(cable.isRoofTopCondition());
        assertTrue(conduit.isRoofTopCondition());

        /*if a conduit rooftop condition is reset, so is the cable.*/
        conduit.resetRoofTopCondition();
        assertFalse(cable.isRoofTopCondition());
        assertFalse(conduit.isRoofTopCondition());

        /*if a cable rooftop condition is set, nothing happens to either the
         cable or the conduit, because rooftop condition of cable is the
         rooftop condition of its conduit.*/
        assertThrows(IllegalArgumentException.class, () -> cable.setRoofTopDistance(5));
        assertFalse(cable.isRoofTopCondition());
        assertFalse(conduit.isRoofTopCondition());
    }

    @Test
    void getAmpacity() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(1);
        cable.setPhaseConductorSize(Size.KCMIL_300);
        assertEquals(285, cable.getCorrectedAndAdjustedAmpacity());

        cable.setAmbientTemperatureF(100);
        assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(38);
        assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(36);
        assertEquals(285 * 0.67, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(6);
        assertEquals(285 * 0.67, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(2);
        assertEquals(285 * 0.58, cable.getCorrectedAndAdjustedAmpacity());

        cable.setRoofTopDistance(0.25);
        assertEquals(285 * 0, cable.getCorrectedAndAdjustedAmpacity());

        cable.resetRoofTopCondition();
        assertEquals(285 * 0.88, cable.getCorrectedAndAdjustedAmpacity());
    }

    @Test
    void getAmpacity01() {
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

        conduit.setRoofTopDistance(10);
        assertEquals(285 * 0.67 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.resetRoofTopCondition();
        assertEquals(285 * 0.94 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @Test
    void getAmpacity02() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300)
                .setRoofTopDistance(20)
                .setAmbientTemperatureF(95);
        assertEquals(285 * 0.75 * 1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

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

    @Test
    void getAmpacity03() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300)
                .setRoofTopDistance(20)
                .setAmbientTemperatureF(95);
        Conduit conduit = new Conduit(95).setType(Type.ENT).setNonNipple();
        conduit
                .setMinimumTrade(Trade.T1$2)
                .setRoofTopDistance(20)
                .add(new Conductor())
                .add(new Conductor())
                .add(new Conductor())
                .add(new Conductor())
                .add(cable);
        Conduitable fithConductor = new Conductor();
        assertEquals(6, conduit.getCurrentCarryingCount());
        assertEquals(285 * 0.75 * 0.8, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        conduit.setNipple();
        assertEquals(285 * 0.75 * 1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setMetal(Metal.ALUMINUM);
        cable.setInsulation(Insul.THHN);
        assertEquals(6, conduit.getCurrentCarryingCount());

        conduit.resetRoofTopCondition();
        assertEquals(260 * 0.96 * 1, cable.getCorrectedAndAdjustedAmpacity());

        conduit.add(fithConductor);
        conduit.setNonNipple();
        assertEquals(260 * 0.96 * 0.7, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @Test
    void getAmpacity04() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setPhaseConductorSize(Size.KCMIL_300)
                .setAmbientTemperatureF(95)
                .setType(CableType.MC)
                .setMetal(Metal.ALUMINUM)
                .setInsulation(Insul.THHN);
        assertEquals(260 * 0.96 * 1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setPhaseConductorSize(Size.AWG_12);
        cable.setMetal(Metal.COPPER);
        assertEquals(30 * 0.96 * 1.0, cable.getCorrectedAndAdjustedAmpacity());

        cable.setType(CableType.NM);
        cable.setAmbientTemperatureF(65);
        cable.setInsulation(Insul.RHW);
        cable.setJacketed();
        assertEquals(25 * 1.11 * 1.0, cable.getCorrectedAndAdjustedAmpacity());
        assertTrue(cable.isJacketed());

        cable.setType(CableType.AC);
        cable.setJacketed();
        assertTrue(cable.isJacketed());
    }

    @Test
    void getAmpacity05() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setAmbientTemperatureF(65)
                .setType(CableType.AC)
                .setInsulation(Insul.RHW)
                .setJacketed();
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

    @Test
    void getAmpacity06() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w)
                .setOuterDiameter(1)
                .setAmbientTemperatureF(65)
                .setType(CableType.AC)
                .setInsulation(Insul.RHW)
                .setJacketed();
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
        assertEquals(1, Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount(), bundle.getBundlingLength()));
        assertEquals(1.11, Factors.getTemperatureCorrectionF(cable.getAmbientTemperatureF(), cable.getTemperatureRating()));
        assertEquals(1, cable.getAdjustmentFactor());
        assertEquals(25 * 1.11 * 1, cable.getCorrectedAndAdjustedAmpacity());

        bundle.getConduitables().forEach(conduitable -> ((Cable) conduitable).setJacketed());
        assertTrue(cable.isJacketed());
        assertEquals(Size.AWG_12, cable.getPhaseConductorSize());
        assertEquals(Metal.COPPER, cable.getMetal());
        assertEquals(2, cable.getCurrentCarryingCount());
        assertEquals(12, bundle.getCurrentCarryingCount());
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(25 * 1.11 * 1, cable.getCorrectedAndAdjustedAmpacity());
        bundle.setBundlingLength(25);
        assertEquals(25 * 1.11 * 0.5, cable.getCorrectedAndAdjustedAmpacity());


        bundle.getConduitables().forEach(conduitable -> ((Cable) conduitable).setNonJacketed());
        assertTrue(bundle.compliesWith310_15_B_3_a_4());
        assertEquals(25 * 1.11 * 1, cable.getCorrectedAndAdjustedAmpacity());

        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        bundle.add(cable.copy());
        assertEquals(22, bundle.getCurrentCarryingCount());
        assertFalse(bundle.compliesWith310_15_B_3_a_4());
        assertTrue(bundle.compliesWith310_15_B_3_a_5());
        assertEquals(25 * 1.11 * 0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setType(CableType.MC);
        assertEquals(25 * 1.11 * 0.6, cable.getCorrectedAndAdjustedAmpacity(), 0.01);

        cable.setType(CableType.NMS);
        assertEquals(25 * 1.11 * 0.45, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @Test
    void getAmpacity07() {
        Cable cable = new Cable(VoltageAC.v480_1ph_3w)
                .setOuterDiameter(1)
                .setAmbientTemperatureF(86)
                .setType(CableType.MC)
                .setInsulation(Insul.THHN);
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
        assertTrue(bundle.compliesWith310_15_B_3_a_5());
        assertEquals(86, cable.getAmbientTemperatureF());
        assertEquals(1, Factors.getTemperatureCorrectionF(cable.getAmbientTemperatureF(), cable.getTemperatureRating()));
        assertEquals(30 * 1 * 0.60, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
    }

    @Test
    void getAmpacity08() {
        Cable cable = new Cable(VoltageAC.v480_1ph_3w)
                .setOuterDiameter(1)
                .setAmbientTemperatureF(86)
                .setType(CableType.MC)
                .setInsulation(Insul.THHN);
        Bundle bundle = new Bundle(86).setBundlingLength(25);
        assertEquals(30*1*1, cable.getCorrectedAndAdjustedAmpacity(), 0.01);
        assertEquals(0, bundle.getCurrentCarryingCount());
    }

    @Test
    void setNeutralConductorSize() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(1);
        //default conductor size is #12
        assertEquals(Size.AWG_12, cable.getGroundingConductorSize());
        //the size of the neutral can be set independently
        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.AWG_12, cable.getGroundingConductorSize());
        assertEquals(Size.AWG_12, cable.getPhaseConductorSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());
        //the size of the phase can be set independently
        cable.setPhaseConductorSize(Size.KCMIL_700);
        assertEquals(Size.AWG_12, cable.getGroundingConductorSize());
        assertEquals(Size.KCMIL_700, cable.getPhaseConductorSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());
        //the size of the ground can be set independently
        cable.setGroundingConductorSize(Size.KCMIL_900);
        assertEquals(Size.KCMIL_900, cable.getGroundingConductorSize());
        assertEquals(Size.KCMIL_700, cable.getPhaseConductorSize());
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());
    }

    @Test
    void setNeutralConductorSize_01(){
        Cable cable = new Cable(VoltageAC.v480_3ph_3w).setOuterDiameter(1);
        //this cable does not have neutral.
        assertNull(cable.getNeutralConductorSize());
    }

    @Test
    void getPhaseConductorSize() {
        Cable cable = new Cable(VoltageAC.v277_1ph_2w).setOuterDiameter(1);
        cable.setPhaseConductorSize(Size.KCMIL_250);
        assertEquals(Size.KCMIL_250, cable.getPhaseConductorSize());

        cable.setNeutralConductorSize(Size.KCMIL_300);
        assertEquals(Size.KCMIL_300, cable.getNeutralConductorSize());
        assertEquals(Size.KCMIL_250, cable.getPhaseConductorSize());
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
        cable1.setCopperCoating(Coating.COATED);
        cable1.setType(CableType.NMS);
        cable1.setLength(123);
        conduit.setRoofTopDistance(20);
        Cable cable2 = cable1.copy();
        assertEquals(cable1.getPhaseConductorSize(), cable2.getPhaseConductorSize());
        assertEquals(cable1.getNeutralConductorSize(), cable2.getNeutralConductorSize());
        assertEquals(cable1.getGroundingConductorSize(), cable2.getGroundingConductorSize());
        assertEquals(cable1.getMetal(), cable2.getMetal());
        assertEquals(cable1.getInsulation(), cable2.getInsulation());
        assertEquals(cable1.getLength(), cable2.getLength());
        assertEquals(cable1.getAmbientTemperatureF(), cable2.getAmbientTemperatureF());
        assertEquals(cable1.getCopperCoating(), cable2.getCopperCoating());
        assertEquals(cable1.isNeutralCarryingConductor(), cable2.isNeutralCarryingConductor());
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
    void setNeutralCarryingConductor(){
        Cable cable = new Cable(VoltageAC.v480_3ph_4w);
        assertEquals(3, cable.getCurrentCarryingCount());

        cable.setNeutralCarryingConductor();
        assertEquals(4, cable.getCurrentCarryingCount());

        cable.setNeutralNonCarryingConductor();
        assertEquals(3, cable.getCurrentCarryingCount());

    }

}