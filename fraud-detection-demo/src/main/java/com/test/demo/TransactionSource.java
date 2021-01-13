package com.test.demo;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

public class TransactionSource implements SourceFunction<Transaction>{

    public static final long initTranscactionId = 1L;

    @Override
    public void run(SourceContext<Transaction> ctx) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long transactionId = initTranscactionId;
                while (true){
                    ctx.collect(getRandomTransaction(transactionId));
                    transactionId ++;
                }
            }
        });
        thread.start();
        thread.join();
    }

    @Override
    public void cancel() {

    }

    public static final Transaction getRandomTransaction(long transactionId){
        int paymentRandom = new Random().nextInt(20) % 3;
        PaymentType paymentType;
        if(paymentRandom== 0){
            paymentType = PaymentType.PC;
        }else if(paymentRandom == 1){
            paymentType = PaymentType.IOS;
        }else {
            paymentType = PaymentType.ANDROID;
        }
        Transaction random = new Transaction(
                transactionId,
                System.currentTimeMillis() - new Random().nextInt(10000),
                paymentType,
                new Random().nextInt(5),
                "test");
        return random;
    }
}
