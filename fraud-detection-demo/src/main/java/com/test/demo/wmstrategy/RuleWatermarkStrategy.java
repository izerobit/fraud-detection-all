package com.test.demo.wmstrategy;

import com.test.demo.Rule;
import org.apache.flink.api.common.eventtime.*;

import java.time.Duration;

public class RuleWatermarkStrategy implements WatermarkStrategy<Rule> {
    @Override
    public WatermarkGenerator<Rule> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
//        return new BoundedOutOfOrdernessWatermarks(Duration.ofMinutes(5));
        return new WatermarksWithIdleness(new BoundedOutOfOrdernessWatermarks(Duration.ofMinutes(5)), Duration.ofMinutes(1));
    }

    @Override
    public TimestampAssigner<Rule> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
        return new TimestampAssigner<Rule>() {
            @Override
            public long extractTimestamp(Rule element, long recordTimestamp) {
                return Long.MAX_VALUE;
            }
        };
    }
}
