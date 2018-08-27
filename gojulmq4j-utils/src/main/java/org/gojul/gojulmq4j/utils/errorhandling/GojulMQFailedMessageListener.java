package org.gojul.gojulmq4j.utils.errorhandling;

import org.gojul.gojulmq4j.GojulMQException;
import org.gojul.gojulmq4j.GojulMQMessageListener;
import org.gojul.gojulmq4j.GojulMQMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Class {@code GojulMQFailedMessageListener} is a simple decorator around {@link GojulMQMessageListener}
 * instances which forwards messages that contain errors to an hospital topic.
 *
 * @param <T> the type of message to listen to.
 */
public class GojulMQFailedMessageListener<T> implements GojulMQMessageListener<T> {

    private final static Logger log = LoggerFactory.getLogger(GojulMQFailedMessageListener.class);

    private final GojulMQMessageProducer<T> producer;
    private final GojulMQMessageListener<T> listener;
    private final String errorTopic;

    /**
     * Constructor.
     * @param producer the message producer used.
     * @param listener the message listener used.
     * @param errorTopic the error topic on which failed messages must be written.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     */
    public GojulMQFailedMessageListener(final GojulMQMessageProducer<T> producer,
                                        final GojulMQMessageListener<T> listener,
                                        final String errorTopic) {
        Objects.requireNonNull(producer, "producer is null");
        Objects.requireNonNull(listener, "listener is null");
        Objects.requireNonNull(errorTopic, "errorTopic is null");

        this.producer = producer;
        this.listener = listener;
        this.errorTopic = errorTopic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(T message) {
        try {
            listener.onMessage(message);
        } catch (GojulMQException e) {
            // We forward the exceptions due to the message queue system
            // such as the ones that would be sent by a producer in order
            // to avoid masking failures.
            log.error("Error with the MQ system");
            throw e;
        } catch (RuntimeException e) {
            log.error("Error processing message", e);
            producer.sendMessage(errorTopic, m -> null, message);
        }
    }
}
