package javaTests;

import org.junit.jupiter.api.Test;
import tools.Helper;

import static org.junit.jupiter.api.Assertions.*;

class HelperTest {

    @Test
    void round() {
        assertEquals(3.1, Helper.round(Math.PI,1));
        assertEquals(3.14, Helper.round(Math.PI,2));
        assertEquals(3.142, Helper.round(Math.PI,3));
        assertEquals(3.1416, Helper.round(Math.PI,4));

    }
}