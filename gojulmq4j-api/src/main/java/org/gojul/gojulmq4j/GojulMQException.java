package org.gojul.gojulmq4j;

/**
 * Class {@code GojulMQException} represents the type of exception
 * to be seen when a message transfer error occurs.
 *
 * @author julien
 */
public class GojulMQException extends RuntimeException {

    /**
     * Constructor.
     */
    public GojulMQException() {

    }

    /**
     * Constructor.
     * @param message the exception message.
     */
    public GojulMQException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause the exception cause.
     */
    public GojulMQException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message the exception message.
     * @param cause the exception cause.
     */
    public GojulMQException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
