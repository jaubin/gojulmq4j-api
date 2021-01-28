package org.gojul.gojulmq4j.utils.misc;

import java.util.Objects;

/**
 * Class {@code GojulArgsCheck} contains validation utility methods
 * to perform input parameter validation.
 */
public class GojulArgsCheck {

    private GojulArgsCheck() {
        throw new IllegalStateException("Go away !!!");
    }

    /**
     * If {@code assertion} is {@code true}, this method does nothing. Otherwise it
     * throws an {@link IllegalArgumentException} with error message {@code msg}.
     *
     * @param assertion the assertion to test.
     * @param msg       the exception message to display if {@code assertion} is {@code false}.
     * @throws NullPointerException     if {@code msg} is {@code null}
     * @throws IllegalArgumentException if {@code assertion} is {@code false}.
     */
    public static void checkArgument(final boolean assertion, final String msg) {
        Objects.requireNonNull(msg, "msg is null");
        if (!assertion) {
            throw new IllegalArgumentException(msg);
        }
    }
}
