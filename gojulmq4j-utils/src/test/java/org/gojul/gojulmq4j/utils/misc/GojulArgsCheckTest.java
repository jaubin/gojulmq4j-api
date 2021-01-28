package org.gojul.gojulmq4j.utils.misc;

import org.junit.Test;

import static org.gojul.gojulmq4j.utils.misc.GojulArgsCheck.checkArgument;

public class GojulArgsCheckTest {

    @Test(expected = NullPointerException.class)
    public void testCheckArgumentWithNullMessageThrowsException() {
        checkArgument(true, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckArgumentWithFalseAssertionThrowsException() {
        checkArgument(false, "hello world");
    }

    @Test
    public void testCheckArgumentWithTrueAssertionDoesNothing() {
        checkArgument(true, "hello world");
    }

}