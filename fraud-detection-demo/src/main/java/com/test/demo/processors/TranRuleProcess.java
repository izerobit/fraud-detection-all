package com.test.demo.processors;

import com.test.demo.Rule;
import com.test.demo.Starter;
import com.test.demo.TranRule;
import com.test.demo.Transaction;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class TranRuleProcess extends BroadcastProcessFunction<Transaction, Rule, TranRule> {

    @Override
    public void processElement(Transaction value, ReadOnlyContext ctx, Collector<TranRule> out) throws Exception {
        ReadOnlyBroadcastState<Integer, Rule> rules = ctx.getBroadcastState(Starter.rulesDescriptor);
        rules.immutableEntries().forEach(rule -> {
            String appName = rule.getValue().getAppName();
            if(value.getAppName().equals(appName)){
                out.collect(new TranRule(rule.getValue().getRuleId(), rule.getValue().getRuleId() + value.getPaymentType().name(), value, value.getTimestamp()));
            }
        });
    }

    @Override
    public void processBroadcastElement(Rule value, Context ctx, Collector<TranRule> out) throws Exception {
        switch (value.getControlType()){
            case NORMAL:
                switch (value.getRuleState()){
                    case DELETE:
                        ctx.getBroadcastState(Starter.rulesDescriptor).remove(value.getRuleId());
                        break;
                    default:
                        ctx.getBroadcastState(Starter.rulesDescriptor).put(value.getRuleId(), value);
                        break;
                }
                break;
            case EXPORT_RULES_CURRENT:
                for (Map.Entry<Integer, Rule> entry: ctx.getBroadcastState(Starter.rulesDescriptor).entries()){
                    ctx.output(Starter.ruleOutputTag, entry.getValue());
                }
                break;
            default:
                break;
        }
    }
}
