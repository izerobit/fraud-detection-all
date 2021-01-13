package com.test.demo.processors;

import com.test.demo.*;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.runtime.state.KeyedStateFunction;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class AlertRuleProcess extends KeyedBroadcastProcessFunction<String, TranRule, Rule, Alert> {

    private static final Logger logger = LoggerFactory.getLogger(AlertRuleProcess.class);

    private transient MapState<Long, CustomWindow> winState;

    private transient Counter counter;

    private MapStateDescriptor<Long, CustomWindow> winStateDescriptor =
            new MapStateDescriptor<>(
                    "winState",
                    BasicTypeInfo.LONG_TYPE_INFO,
                    TypeInformation.of(new TypeHint<CustomWindow>() {
                    }));

    @Override
    public void open(Configuration parameters) throws Exception {
        winState = getRuntimeContext().getMapState(winStateDescriptor);
        counter = getRuntimeContext().getMetricGroup().counter("alert count");
//        Accumulator<Object, Serializable> accumulator = getRuntimeContext().getAccumulator("acc");
    }

    public void processElement(TranRule value, ReadOnlyContext ctx, Collector<Alert> out) throws Exception {
        int ruleId = value.getId();
        Rule rule = ctx.getBroadcastState(Starter.rulesDescriptor).get(ruleId);

        String groupKey = ctx.getCurrentKey();
        long currentEventTime = value.getTransaction().getTimestamp();

        createWindowForElement(rule, value.getTransaction(), groupKey, currentEventTime);
        addElementToWindow(rule, value.getTransaction(), groupKey, currentEventTime, new BiConsumer<CustomWindow, BigDecimal>() {
            @Override
            public void accept(CustomWindow customWindow, BigDecimal realValue) {
                out.collect(new Alert(ruleId + "", rule.getThreshold(), realValue));
                counter.inc();
            }
        });

        ctx.timerService().registerProcessingTimeTimer(ctx.currentProcessingTime() + 1 * 1000);
    }

    private void addElementToWindow(Rule rule, Transaction transaction, String groupKey, long currentEventTime, BiConsumer<CustomWindow, BigDecimal> consumer) throws Exception {

        Iterator<Map.Entry<Long, CustomWindow>> ite = winState.iterator();
        while (ite.hasNext()){
            Map.Entry<Long, CustomWindow> currentWindowEntry = ite.next();
            CustomWindow cusWindow = currentWindowEntry.getValue();
            cusWindow.addElement(transaction);
            BigDecimal realValue = (BigDecimal) cusWindow.getAccumulator().getLocalValue();
            if (realValue.compareTo(rule.getThreshold()) > 0){
                consumer.accept(cusWindow, realValue);
                ite.remove();
            }
        }
    }

    private void createWindowForElement(Rule rule, Transaction transaction, String groupKey, long currentEventTime) throws Exception {

        CustomWindow window = winState.get(currentEventTime);
        if (window == null){
            window = new CustomWindow(groupKey, currentEventTime, currentEventTime + rule.windowMillis(), rule);
            winState.put(currentEventTime, window);
        }
    }

    @Override
    public void processBroadcastElement(Rule value, Context ctx, Collector<Alert> out) throws Exception {

        switch (value.getControlType()){
            case NORMAL:
                ctx.getBroadcastState(Starter.rulesDescriptor).put(value.getRuleId(), value);
                break;
            case EXPORT_WINDOW_CURRENT:
                logger.info("process export window current ... all key {}", winState.keys());
                ctx.applyToKeyedState(winStateDescriptor, new KeyedStateFunction<String, MapState<Long, CustomWindow>>() {
                    @Override
                    public void process(String key, MapState<Long, CustomWindow> state) throws Exception {
                        logger.info("key {}", key);
                        state.entries().forEach(mapEntry -> {
                            ctx.output(Starter.windowStateOutputTag, mapEntry.getValue());

                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimer(final long timestamp, final OnTimerContext ctx, final Collector<Alert> out)
            throws Exception {
        long watermark = ctx.currentWatermark();
        Iterator<Map.Entry<Long, CustomWindow>> ite = winState.iterator();
        while (ite.hasNext()){
            Map.Entry<Long, CustomWindow> next = ite.next();
            if (next.getValue().getEndTs() < watermark){
                ite.remove();
                logger.info("remove winState for key {}", next);
            }
        }
    }
}
