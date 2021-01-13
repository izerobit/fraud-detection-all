package com.test.kafka;

import com.test.demo.Constants;
import com.test.demo.Rule;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProduceRuleControl {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {

        KafkaProducer<String, String> producer = KafkaUtils.getProducer();
        while (true){
            Rule rule = new Rule();
            rule.setControlType(Rule.ControlType.EXPORT_RULES_CURRENT);
            String msg = objectMapper.writeValueAsString(rule);
            ProducerRecord<String, String> record = new ProducerRecord<>(Constants.RULE, msg);
            Future<RecordMetadata> r = producer.send(record);
            System.out.println(r.get().offset());
            System.out.println("write rule control msg EXPORT_RULES_CURRENT");
            Thread.sleep(new Random().nextInt(10) * 1000 * 30);
        }
    }
}
