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
     * @param cls the object type for this consumer. THis must be the type parameter
     *            of this class.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws IllegalArgumentException if one of the mandatory parameters is not set, i.e. the Kafka server URL(s),
     *      * the client ID. It is possible to avoid specifying the schema registry URL only if the specified
     *      * class is {@link String}.
     */
    public GojulMQKafkaMessageProducer(final Properties settings, final Class<T> cls) {
        Objects.requireNonNull(settings, "settings is null");
        Objects.requireNonNull(cls, "cls is null");

        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)),
                String.format("%s not set", ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(ProducerConfig.CLIENT_ID_CONFIG)),
                String.format("%s not set", ProducerConfig.CLIENT_ID_CONFIG));

        boolean useAvro = useAvro(cls);


        Properties props = (Properties) settings.clone();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        if (useAvro) {
            Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(KafkaAvroSerializerConfig
                    .SCHEMA_REGISTRY_URL_CONFIG)), String.format("%s not set", KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG));
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        } else {
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        }
                
        this.producer = new KafkaProducer<>(props);
    }

    private boolean useAvro(Class<T> cls) {
        return cls != String.class;
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
            Objects.requireNonNull(message, "message is null");
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
