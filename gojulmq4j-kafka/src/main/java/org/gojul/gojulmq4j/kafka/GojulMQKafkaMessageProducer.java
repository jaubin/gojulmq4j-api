package org.gojul.gojulmq4j.kafka;

import com.google.common.base.Preconditions;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.gojul.gojulmq4j.GojulMQMessageKeyProvider;
import org.gojul.gojulmq4j.GojulMQMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

/**
 * Class {@code GojulMQKafkaMessageProducer} is the Kafka implementation
 * of the {@link GojulMQMessageProducer} interface. Note that
 * this class is thread-safe, and that messages are automatically flushed
 * after being sent. Although this is not the fastest configuration, it is
 * the safest one for services intended to run as daemons like this one.
 * So in order to increase performance you should try to favor
 * batch sending whenever possible.
 *
 * @author julien
 *
 * @param <T> the type of messages to be produced. Note that this type must
 *           have been generated following Avro conventions, as defined
 *           <a href="https://dzone.com/articles/kafka-avro-serialization-and-the-schema-registry">there</a>.
 */
public class GojulMQKafkaMessageProducer<T> implements GojulMQMessageProducer<T> {

    private final static Logger log = LoggerFactory.getLogger(GojulMQKafkaMessageProducer.class);

    private final KafkaProducer<String, T> producer;

    /**
     * Constructor.
     * @param settings the settings object used. These settings mirror the ones
     *                 defined in Kafka documentation, except for the key and
     *                 value serializers which are automatically set to String and
     *                 Avro serializers respectively.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws IllegalArgumentException if any of the mandatory properties is not set,
     * in our case the bootstrap server URLs, the schema registry URLs and the client ID.
     */
    public GojulMQKafkaMessageProducer(final Properties settings) {
        Objects.requireNonNull(settings, "settigs is null");
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)),
                String.format("%s not set", ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(ProducerConfig.CLIENT_ID_CONFIG)),
                String.format("%s not set", ProducerConfig.CLIENT_ID_CONFIG));
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(KafkaAvroSerializerConfig
                .SCHEMA_REGISTRY_URL_CONFIG)), String.format("%s not set", KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG));

        Properties props = (Properties) settings.clone();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
                
        this.producer = new KafkaProducer<>(props);
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
