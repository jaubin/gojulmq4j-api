package org.gojul.gojulmq4j;

/**
 * Some message brokers strongly suggest that you provide a key for your messages.
 * The purpose of this interface is to provide a simple way to generate this key.
 *
 * @param <T> the type of object for which you must provide a key.
 * @author julien
 */
@FunctionalInterface
public interface GojulMQMessageKeyProvider<T> {

    /**
     * Return the key for object {@code msg}. Depending on your
     * needs this method may return {@code null}.
     *
     * @param msg the message for which a key must be generated.
     * @return the key for object {@code msg}.
     */
    String getKey(final T msg);
}
