package javaTests;

import eecalcs.conductors.ConductiveMetal;
import eecalcs.conductors.EGC;
import eecalcs.conductors.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class EGCTest {

    // Returns the correct EGC size for a given OCPD rating and metal type.
    @Test
    public void test_correct_egc_size() {
        assertEquals(Size.AWG_14, EGC.getEGCSize(15, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_12, EGC.getEGCSize(15, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_2, EGC.getEGCSize(500, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_1$0, EGC.getEGCSize(500, ConductiveMetal.ALUMINUM));

        assertEquals(Size.AWG_2$0, EGC.getEGCSize(1000, ConductiveMetal.COPPER));
        assertEquals(Size.AWG_4$0, EGC.getEGCSize(1000, ConductiveMetal.ALUMINUM));

        assertEquals(Size.KCMIL_250, EGC.getEGCSize(2000, ConductiveMetal.COPPER));
        assertEquals(Size.KCMIL_400, EGC.getEGCSize(2000, ConductiveMetal.ALUMINUM));

        assertEquals(Size.KCMIL_800, EGC.getEGCSize(6000, ConductiveMetal.COPPER));
        assertEquals(Size.KCMIL_1250, EGC.getEGCSize(6000, ConductiveMetal.ALUMINUM));
    }

    // Returns null when the metal type is null.
    @Test
    public void test_out_of_range_ocpd() {
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(50, null));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(14, null));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(6001, null));
    }

}