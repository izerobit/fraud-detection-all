package com.test.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;

public class KafkaUtils {

    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);

    public static final KafkaProducer<String, String> getProducer(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "all");
//        properties.put("retries", 0);
        properties.put("batch.size", 5);
        properties.put("linger.ms", 0);
        properties.put("buffer.memory", 1024);

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        return producer;
    }

    public static final Properties getConsumerProp(String groupId){
        return getConsumerProp("localhost:9092", groupId);
    }

    public static Properties getConsumerProp(String kafkaHost, String groupId) {
        Properties properties = new Properties();
        logger.info("kafka host is [" + kafkaHost + "]");
        properties.put("bootstrap.servers", kafkaHost + ":9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(GROUP_ID_CONFIG, groupId);
        properties.put("enable.auto.commit", false);
        return properties;
    }
}
