package test.java;

import eecalcs.circuits.OCPD;
import eecalcs.conductors.ConductiveMetal;
import eecalcs.conductors.EGC;
import eecalcs.conductors.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows (IllegalArgumentException.class, () -> EGC.getEGCSize(0, ConductiveMetal.COPPER));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(1, ConductiveMetal.COPPER));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(-10, ConductiveMetal.COPPER));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(14, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_14, EGC.getEGCSize(15, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_12, EGC.getEGCSize(15, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_12, EGC.getEGCSize(16, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_10, EGC.getEGCSize(16, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_10, EGC.getEGCSize(55, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_8, EGC.getEGCSize(55, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_6, EGC.getEGCSize(150, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_6, EGC.getEGCSize(100, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_2$0, EGC.getEGCSize(1000, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_4$0, EGC.getEGCSize(1000, ConductiveMetal.ALUMINUM));

        assertEquals(Size.KCMIL_1250, EGC.getEGCSize(5999, ConductiveMetal.ALUMINUM));

        assertEquals(Size.KCMIL_800, EGC.getEGCSize(6000, ConductiveMetal.COPPER));
        assertEquals(Size.KCMIL_1250, EGC.getEGCSize(6000, ConductiveMetal.ALUMINUM));

        assertThrows (IllegalArgumentException.class, () -> EGC.getEGCSize(6001, ConductiveMetal.COPPER));
        assertThrows (IllegalArgumentException.class, () -> EGC.getEGCSize(6001, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_6, EGC.getEGCSize(110, ConductiveMetal.COPPER));
    }

    @Test
    void getNextHigherRating(){
        assertEquals(15, OCPD.getNextHigherRating(10));
        assertEquals(15, OCPD.getNextHigherRating(15));
        assertEquals(20, OCPD.getNextHigherRating(16));
        assertEquals(60, OCPD.getNextHigherRating(59));
        assertEquals(5000, OCPD.getNextHigherRating(5000));
        assertEquals(6000, OCPD.getNextHigherRating(6000));
        assertEquals(6000, OCPD.getNextHigherRating(7000));
    }

    @Test
    void getNextLowerRating(){
        assertEquals(15, OCPD.getNextLowerRating(10));
        assertEquals(15, OCPD.getNextLowerRating(15));
        assertEquals(15, OCPD.getNextLowerRating(19));
        assertEquals(50, OCPD.getNextLowerRating(59));
        assertEquals(5000, OCPD.getNextLowerRating(5999));
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