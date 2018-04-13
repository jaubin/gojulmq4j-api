package org.gojul.gojulmq4j;

/**
 * Class {@code GojulMQMessageListener} class is in charge
 * of providing a simple and reusable interface to deal
 * with consumed message. Basically it is a stupid simple listener.
 *
 * @author julien
 *
 * @param <T> the message type.
 */
public interface GojulMQMessageListener<T> {

    /**
     * Method invoked when a message is received. Note that it
     * is definitely not a good idea to throw an exception from
     * the listener, as it could have some unpleasant side effects.
     * 
     * @param message the message to process.
     */
    void onMessage(final T message);
}
