package org.gojul.gojulmq4j.kafka;

import com.google.common.base.Preconditions;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.gojul.gojulmq4j.GojulMQException;
import org.gojul.gojulmq4j.GojulMQMessageConsumer;
import org.gojul.gojulmq4j.GojulMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

/**
 * Class {@code GojulMQKafkaMessageConsumer} is the Kafka implementation of interface
 * {@link GojulMQMessageConsumer}. Note that this implementation is not thread-safe. However
 * it's not exactly a bright idea ot share the same message listener between threads in most
 * systems. Thus messages are automatically acked after being consumed. Although it is not the
 * most efficient behaviour, it is the safest one for services intended to run as daemons like
 * this one.
 *
 * @param <T> the type of messages to be read. Note that these messages must follow the norm
 *           defined by Avro so that they're recorded in the schema registry.
 */
public class GojulMQKafkaMessageConsumer<T> implements GojulMQMessageConsumer<T> {

    private final static Logger log = LoggerFactory.getLogger(GojulMQKafkaMessageConsumer.class);

    private final KafkaConsumer<String, T> consumer;
    private volatile boolean isStopped;

    /**
     * Constructor.
     *
     * @param settings the settings object used. These settings mirror the ones
     *                  defined in Kafka documentation, except for the key and
     *                  value deserializers which are automatically set to string
     *                  and Avro deserializers respectively.
     *
     * @throws NullPointerException if any of the method parameters is {@code null}.
     * @throws IllegalArgumentException if one of the mandatory parameters is not set, i.e. the Kafka server URL(s),
     * the consumer group ID, or the schema registry URL.
     */
    public GojulMQKafkaMessageConsumer(final Properties settings) {
        Objects.requireNonNull(settings, "settings is null");
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)),
                String.format("%s not set", ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(ConsumerConfig.GROUP_ID_CONFIG)),
                String.format("%s not set", ConsumerConfig.GROUP_ID_CONFIG));
        Preconditions.checkArgument(StringUtils.isNotBlank(settings.getProperty(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG)),
                String.format("%s not set", KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG));

        Properties props = (Properties) settings.clone();
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.setProperty(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, Boolean.TRUE.toString());
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE.toString());

        this.consumer = new KafkaConsumer<>(props);
        this.isStopped = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void consumeMessages(final String topic, final GojulMQMessageListener<T> messageListener) {
        Objects.requireNonNull(topic, "topic is null");
        Objects.requireNonNull(messageListener, "messageListener is null");

        consumer.subscribe(Collections.singleton(topic));

        try {
            while (!isStopped) {
                consumeMessages(messageListener);
            }
        } finally{
            consumer.close();
        }
    }

    private void consumeMessages(final GojulMQMessageListener<T> listener) {
        try {
            ConsumerRecords<String, T> records = consumer.poll(100L);

            if (records.count() > 0) {
                int countProcessed = 0;

                for (ConsumerRecord<String, T> record: records) {
                    listener.onMessage(record.value());
                    countProcessed++;

                    if (countProcessed > 100) {
                        consumer.commitSync(Collections.singletonMap(getPartition(records, record),
                                new OffsetAndMetadata(record.offset() + 1L)));
                        countProcessed = 0;
                    }
                }
                consumer.commitSync();
            }
        } catch (InvalidOffsetException | AuthenticationException | AuthorizationException e) {
            log.error("A fatal error occurred - aborting consumer", e);
            throw new GojulMQException(e);
        } catch (InterruptException e) {
            log.info("Consumer halted - halting", e);
            throw e;
        } catch (KafkaException e) {
            log.error("Error while processing message - skipping this message !", e);
        }
    }

    private TopicPartition getPartition(ConsumerRecords<String, T> records, ConsumerRecord<String, T> record) {
        int partNum = record.partition();
        String topicName = record.topic();
        for (TopicPartition tp: records.partitions()) {
            if (tp.partition() == partNum
                && topicName.equals(tp.topic())) {
                return tp;
            }
        }
        throw new IllegalArgumentException(String.format("No partition found for topic name %s and partition num %d",
                topicName, partNum));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopConsumer() {
        this.isStopped = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        consumer.close();
    }
}
