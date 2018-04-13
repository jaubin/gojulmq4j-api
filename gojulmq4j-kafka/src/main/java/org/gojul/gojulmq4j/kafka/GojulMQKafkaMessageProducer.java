package org.gojul.gojulmq4j.kafka;

import com.google.common.base.Preconditions;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jackson.map.ser.std.StringSerializer;
import org.gojul.gojulmq4j.GojulMQMessageKeyProvider;
import org.gojul.gojulmq4j.GojulMQMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

import static org.gojul.gojulmq4j.kafka.GojulKafkaMQConstants.*;

/**
 * Class {@code GojulMQKafkaMessageProducer} is the Kafka implementation
 * of the {@link GojulMQMessageProducer} interface. It does not support
 * all the possible options for a Kafka producer, but only the ones
 * specified within {@link GojulKafkaMQConstants} class. Note that
 * this class is thread-safe, and that messages are automatically flushed
 * after being sent. Although this is not the fastest configuration, it is
 * the safest one. So in order to increase performance you should try to favor
 * batch sending whenever possible.
 *
 * @author julien
 *
 * @param <T> the type of messages to be produced. Note that this type must
 *           have been generated following Avro conventions, as defined
 *           <a href="https://dzone.com/articles/kafka-avro-serialization-and-the-schema-registry">there</a>.
 */
public class GojulMQKafkaMessageProducer<T> implements GojulMQMessageProducer<T>, Closeable {

    private final static Logger log = LoggerFactory.getLogger(GojulMQKafkaMessageProducer.class);

    private KafkaProducer<String, T> producer;

    /**
     * Constructor.
     * @param clientId the client ID used.
     * @param settings the settings object used. Note that these settings
     *                 must be set according to the constants defined in {@link GojulKafkaMQConstants}.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws IllegalArgumentException if any of the mandatory properties is not set.
     */
    public GojulMQKafkaMessageProducer(final String clientId, final Properties settings) {
        Objects.requireNonNull(clientId, "clientId is null");
        Objects.requireNonNull(settings, "settigs is null");
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(KAFKA_HOST_PORTS)), String.format("%s not set", KAFKA_HOST_PORTS));
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(SCHEMA_REGISTRY_URL)), String.format("%s not set", SCHEMA_REGISTRY_URL));

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, settings.getProperty(KAFKA_HOST_PORTS));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, settings.getProperty(SCHEMA_REGISTRY_URL));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        populatePropertyIfSet(props, ProducerConfig.ACKS_CONFIG, settings, clientId + "." + ACKS_COUNT);
        populatePropertyIfSet(props, ProducerConfig.BUFFER_MEMORY_CONFIG, settings, clientId + "." + BUFFER_SIZE);
        populatePropertyIfSet(props, ProducerConfig.RETRIES_CONFIG, settings, clientId + "." + RETRIES_COUNT);

        this.producer = new KafkaProducer<>(props);
    }

    private void populatePropertyIfSet(final Properties targetProperties, final String targetPropertyName,
                                       final Properties sourceProperties, final String sourcePropertyName) {
        String val = sourceProperties.getProperty(sourcePropertyName);
        if (val != null) {
            targetProperties.setProperty(targetPropertyName, val);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(final String topic, final GojulMQMessageKeyProvider<T> messageKeyProvider,
                            final T message) {
        Objects.requireNonNull(message, "message is null");

        sendMessages(topic, messageKeyProvider, Collections.singleton(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessages(final String topic, final GojulMQMessageKeyProvider<T> messageKeyProvider,
                             final Iterable<T> messages) {
        Objects.requireNonNull(topic, "topic is null");
        Objects.requireNonNull(messageKeyProvider, "messageKeyProvider is null");
        Objects.requireNonNull(messages, "messages is null");

        log.info(String.format("Starting to send messages to topic %s", topic));

        int i = 0;
        for (T message: messages) {
            Preconditions.checkNotNull(message, "message is null");
            producer.send(new ProducerRecord<>(topic, messageKeyProvider.getKey(message), message));
            i++;
        }
        producer.flush();
        log.info(String.format("Successfully sent %d messages to topic %s", i, topic));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.producer.close();
    }
}
