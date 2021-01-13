package com.test.demo.wmstrategy;

import com.test.demo.Transaction;
import org.apache.flink.api.common.eventtime.*;

import java.time.Duration;

public class TransactionWatermarkStrategy implements WatermarkStrategy<Transaction> {
    @Override
    public WatermarkGenerator<Transaction> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        return new BoundedOutOfOrdernessWatermarks(Duration.ofMinutes(5));
    }

    @Override
    public TimestampAssigner<Transaction> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
        return new TimestampAssigner<Transaction>() {
            @Override
            public long extractTimestamp(Transaction element, long recordTimestamp) {
                return element.getTimestamp();
            }
        };
    }
}
