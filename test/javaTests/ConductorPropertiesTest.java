package javaTests;

import eecalcs.conductors.*;
import eecalcs.conduits.OuterMaterial;
import eecalcs.conductors.TempRating;
import eecalcs.systems.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorPropertiesTest {

    @Test
    void getSizeByAmperes() {
        assertEquals(Size.AWG_1, ConductorProperties.getSizePerCurrent(144.23, ConductiveMetal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_1$0, ConductorProperties.getSizePerCurrent(144.23, ConductiveMetal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_3$0, ConductorProperties.getSizePerCurrent(144.23, ConductiveMetal.ALUMINUM, TempRating.T75));
    }

    @Test
    void hasInsulatedArea() {
        assertFalse(ConductorProperties.hasInsulatedAreaDefined(null, null));
    }


    @Test
    void getInsulatedAreaIn2() {
        assertFalse(ConductorProperties.hasInsulatedAreaDefined(null, null));
    }

    @Test
    void getCompactAreaIn2() {
        assertEquals(0.0, ConductorProperties.getCompactConductorAreaIn2(null, null),0.0001);
        assertEquals(0.0531, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insulation.RHH),0.0001);
        assertEquals(0.0, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_3, Insulation.RHH),0.0001);
    }

    @Test
    void getCompactBareAreaIn2() {
        assertEquals(0.0141026094219646, ConductorProperties.getBareCompactConductorAreaIn2(Size.AWG_8),0.0001);
        assertEquals(0.0, ConductorProperties.getBareCompactConductorAreaIn2(Size.AWG_3),0.0001);
    }

    @Test
    void hasCompactArea() {
        assertFalse(ConductorProperties.hasCompactAreaDefined(null, null));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.USE));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_8, Insulation.THHN));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insulation.THW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.KCMIL_2000, Insulation.XHHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_8, Insulation.XHHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.KCMIL_1000, Insulation.RHH));
    }

    @Test
    void hasCompactBareArea() {
        assertFalse(ConductorProperties.hasCompactAreaDefined(null, null));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.USE));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.KCMIL_750, Insulation.THW));
    }

    @Test
    void getTempRating() {
        //for dry locations
        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insulation.TW));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insulation.THW));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insulation.THHW));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insulation.XHHW));

        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insulation.TW, Location.DRY));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insulation.THW, Location.DRY));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insulation.THHW, Location.DRY));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insulation.XHHW, Location.DRY));
        //for wet locations
        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insulation.TW, Location.WET));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insulation.THW, Location.WET));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insulation.THHW, Location.WET));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insulation.XHHW, Location.WET));
        //for damp locations
        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insulation.TW, Location.DAMP));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insulation.THW, Location.DAMP));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insulation.THHW, Location.DAMP));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insulation.XHHW, Location.DAMP));
    }

    @Test
    void getReactance() {
        boolean Magnetic = true;
        boolean nonMagnetic = false;
        assertEquals(0.057000, ConductorProperties.getReactance(Size.AWG_1, Magnetic),0.0001);
        assertEquals(0.054, ConductorProperties.getReactance(Size.AWG_10, nonMagnetic), 0.0001);
    }

    @Test
    void getAreaCM() {
        assertEquals(4110, ConductorProperties.getAreaCM(Size.AWG_14));
        assertEquals(2000000, ConductorProperties.getAreaCM(Size.KCMIL_2000));
    }

    @Test
    void getDCResistance() {
        assertEquals(3.19, ConductorProperties.getDCResistance(Size.AWG_14, ConductiveMetal.COPPERCOATED/*, Coating.COATED*/),0.001);
        assertEquals(0.0106, ConductorProperties.getDCResistance(Size.KCMIL_2000, ConductiveMetal.ALUMINUM/*, Coating.COATED*/),0.0001);

        assertEquals(0.0106*100*0.001/3, ConductorProperties.getDCResistance(Size.KCMIL_2000, ConductiveMetal.ALUMINUM, 100, 3/*, Coating.COATED*/), 0.0001);
    }

    @Test
    void getACResistance() {
        assertEquals(3.1, ConductorProperties.getACResistance(Size.AWG_14, ConductiveMetal.COPPER, OuterMaterial.PVC),0.001);
        assertEquals(0.0166, ConductorProperties.getACResistance(Size.KCMIL_2000, ConductiveMetal.ALUMINUM, OuterMaterial.STEEL),0.0001);
    }

    @Test
    void getAmpacity() {
        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.COPPER, TempRating.T60));
        assertEquals(630, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.ALUMINUM, TempRating.T90));
    }


    @Test
    void getSizePerArea() {
        assertNull(ConductorProperties.getSizePerArea(2000001));

        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerArea(2000000));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerArea(1999999));

        assertEquals(Size.AWG_4$0, ConductorProperties.getSizePerArea(211600));
        assertEquals(Size.AWG_4$0, ConductorProperties.getSizePerArea(211600-1));
        assertEquals(Size.KCMIL_250, ConductorProperties.getSizePerArea(211600+1));
        assertEquals(Size.AWG_4$0, ConductorProperties.getSizePerArea(200000));

        assertEquals(Size.AWG_14, ConductorProperties.getSizePerArea(4110));
        assertEquals(Size.AWG_14, ConductorProperties.getSizePerArea(4110-1));
        assertEquals(Size.AWG_14, ConductorProperties.getSizePerArea(4110-100));
    }

    @Test
    void getInsulatedConductorAreaIn2() {
        assertEquals(0.0293, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insulation.RHH));
        assertEquals(3.3719, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insulation.RHW2));

        assertEquals(0.0139, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insulation.TW));
        assertEquals(2.7818, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insulation.THW2));

        assertEquals(0.0139, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insulation.XHHW));
        assertEquals(2.6073, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insulation.XHHW2));

        assertEquals(0.0139, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insulation.ZW));
        assertEquals(0.1146, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_2, Insulation.ZW));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_1, Insulation.ZW));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insulation.ZW));

        assertEquals(0.0097, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insulation.THWN));
        assertEquals(1.3478, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_1000, Insulation.THHN));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_1250, Insulation.THHN));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insulation.THHN));

        assertEquals(0.0100, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insulation.FEP));
        assertEquals(0.0973, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_2, Insulation.FEPB));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_1, Insulation.FEP));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insulation.FEPB));
    }

    @Test
    void getCompactConductorAreaIn2() {
        assertEquals(0.0531, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insulation.RHH));
        assertEquals(1.2968, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insulation.RHH));

        assertEquals(0.0510, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insulation.THW));
        assertEquals(0.0510, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insulation.THHW));
        assertEquals(1.2968, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insulation.THW));
        assertEquals(1.2968, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insulation.THHW));

        assertEquals(0.0, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insulation.THHN));
        assertEquals(0.0452, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_6, Insulation.THHN));
        assertEquals(1.2370, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insulation.THHN));

        assertEquals(0.0394, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insulation.XHHW));
        assertEquals(1.1882, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insulation.XHHW));
    }

    @Test
    void getBareCompactConductorAreaIn2() {
        assertEquals(0.0141, ConductorProperties.getBareCompactConductorAreaIn2(Size.AWG_8));
        assertEquals(0.8825, ConductorProperties.getBareCompactConductorAreaIn2(Size.KCMIL_1000));
    }

    @Test
    void hasInsulatedAreaDefined() {
        assertTrue(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_2, Insulation.FEP));
        assertTrue(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_2, Insulation.ZW));
        assertTrue(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_14, Insulation.ZW));
        assertFalse(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_14, Insulation.USE));
        assertFalse(ConductorProperties.hasInsulatedAreaDefined(Size.KCMIL_2000, Insulation.ZW));
    }

    @Test
    void hasCompactAreaDefined() {
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insulation.RHH));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insulation.THW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insulation.THHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insulation.THHN));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insulation.XHHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insulation.RHH));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insulation.THW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insulation.THHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insulation.THHN));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insulation.XHHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.RHH));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.RHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.USE));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.THW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.THHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.THHN));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insulation.XHHW));

    }


    @Test
    void hasCompactBareAreaDefined() {
        assertFalse(ConductorProperties.hasCompactBareAreaDefined(Size.AWG_3));
        assertFalse(ConductorProperties.hasCompactBareAreaDefined(Size.AWG_10));
        assertFalse(ConductorProperties.hasCompactBareAreaDefined(Size.KCMIL_800));
        assertFalse(ConductorProperties.hasCompactBareAreaDefined(Size.KCMIL_1250));

        assertTrue(ConductorProperties.hasCompactBareAreaDefined(Size.AWG_8));
        assertTrue(ConductorProperties.hasCompactBareAreaDefined(Size.AWG_4));
        assertTrue(ConductorProperties.hasCompactBareAreaDefined(Size.AWG_2));
        assertTrue(ConductorProperties.hasCompactBareAreaDefined(Size.KCMIL_750));
        assertTrue(ConductorProperties.hasCompactBareAreaDefined(Size.KCMIL_900));
        assertTrue(ConductorProperties.hasCompactBareAreaDefined(Size.KCMIL_1000));


    }

    @Test
    void getBiggestSize() {
        assertEquals(Size.AWG_12, ConductorProperties.getBiggestSize(Size.AWG_14, Size.AWG_12));
        assertEquals(Size.AWG_1, ConductorProperties.getBiggestSize(Size.AWG_1, Size.AWG_10));
        assertEquals(Size.AWG_1, ConductorProperties.getBiggestSize(Size.AWG_1, Size.AWG_1));
        assertEquals(Size.AWG_1$0, ConductorProperties.getBiggestSize(Size.AWG_1, Size.AWG_1$0));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getBiggestSize(Size.AWG_14, Size.KCMIL_2000));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getBiggestSize(Size.KCMIL_2000, Size.KCMIL_1750));
    }

    @Test
    void getStandardAmpacity() {
        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.COPPER, TempRating.T60));
        assertEquals(20, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.COPPER, TempRating.T75));
        assertEquals(25, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.COPPER, TempRating.T90));

        assertEquals(0, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.ALUMINUM, TempRating.T60));
        assertEquals(0, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.ALUMINUM, TempRating.T75));
        assertEquals(0, ConductorProperties.getStandardAmpacity(Size.AWG_14, ConductiveMetal.ALUMINUM, TempRating.T90));

        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_12, ConductiveMetal.ALUMINUM, TempRating.T60));
        assertEquals(20, ConductorProperties.getStandardAmpacity(Size.AWG_12, ConductiveMetal.ALUMINUM, TempRating.T75));
        assertEquals(25, ConductorProperties.getStandardAmpacity(Size.AWG_12, ConductiveMetal.ALUMINUM, TempRating.T90));

        assertEquals(555, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.COPPER, TempRating.T60));
        assertEquals(665, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.COPPER, TempRating.T75));
        assertEquals(750, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.COPPER, TempRating.T90));
        assertEquals(470, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.ALUMINUM, TempRating.T60));
        assertEquals(560, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.ALUMINUM, TempRating.T75));
        assertEquals(630, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, ConductiveMetal.ALUMINUM, TempRating.T90));
    }

    @Test
    void getSizePerCurrent() {
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(546, ConductiveMetal.COPPER, TempRating.T60));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(651, ConductiveMetal.COPPER, TempRating.T75));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(736, ConductiveMetal.COPPER, TempRating.T90));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(455.1, ConductiveMetal.ALUMINUM, TempRating.T60));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(546, ConductiveMetal.ALUMINUM, TempRating.T75));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(616, ConductiveMetal.ALUMINUM, TempRating.T90));

        assertEquals(Size.AWG_14, ConductorProperties.getSizePerCurrent(5, ConductiveMetal.COPPER, TempRating.T60));
        assertEquals(Size.AWG_14, ConductorProperties.getSizePerCurrent(19, ConductiveMetal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_12, ConductorProperties.getSizePerCurrent(26, ConductiveMetal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_4, ConductorProperties.getSizePerCurrent(41, ConductiveMetal.ALUMINUM, TempRating.T60));
        assertEquals(Size.KCMIL_250, ConductorProperties.getSizePerCurrent(181, ConductiveMetal.ALUMINUM, TempRating.T75));
        assertEquals(Size.KCMIL_900, ConductorProperties.getSizePerCurrent(450, ConductiveMetal.ALUMINUM, TempRating.T90));
    }
}