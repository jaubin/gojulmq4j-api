package org.gojul.gojulmq4j.kafka.tester;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.gojul.gojulmq4j.GojulMQMessageConsumer;
import org.gojul.gojulmq4j.GojulMQMessageListener;
import org.gojul.gojulmq4j.GojulMQMessageProducer;
import org.gojul.gojulmq4j.kafka.GojulMQKafkaMessageConsumer;
import org.gojul.gojulmq4j.kafka.GojulMQKafkaMessageProducer;

import java.io.FileInputStream;
import java.util.Properties;


public class GojulMQ4jKafkaTester {

    // NOTE : you must build this project once using Maven
    // prior to using this small test project.

    private static Properties createConfig() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "TestProducer");
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
              "http://localhost:8081");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "TestConsumer");
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
              "http://localhost:8081");


        return props;
    }

    private static void testWithAvro(final Properties settings) {
        final GojulMQMessageConsumer<Dummy> consumer = new GojulMQKafkaMessageConsumer<>(settings, Dummy.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                consumer.consumeMessages("dummyTopic", new GojulMQMessageListener<Dummy>() {
                    @Override
                    public void onMessage(Dummy dummy) {
                        System.out.println("Consumed with Avro : " + dummy.getValue());
                    }
                });
            }
        }).start();

        GojulMQMessageProducer<Dummy> producer = new GojulMQKafkaMessageProducer<>(settings, Dummy.class);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    Dummy dummy = Dummy.newBuilder().setValue("Hello " + Math.random()).build();
                    producer.sendMessage("dummyTopic", e -> e.getValue().toString(), dummy);
                    System.out.println("Produced with Avro : " + dummy.getValue());
                }
            }
        }).start();
    }

    private static void testWithoutAvro(final Properties props) {
        Properties settings = (Properties) props.clone();
        settings.remove(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG);

        final GojulMQMessageConsumer<String> consumer = new GojulMQKafkaMessageConsumer<>(settings, String.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                consumer.consumeMessages("stringTopic", new GojulMQMessageListener<String>() {
                    @Override
                    public void onMessage(String s) {
                        System.out.println("Consumed without Avro : " + s);
                    }
                });
            }
        }).start();

        GojulMQMessageProducer<String> producer = new GojulMQKafkaMessageProducer<>(settings, String.class);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    String value = "Goodbye " + Math.random();
                    producer.sendMessage("stringTopic", e -> e, value);
                    System.out.println("Produced without Avro : " + value);
                }
            }
        }).start();
    }

    public static void main(final String[] args) throws Throwable {
        Properties settings = new Properties();
        if (args.length == 1) {
            settings.load(new FileInputStream(args[0]));
        } else {
            settings = createConfig();
        }

        testWithAvro(settings);
        testWithoutAvro(settings);
    }
}
