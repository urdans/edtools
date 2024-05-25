package test.java;

import eecalcs.circuits.OCPD;
import eecalcs.conductors.EGC;
import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OCPDTest {

    @Test
    void getRatingFor() {
        //case for upper extreme limit
        assertEquals(6000, OCPD.getRatingFor(6001, true));
        assertEquals(6000, OCPD.getRatingFor(6001, false));

        //case for an ampacity value even greater than the critical value of 800
        assertEquals(1000, OCPD.getRatingFor(1001, true));
        assertEquals(1000, OCPD.getRatingFor(1001, false));

        //case for an ampacity value greater than the critical value of 800
        assertEquals(800, OCPD.getRatingFor(900, true));
        assertEquals(800, OCPD.getRatingFor(900, false));

        //case for an ampacity value equal to the critical value of 800
        assertEquals(800, OCPD.getRatingFor(800, true));
        assertEquals(800, OCPD.getRatingFor(800, false));

        //case for an ampacity value lesser than the critical value of 800
        assertEquals(700, OCPD.getRatingFor(750, false));
        assertEquals(800, OCPD.getRatingFor(750, true));

        //case for an ampacity value even lesser than the critical value of 800
        assertEquals(200, OCPD.getRatingFor(224, false));
        assertEquals(225, OCPD.getRatingFor(224, true));

        //lower extreme limit case
        assertEquals(15, OCPD.getRatingFor(14, true));
        assertEquals(15, OCPD.getRatingFor(14, false));
    }

    @Test
    void getEGCSize(){
        assertNull(EGC.getEGCSize(0, Metal.COPPER));
        assertEquals(Size.AWG_14, EGC.getEGCSize(1, Metal.COPPER));
        assertEquals(Size.AWG_14, EGC.getEGCSize(-10, Metal.COPPER));
        assertEquals(Size.AWG_14, EGC.getEGCSize(14, Metal.COPPER));
        assertEquals(Size.AWG_14, EGC.getEGCSize(15, Metal.COPPER));

        assertNull(EGC.getEGCSize(0, Metal.COPPER));
        assertEquals(Size.AWG_12, EGC.getEGCSize(1, Metal.ALUMINUM));
        assertEquals(Size.AWG_12, EGC.getEGCSize(-10, Metal.ALUMINUM));
        assertEquals(Size.AWG_12, EGC.getEGCSize(14, Metal.ALUMINUM));
        assertEquals(Size.AWG_12, EGC.getEGCSize(15, Metal.ALUMINUM));

        assertEquals(Size.AWG_12, EGC.getEGCSize(-16, Metal.COPPER));
        assertEquals(Size.AWG_12, EGC.getEGCSize(16, Metal.COPPER));
        assertEquals(Size.AWG_10, EGC.getEGCSize(-16, Metal.ALUMINUM));
        assertEquals(Size.AWG_10, EGC.getEGCSize(16, Metal.ALUMINUM));

        assertEquals(Size.AWG_10, EGC.getEGCSize(-55, Metal.COPPER));
        assertEquals(Size.AWG_10, EGC.getEGCSize(55, Metal.COPPER));
        assertEquals(Size.AWG_8, EGC.getEGCSize(-55, Metal.ALUMINUM));
        assertEquals(Size.AWG_8, EGC.getEGCSize(55, Metal.ALUMINUM));

        assertEquals(Size.AWG_8, EGC.getEGCSize(-100, Metal.COPPER));
        assertEquals(Size.AWG_6, EGC.getEGCSize(150, Metal.COPPER));
        assertEquals(Size.AWG_6, EGC.getEGCSize(100, Metal.ALUMINUM));
        assertEquals(Size.AWG_4, EGC.getEGCSize(-150, Metal.ALUMINUM));

        assertEquals(Size.AWG_2$0, EGC.getEGCSize(1000, Metal.COPPER));
        assertEquals(Size.AWG_4$0, EGC.getEGCSize(1000, Metal.ALUMINUM));

        assertEquals(Size.KCMIL_800, EGC.getEGCSize(-5999, Metal.COPPER));
        assertEquals(Size.KCMIL_1500, EGC.getEGCSize(5999, Metal.ALUMINUM));


        assertEquals(Size.KCMIL_800, EGC.getEGCSize(6000, Metal.COPPER));
        assertEquals(Size.KCMIL_800, EGC.getEGCSize(-6000, Metal.COPPER));
        assertEquals(Size.KCMIL_1500, EGC.getEGCSize(6000, Metal.ALUMINUM));
        assertEquals(Size.KCMIL_1500, EGC.getEGCSize(-6000, Metal.ALUMINUM));

        assertNull(EGC.getEGCSize(6001, Metal.COPPER));
        assertNull(EGC.getEGCSize(6001, Metal.ALUMINUM));

        assertEquals(Size.AWG_6, EGC.getEGCSize(110, Metal.COPPER));
    }

    @Test
    void getNextHigherRating(){
        assertEquals(20, OCPD.getNextHigherRating(15));
        assertEquals(6000, OCPD.getNextHigherRating(5000));
        assertEquals(0, OCPD.getNextHigherRating(6000));
        assertEquals(0, OCPD.getNextHigherRating(10));
        assertEquals(0, OCPD.getNextHigherRating(59));
    }

    @Test
    void getNextLowerRating(){
        assertEquals(15, OCPD.getNextLowerRating(20));
        assertEquals(5000, OCPD.getNextLowerRating(6000));
        assertEquals(0, OCPD.getNextLowerRating(15));
        assertEquals(0, OCPD.getNextLowerRating(10));
        assertEquals(0, OCPD.getNextLowerRating(59));
    }

    @Test
    void getClosestMatch(){
        assertEquals(15, OCPD.getClosestMatch(5));
        assertEquals(15, OCPD.getClosestMatch(-1));
        assertEquals(25, OCPD.getClosestMatch(22.5));
        assertEquals(20, OCPD.getClosestMatch(22.4));
        assertEquals(6000, OCPD.getClosestMatch(5501));
        assertEquals(5000, OCPD.getClosestMatch(5499));
        assertEquals(6000, OCPD.getClosestMatch(6001));
    }
}