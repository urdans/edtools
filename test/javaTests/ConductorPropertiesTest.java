package javaTests;

import eecalcs.conductors.*;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.conductors.TempRating;
import eecalcs.systems.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorPropertiesTest {

    @Test
    void getSizeByAmperes() {
        assertEquals(Size.AWG_1, ConductorProperties.getSizePerCurrent(144.23, Metal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_1$0, ConductorProperties.getSizePerCurrent(144.23, Metal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_3$0, ConductorProperties.getSizePerCurrent(144.23, Metal.ALUMINUM, TempRating.T75));
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
        assertEquals(0.0531, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insul.RHH),0.0001);
        assertEquals(0.0, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_3, Insul.RHH),0.0001);
    }

    @Test
    void getCompactBareAreaIn2() {
        assertEquals(0.0141026094219646, ConductorProperties.getBareCompactConductorAreaIn2(Size.AWG_8),0.0001);
        assertEquals(0.0, ConductorProperties.getBareCompactConductorAreaIn2(Size.AWG_3),0.0001);
    }

    @Test
    void hasCompactArea() {
        assertFalse(ConductorProperties.hasCompactAreaDefined(null, null));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.USE));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_8, Insul.THHN));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insul.THW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.KCMIL_2000, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_8, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.KCMIL_1000, Insul.RHH));
    }

    @Test
    void hasCompactBareArea() {
        assertFalse(ConductorProperties.hasCompactAreaDefined(null, null));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.USE));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.KCMIL_750, Insul.THW));
    }

    @Test
    void getTempRating() {
        //for dry locations
        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insul.TW));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insul.THW));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insul.THHW));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insul.XHHW));

        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insul.TW, Location.DRY));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insul.THW, Location.DRY));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insul.THHW, Location.DRY));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insul.XHHW, Location.DRY));
        //for wet locations
        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insul.TW, Location.WET));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insul.THW, Location.WET));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insul.THHW, Location.WET));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insul.XHHW, Location.WET));
        //for damp locations
        assertEquals(TempRating.T60, ConductorProperties.getTempRating(Insul.TW, Location.DAMP));
        assertEquals(TempRating.T75, ConductorProperties.getTempRating(Insul.THW, Location.DAMP));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insul.THHW, Location.DAMP));
        assertEquals(TempRating.T90, ConductorProperties.getTempRating(Insul.XHHW, Location.DAMP));
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
        assertEquals(3.19, ConductorProperties.getDCResistance(Size.AWG_14, Metal.COPPERCOATED/*, Coating.COATED*/),0.001);
        assertEquals(0.0106, ConductorProperties.getDCResistance(Size.KCMIL_2000, Metal.ALUMINUM/*, Coating.COATED*/),0.0001);

        assertEquals(0.0106*100*0.001/3, ConductorProperties.getDCResistance(Size.KCMIL_2000, Metal.ALUMINUM, 100, 3/*, Coating.COATED*/), 0.0001);
    }

    @Test
    void getACResistance() {
        assertEquals(3.1, ConductorProperties.getACResistance(Size.AWG_14, Metal.COPPER, Material.PVC),0.001);
        assertEquals(0.0166, ConductorProperties.getACResistance(Size.KCMIL_2000, Metal.ALUMINUM, Material.STEEL),0.0001);
    }

    @Test
    void getAmpacity() {
        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.COPPER, TempRating.T60));
        assertEquals(630, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.ALUMINUM, TempRating.T90));
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
        assertEquals(0.0293, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insul.RHH));
        assertEquals(3.3719, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000,Insul.RHW2));

        assertEquals(0.0139, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insul.TW));
        assertEquals(2.7818, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insul.THW2));

        assertEquals(0.0139, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insul.XHHW));
        assertEquals(2.6073, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insul.XHHW2));

        assertEquals(0.0139, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insul.ZW));
        assertEquals(0.1146, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_2, Insul.ZW));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_1, Insul.ZW));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insul.ZW));

        assertEquals(0.0097, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insul.THWN));
        assertEquals(1.3478, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_1000, Insul.THHN));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_1250, Insul.THHN));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insul.THHN));

        assertEquals(0.0100, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_14, Insul.FEP));
        assertEquals(0.0973, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_2, Insul.FEPB));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.AWG_1, Insul.FEP));
        assertEquals(0.0, ConductorProperties.getInsulatedConductorAreaIn2(Size.KCMIL_2000, Insul.FEPB));
    }

    @Test
    void getCompactConductorAreaIn2() {
        assertEquals(0.0531, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insul.RHH));
        assertEquals(1.2968, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insul.RHH));

        assertEquals(0.0510, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insul.THW));
        assertEquals(0.0510, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insul.THHW));
        assertEquals(1.2968, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insul.THW));
        assertEquals(1.2968, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insul.THHW));

        assertEquals(0.0, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insul.THHN));
        assertEquals(0.0452, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_6, Insul.THHN));
        assertEquals(1.2370, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insul.THHN));

        assertEquals(0.0394, ConductorProperties.getCompactConductorAreaIn2(Size.AWG_8, Insul.XHHW));
        assertEquals(1.1882, ConductorProperties.getCompactConductorAreaIn2(Size.KCMIL_1000, Insul.XHHW));
    }

    @Test
    void getBareCompactConductorAreaIn2() {
        assertEquals(0.0141, ConductorProperties.getBareCompactConductorAreaIn2(Size.AWG_8));
        assertEquals(0.8825, ConductorProperties.getBareCompactConductorAreaIn2(Size.KCMIL_1000));
    }

    @Test
    void hasInsulatedAreaDefined() {
        assertTrue(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_2, Insul.FEP));
        assertTrue(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_2, Insul.ZW));
        assertTrue(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_14, Insul.ZW));
        assertFalse(ConductorProperties.hasInsulatedAreaDefined(Size.AWG_14, Insul.USE));
        assertFalse(ConductorProperties.hasInsulatedAreaDefined(Size.KCMIL_2000, Insul.ZW));
    }

    @Test
    void hasCompactAreaDefined() {
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insul.RHH));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insul.THW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insul.THHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insul.THHN));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_10, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insul.RHH));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insul.THW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insul.THHW));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insul.THHN));
        assertTrue(ConductorProperties.hasCompactAreaDefined(Size.AWG_1, Insul.XHHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.RHH));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.RHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.USE));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.THW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.THHW));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.THHN));
        assertFalse(ConductorProperties.hasCompactAreaDefined(Size.AWG_3, Insul.XHHW));

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
        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.COPPER, TempRating.T60));
        assertEquals(20, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.COPPER, TempRating.T75));
        assertEquals(25, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.COPPER, TempRating.T90));

        assertEquals(0, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.ALUMINUM, TempRating.T60));
        assertEquals(0, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.ALUMINUM, TempRating.T75));
        assertEquals(0, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.ALUMINUM, TempRating.T90));

        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_12, Metal.ALUMINUM, TempRating.T60));
        assertEquals(20, ConductorProperties.getStandardAmpacity(Size.AWG_12, Metal.ALUMINUM, TempRating.T75));
        assertEquals(25, ConductorProperties.getStandardAmpacity(Size.AWG_12, Metal.ALUMINUM, TempRating.T90));

        assertEquals(555, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.COPPER, TempRating.T60));
        assertEquals(665, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.COPPER, TempRating.T75));
        assertEquals(750, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.COPPER, TempRating.T90));
        assertEquals(470, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.ALUMINUM, TempRating.T60));
        assertEquals(560, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.ALUMINUM, TempRating.T75));
        assertEquals(630, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.ALUMINUM, TempRating.T90));
    }

    @Test
    void getSizePerCurrent() {
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(546, Metal.COPPER, TempRating.T60));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(651, Metal.COPPER, TempRating.T75));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(736, Metal.COPPER, TempRating.T90));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(455.1, Metal.ALUMINUM, TempRating.T60));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(546, Metal.ALUMINUM, TempRating.T75));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerCurrent(616, Metal.ALUMINUM, TempRating.T90));

        assertEquals(Size.AWG_14, ConductorProperties.getSizePerCurrent(5, Metal.COPPER, TempRating.T60));
        assertEquals(Size.AWG_14, ConductorProperties.getSizePerCurrent(19, Metal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_12, ConductorProperties.getSizePerCurrent(26, Metal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_4, ConductorProperties.getSizePerCurrent(41, Metal.ALUMINUM, TempRating.T60));
        assertEquals(Size.KCMIL_250, ConductorProperties.getSizePerCurrent(181, Metal.ALUMINUM, TempRating.T75));
        assertEquals(Size.KCMIL_900, ConductorProperties.getSizePerCurrent(450, Metal.ALUMINUM, TempRating.T90));
    }
}