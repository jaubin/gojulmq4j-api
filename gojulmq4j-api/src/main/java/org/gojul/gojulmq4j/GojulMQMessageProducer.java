package org.gojul.gojulmq4j;

import java.io.Closeable;

/**
 * Interface {@code GojulMQMessageProducer} is used to produce messages
 * to send to a message broker. Note that it's not up to you to implement
 * this class, it is implemented by the various library implementations. Implementors
 * should generally considered as being thread-safe, contrary to what happens with
 * producers. Note that in case you do not run this producer as a daemon you must
 * close it explicitely.
 *
 * @param <T> the type of messages to be produced.
 *
 * @author julien
 */
public interface GojulMQMessageProducer<T> extends Closeable {

    /**
     * Send the message {@code message} to the MQ on topic with name {@code topic}.
     *
     * @param topic the topic to which messages must be sent.
     * @param messageKeyProvider the message key provider used.
     * @param message the message to send itself.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws GojulMQException if an error occured while sending the message.
     */
    void sendMessage(final String topic, final GojulMQMessageKeyProvider<T> messageKeyProvider,
                    final T message);

    /**
     * Send the messages from iterable {@code messages}. This method allows to make batch
     * transmissions, which usually tend to be faster than single message transmissions.
     *
     * @param topic the topic to which messages must be sent.
     * @param messageKeyProvider the message key provider used.
     * @param messages the messages to send.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws GojulMQException if an error occured while sending the message.
     */
    void sendMessages(final String topic, final GojulMQMessageKeyProvider<T> messageKeyProvider,
                    final Iterable<T> messages);
}
