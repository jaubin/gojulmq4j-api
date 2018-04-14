package org.gojul.gojulmq4j.kafka.tester;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.gojul.gojulmq4j.GojulMQMessageListener;
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

    public static void main(final String[] args) throws Throwable {
        Properties settings = new Properties();
        if (args.length == 1) {
            settings.load(new FileInputStream(args[0]));
        } else {
            settings = createConfig();
        }

        final GojulMQKafkaMessageConsumer<Dummy> consumer = new GojulMQKafkaMessageConsumer<>(settings);

        new Thread(new Runnable() {
            @Override
            public void run() {
                consumer.consumeMessages("dummyTopic", new GojulMQMessageListener<Dummy>() {
                    @Override
                    public void onMessage(Dummy dummy) {
                        System.out.println("Consumed : " + dummy.getValue());
                    }
                });
            }
        }).start();

        GojulMQKafkaMessageProducer<Dummy> producer = new GojulMQKafkaMessageProducer<>(settings);

        while (true) {
            Dummy dummy = Dummy.newBuilder().setValue("Hello " + Math.random()).build();

            producer.sendMessage("dummyTopic", e -> e.getValue().toString(), dummy);

            System.out.println("Produced : " + dummy.getValue());
        }
    }
}
