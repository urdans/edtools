package javaTests;

import eecalcs.conductors.EGC;
import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class EGCTest {

    // Returns the correct EGC size for a given OCPD rating and metal type.
    @Test
    public void test_correct_egc_size() {
        assertEquals(Size.AWG_14, EGC.getEGCSize(15, Metal.COPPER));
        assertEquals(Size.AWG_12, EGC.getEGCSize(15, Metal.ALUMINUM));

        assertEquals(Size.AWG_2, EGC.getEGCSize(500, Metal.COPPER));
        assertEquals(Size.AWG_1$0, EGC.getEGCSize(500, Metal.ALUMINUM));

        assertEquals(Size.AWG_2$0, EGC.getEGCSize(1000, Metal.COPPER));
        assertEquals(Size.AWG_4$0, EGC.getEGCSize(1000, Metal.ALUMINUM));

        assertEquals(Size.KCMIL_250, EGC.getEGCSize(2000, Metal.COPPER));
        assertEquals(Size.KCMIL_400, EGC.getEGCSize(2000, Metal.ALUMINUM));

        assertEquals(Size.KCMIL_800, EGC.getEGCSize(6000, Metal.COPPER));
        assertEquals(Size.KCMIL_1250, EGC.getEGCSize(6000, Metal.ALUMINUM));
    }

    // Returns null when the metal type is null.
    @Test
    public void test_out_of_range_ocpd() {
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(50, null));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(14, null));
        assertThrows(IllegalArgumentException.class, () -> EGC.getEGCSize(6001, null));
    }

}