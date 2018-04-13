package org.gojul.gojulmq4j;

/**
 * Interface {@code GojulMQMessageProducer} is used to produce messages
 * to send to a message broker. Note that it's not up to you to implement
 * this class, it is implemented by the various library implementations. Implementors
 * should generally considered as being thread-safe, contrary to what happens with
 * producers.
 *
 * @author julien
 */
public interface GojulMQMessageProducer {

    /**
     * Send the message {@code message} to the MQ on topic with name {@code topic}.
     *
     * @param topic the topic to which messages must be sent.
     * @param messageKeyProvider the message key provider used.
     * @param message the message to send itself.
     * @param <T> the message type.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws GojulMQException if an error occured while sending the message.
     */
    <T> void sendMessage(final String topic, final GojulMQMessageKeyProvider<T> messageKeyProvider,
                    final T message);
}
