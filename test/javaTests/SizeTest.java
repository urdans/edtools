package javaTests;

import eecalcs.conductors.Size;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SizeTest {
    @Test
    void getNextSizeUp() {
        assertEquals(Size.AWG_12, Size.AWG_14.getNextSizeUp());

        assertEquals(Size.AWG_10, Size.AWG_12.getNextSizeUp());

        assertEquals(Size.AWG_1$0, Size.AWG_1.getNextSizeUp());

        assertEquals(Size.KCMIL_250, Size.AWG_4$0.getNextSizeUp());

        assertEquals(Size.KCMIL_2000, Size.KCMIL_1750.getNextSizeUp());

        assertNull(Size.KCMIL_2000.getNextSizeUp());
    }

    @Test
    void getSizeByName() {
        String name = Size.AWG_1.getName();
        Size sizeByName = Size.getSizeByName(name);
	    assert sizeByName != null;
	    assertEquals(name, sizeByName.getName());
        System.out.println(name + "  " + sizeByName);
    }
}