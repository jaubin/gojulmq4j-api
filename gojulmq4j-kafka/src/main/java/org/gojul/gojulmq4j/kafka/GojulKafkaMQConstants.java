package org.gojul.gojulmq4j.kafka;

/**
 * Class {@code GojulKafkaMQConstants} defines global
 * constants used to configure GojulKafka MQ implementors.
 *
 * @author julien
 */
public class GojulKafkaMQConstants {

    /**
     * The property name defining the Kafka hosts and port.
     */
    public final static String KAFKA_HOST_PORTS = "kafka.hosts";

    /**
     * The property name defining the schema registry URL.
     */
    public final static String SCHEMA_REGISTRY_URL = "avro.host";

    /**
     * <p>
     * The property name indicating the number of acks expected
     * from message brokers. Check Kafka documentation for the possible values
     * and their meaning.
     * </p>
     * <p>Note that this property is set per client identifier and is purely optional.
     * It must be prefixed with the producer identifier in your configuration.</p>
     */
    public final static String ACKS_COUNT = "kafka.acks";

    /**
     * <p>
     * The property name indicating the number of retries if a message could
     * not be sent. Check Kafka documentation for the possible values and
     * their meaning.
     * </p>
     * <p>Note that this property is set per client identifier and is purely optional.
     * It must be prefixed with the producer identifier in your configuration.</p>
     */
    public final static String RETRIES_COUNT = "kafka.retries";

    /**
     * <p>
     * The property name indicating the buffer size to use to send messages. Check
     * Kafka documentation for the possible values and their meaning.
     * </p>
     * <p>Note that this property is set per client identifier and is purely optional.
     * It must be prefixed with the producer identifier in your configuration.</p>
     */
    public final static String BUFFER_SIZE = "kafka.buffer.size";

    /**
     * Private constructor. Prevents the class from being
     * implemented.
     */
    private GojulKafkaMQConstants() {

    }
}
