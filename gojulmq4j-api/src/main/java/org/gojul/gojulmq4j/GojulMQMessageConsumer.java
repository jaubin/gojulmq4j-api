package org.gojul.gojulmq4j;

import java.io.Closeable;

/**
 * Interface {@code GojulMQMessageConsumer} is used
 * in order to make it possible to consume messages easily.
 * It is not up to you to implement this interface, it is provided
 * by various implementations of the API. Note that depending on the
 * implementation instances of this interface may or may not be thread-safe.
 * However it is generally thought that a consumer should not be shared between
 * different threads, contrary to a producer. An instance of this class should
 * basically be seen as a service that you inject in your code using an IoC mechanism.
 * Note that in case you do not run this consumer as a daemon you must
 * close it explicitely.
 *
 * @param <T> the type of messages to listen to.
 * @author julien
 */
public interface GojulMQMessageConsumer<T> extends Closeable {

    /**
     * Consume messages from the topic with name {@code topicName}.
     *
     * @param topicName       the name of the topic from which messages must be consumed.
     * @param messageListener the listener implementation used to listen to messages.
     * @throws NullPointerException if any of the method parameters is {@code null}.
     */
    void consumeMessages(final String topicName, final GojulMQMessageListener<T> messageListener);

    /**
     * Notify the consumer to stop doing stuff. Note that once a consumer has
     * been stopped it cannot be reused.
     */
    void stopConsumer();
}
