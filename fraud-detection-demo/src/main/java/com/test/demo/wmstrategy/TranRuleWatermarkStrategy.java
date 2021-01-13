package com.test.demo.wmstrategy;

import com.test.demo.TranRule;
import org.apache.flink.api.common.eventtime.*;

import java.time.Duration;

public class TranRuleWatermarkStrategy implements WatermarkStrategy<TranRule> {
    @Override
    public WatermarkGenerator<TranRule> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        return new BoundedOutOfOrdernessWatermarks(Duration.ofMillis(1000));
    }

    @Override
    public TimestampAssigner<TranRule> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
        return new TimestampAssigner<TranRule>() {
            @Override
            public long extractTimestamp(TranRule element, long recordTimestamp) {
                return element.getTimestamp();
            }
        };
    }
}
