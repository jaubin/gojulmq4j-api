package org.gojul.gojulmq4j.utils.misc;

import org.junit.Test;

import static org.gojul.gojulmq4j.utils.misc.GojulStrings.isBlank;
import static org.gojul.gojulmq4j.utils.misc.GojulStrings.isNotBlank;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GojulStringsTest {

    @Test
    public void testIsBlank() {
        assertTrue(isBlank(null));
        assertTrue(isBlank(""));
        assertTrue(isBlank("    \t\n\n   "));
        assertFalse(isBlank("   java\n\n   "));
    }

    @Test
    public void testIsNotBlank() {
        assertFalse(isNotBlank(null));
        assertFalse(isNotBlank(""));
        assertFalse(isNotBlank("    \n\n   "));
        assertTrue(isNotBlank("   java\n\n   "));
    }
}