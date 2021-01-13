package com.test.kafka;

import com.test.demo.Constants;
import com.test.demo.Transaction;
import com.test.demo.TransactionSource;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProduceTrans {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {

        KafkaProducer<String, String> producer = KafkaUtils.getProducer();

        long trancactionId = 1;
        while (true){
            Transaction tran = TransactionSource.getRandomTransaction(trancactionId);
            trancactionId ++;
            String msg = objectMapper.writeValueAsString(tran);
            ProducerRecord<String, String> record = new ProducerRecord<>(Constants.TRANSACTION, msg);
            Thread.sleep(new Random().nextInt(5) * 1000);
            Future<RecordMetadata> r = producer.send(record);
            System.out.println(r.get().offset());
            System.out.println("write");
        }
    }
}
