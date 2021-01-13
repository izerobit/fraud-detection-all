package com.test.demo.wmstrategy;

import com.test.demo.Rule;
import org.apache.flink.api.common.eventtime.*;

import java.time.Duration;

public class UpdateRuleWatermarkStrategy implements WatermarkStrategy<Rule> {

    public static class CusWatermarkGenerator<Rule> implements WatermarkGenerator{

        @Override
        public void onEvent(Object event, long eventTimestamp, WatermarkOutput output) {

        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            output.emitWatermark(new Watermark(Long.MAX_VALUE));
        }
    }

    @Override
    public WatermarkGenerator<Rule> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        return new WatermarksWithIdleness(new BoundedOutOfOrdernessWatermarks(Duration.ofMinutes(5)), Duration.ofMillis(1)){
            @Override
            public void onPeriodicEmit(WatermarkOutput output) {
//                super.onPeriodicEmit(new Watermark(Long.MAX_VALUE));

            }
        };
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
