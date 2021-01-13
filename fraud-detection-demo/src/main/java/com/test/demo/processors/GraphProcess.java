package com.test.demo.processors;

import com.test.demo.*;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class GraphProcess extends KeyedBroadcastProcessFunction<String, TranRule, Rule, GraphResult> {

    private static final Logger logger = LoggerFactory.getLogger(GraphProcess.class);

    private transient MapState<Long, GraphResult> windowState;

    private MapStateDescriptor<Long, GraphResult> mapStateDescriptor = new MapStateDescriptor<>(
            "graphWindowState", BasicTypeInfo.LONG_TYPE_INFO,
            TypeInformation.of(new TypeHint<GraphResult>() {
            }));

    @Override
    public void open(Configuration parameters) throws Exception {
        windowState = getRuntimeContext().getMapState(mapStateDescriptor);
    }

    @Override
    public void processElement(TranRule value, ReadOnlyContext ctx, Collector<GraphResult> out) throws Exception {

        int ruleId = value.getId();
        Rule rule = ctx.getBroadcastState(Starter.rulesDescriptor).get(ruleId);
        long watermark = ctx.currentWatermark();
        long eventTime = value.getTimestamp();
        long windowStartTs = calculateWindowStartTs(rule.windowMillis(), eventTime);
        String key = ctx.getCurrentKey();

        GraphResult graphResult = windowState.get(windowStartTs);
        if (graphResult == null){
            CustomWindow window = new CustomWindow(key, windowStartTs, windowStartTs + rule.windowMillis(), rule);
            graphResult = new GraphResult(rule, window);
            windowState.put(windowStartTs, graphResult);
        }

        windowState.entries().forEach(new Consumer<Map.Entry<Long, GraphResult>>() {
            @Override
            public void accept(Map.Entry<Long, GraphResult> entry) {
                GraphResult result = entry.getValue();
                CustomWindow window = result.getCustomWindow();
                if (eventTime < window.getEndTs() && eventTime >= windowStartTs){
                    window.getAccumulator().add(new BigDecimal(value.getTransaction().getPaymentAmount()));
                    result.setModified(true);
                    if (result.isCompleteness()){
                        out.collect(result);
                    }
                }
            }
        });
        ctx.timerService().registerProcessingTimeTimer(ctx.currentProcessingTime() + 1 * 60 * 1000);
    }

    private long calculateWindowStartTs(Long time, long eventTime) {
        return time * (eventTime / time);
    }

    @Override
    public void processBroadcastElement(Rule value, Context ctx, Collector<GraphResult> out) throws Exception {
        ctx.getBroadcastState(Starter.rulesDescriptor).put(value.getRuleId(), value);
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<GraphResult> out) throws Exception {

        ctx.timerService().registerProcessingTimeTimer(ctx.currentProcessingTime() + 1 * 60 * 1000);
        Iterator<Map.Entry<Long, GraphResult>> ite = windowState.entries().iterator();
        while (ite.hasNext()){
            Map.Entry<Long, GraphResult> next = ite.next();
            GraphResult result = next.getValue();
            if (!result.isCompleteness()){
                if (ctx.currentWatermark() > result.getCustomWindow().getEndTs()) {
                    result.setCompleteness(true);
                    result.setModified(true);
                }
                if (result.isModified()){
                    out.collect(result);
                    result.setModified(false);
                }
            }else {
                if (ctx.currentWatermark() > result.getCustomWindow().getEndTs() + 3 * 60 * 1000L){
                    ite.remove();
                    logger.info("remove graphResult key {}, ts {}", ctx.getCurrentKey(), next.getKey());
                }
            }
        }
    }
}
