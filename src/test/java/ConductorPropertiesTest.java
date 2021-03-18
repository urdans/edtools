package test.java;

import eecalcs.conductors.*;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.systems.TempRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConductorPropertiesTest {

    @Test
    void getSizeByAmperes() {
        assertEquals(Size.AWG_1, ConductorProperties.getSizeFromStandardAmpacityTable(144.23, Metal.COPPER, TempRating.T90));
        assertEquals(Size.AWG_1$0, ConductorProperties.getSizeFromStandardAmpacityTable(144.23, Metal.COPPER, TempRating.T75));
        assertEquals(Size.AWG_3$0, ConductorProperties.getSizeFromStandardAmpacityTable(144.23, Metal.ALUMINUM, TempRating.T75));
    }

    @Test
    void hasInsulatedArea() {
        assertFalse(ConductorProperties.hasInsulatedArea(null, null));
    }


    @Test
    void getInsulatedAreaIn2() {
        assertFalse(ConductorProperties.hasInsulatedArea(null, null));
    }

    @Test
    void getCompactAreaIn2() {
        assertEquals(0.0, ConductorProperties.getCompactAreaIn2(null, null),0.0001);
        assertEquals(0.0531, ConductorProperties.getCompactAreaIn2(Size.AWG_8, Insul.RHH),0.0001);
        assertEquals(0.0, ConductorProperties.getCompactAreaIn2(Size.AWG_3, Insul.RHH),0.0001);
    }

    @Test
    void getCompactBareAreaIn2() {
        assertEquals(0.0, ConductorProperties.getCompactBareAreaIn2(null),0.0001);
        assertEquals(0.0141026094219646, ConductorProperties.getCompactBareAreaIn2(Size.AWG_8),0.0001);
        assertEquals(0.0, ConductorProperties.getCompactBareAreaIn2(Size.AWG_3),0.0001);
    }

    @Test
    void hasCompactArea() {
        assertFalse(ConductorProperties.hasCompactArea(null, null));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_3, Insul.USE));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_8, Insul.THHN));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_10, Insul.THW));
        assertFalse(ConductorProperties.hasCompactArea(Size.KCMIL_2000, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactArea(Size.AWG_8, Insul.XHHW));
        assertTrue(ConductorProperties.hasCompactArea(Size.KCMIL_1000, Insul.RHH));
    }

    @Test
    void hasCompactBareArea() {
        assertFalse(ConductorProperties.hasCompactArea(null, null));
        assertFalse(ConductorProperties.hasCompactArea(Size.AWG_3, Insul.USE));
        assertTrue(ConductorProperties.hasCompactArea(Size.KCMIL_750, Insul.THW));
    }

    @Test
    void getTempRating() {
        assertNull(ConductorProperties.getTempRating(null));
        assertEquals(TempRating.T90,ConductorProperties.getTempRating(Insul.THHN));
    }

    @Test
    void getReactance() {
        assertEquals(0, ConductorProperties.getReactance(null, ConduitProperties.Magnetic),0.0001);
        assertEquals(0.057000, ConductorProperties.getReactance(Size.AWG_1, ConduitProperties.Magnetic),0.0001);
        assertEquals(0.054, ConductorProperties.getReactance(Size.AWG_10, ConduitProperties.nonMagnetic), 0.0001);
    }

    @Test
    void getAreaCM() {
        assertEquals(4110, ConductorProperties.getAreaCM(Size.AWG_14));
        assertEquals(2000000, ConductorProperties.getAreaCM(Size.KCMIL_2000));
        assertEquals(0, ConductorProperties.getAreaCM(null));
    }

    @Test
    void getDCResistance() {
        assertEquals(3.19, ConductorProperties.getDCResistance(Size.AWG_14, Metal.COPPER, Coating.COATED),0.001);
        assertEquals(0.0106, ConductorProperties.getDCResistance(Size.KCMIL_2000, Metal.ALUMINUM, Coating.COATED),0.0001);
        assertEquals(0, ConductorProperties.getDCResistance(null, null, null),0.0001);

        assertEquals(0.0106*100*0.001/3, ConductorProperties.getDCResistance(Size.KCMIL_2000, Metal.ALUMINUM, 100, 3, Coating.COATED), 0.0001);
        assertEquals(0, ConductorProperties.getDCResistance(null, null, 0, 0,null),0.0001);
    }

    @Test
    void getACResistance() {
        assertEquals(3.1, ConductorProperties.getACResistance(Size.AWG_14, Metal.COPPER, Material.PVC),0.001);
        assertEquals(0.0166, ConductorProperties.getACResistance(Size.KCMIL_2000, Metal.ALUMINUM, Material.STEEL),0.0001);
        assertEquals(0, ConductorProperties.getACResistance(null, null, null),0.0001);
    }

    @Test
    void getAmpacity() {
        assertEquals(15, ConductorProperties.getStandardAmpacity(Size.AWG_14, Metal.COPPER, TempRating.T60));
        assertEquals(630, ConductorProperties.getStandardAmpacity(Size.KCMIL_2000, Metal.ALUMINUM, TempRating.T90));
        assertEquals(0, ConductorProperties.getStandardAmpacity(null, null, null));
    }

    @Test
    void isValidFullName() {
        assertTrue(ConductorProperties.isValidFullName("14 AWG"));
        assertTrue(ConductorProperties.isValidFullName("2000 KCMIL"));
        assertFalse(ConductorProperties.isValidFullName("2"));
    }

    @Test
    void getSizeByStringFullName() {
        assertEquals(Size.AWG_14, ConductorProperties.getSizeByStringFullName("14 AWG"));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizeByStringFullName("2000 KCMIL"));
        assertNull(ConductorProperties.getSizeByStringFullName("2"));
    }

    @Test
    void isValidInsulStringName() {
        assertTrue(ConductorProperties.isValidInsulStringName("TW"));
        assertTrue(ConductorProperties.isValidInsulStringName("ZW-2"));
        assertFalse(ConductorProperties.isValidInsulStringName("2"));
    }

    @Test
    void getInsulByStringName() {
        assertEquals(Insul.TW, ConductorProperties.getInsulByStringName("TW"));
        assertEquals(Insul.ZW2, ConductorProperties.getInsulByStringName("ZW-2"));
        assertNull(ConductorProperties.getInsulByStringName("2"));
    }

    @Test
    void getSizePerArea() {
        assertNull(ConductorProperties.getSizePerArea(2000001));
        assertNull(ConductorProperties.getSizePerArea(-2000001));

        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerArea(2000000));
        assertEquals(Size.KCMIL_2000, ConductorProperties.getSizePerArea(1999999));

        assertEquals(Size.AWG_4$0, ConductorProperties.getSizePerArea(211600));
        assertEquals(Size.AWG_4$0, ConductorProperties.getSizePerArea(211600-1));
        assertEquals(Size.KCMIL_250, ConductorProperties.getSizePerArea(211600+1));
        assertEquals(Size.AWG_4$0, ConductorProperties.getSizePerArea(200000));

        assertEquals(Size.AWG_14, ConductorProperties.getSizePerArea(4110));
        assertEquals(Size.AWG_14, ConductorProperties.getSizePerArea(4110-1));
        assertEquals(Size.AWG_14, ConductorProperties.getSizePerArea(4110-100));

        assertNull(ConductorProperties.getSizePerArea(0));
    }
}