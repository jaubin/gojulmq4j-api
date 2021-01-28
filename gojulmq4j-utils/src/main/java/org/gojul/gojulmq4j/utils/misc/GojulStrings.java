package org.gojul.gojulmq4j.utils.misc;

/**
 * Class {@code GojulStrings} contains various string utilities.
 * They're mainly inspired by Apache Commons StringUtils.
 */
public class GojulStrings {

    private GojulStrings() {
        throw new IllegalStateException("Go away !");
    }

    /**
     * Return {@code true} if {@code s} contains only spaces
     * or is {@code null}, {@code false} otherwise.
     *
     * @param s the string to test.
     * @return {@code true} if {@code s} contains only spaces
     * or is {@code null}, {@code false} otherwise.
     */
    public static boolean isBlank(final String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Return {@code false} if {@code s} contains only spaces
     * or is {@code null}, {@code true} otherwise.
     *
     * @param s the string to test.
     * @return {@code false} if {@code s} contains only spaces
     * or is {@code null}, {@code true} otherwise.
     */
    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }
}
