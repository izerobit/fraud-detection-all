package com.test.demo;

import java.math.BigDecimal;

public class CustomWindow {

    private String groupKey;
    private long startTs;
    private long endTs;
    private Rule rule;
    private Rule.SimpleAccumulator accumulator;

    public CustomWindow(){}

    public CustomWindow(String groupKey, long startTs, long endTs, Rule rule) {
        this.groupKey = groupKey;
        this.startTs = startTs;
        this.endTs = endTs;
        this.rule = rule;
        accumulator = rule.accumulator();
    }


    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public long getStartTs() {
        return startTs;
    }

    public void setStartTs(long startTs) {
        this.startTs = startTs;
    }

    public long getEndTs() {
        return endTs;
    }

    public void setEndTs(long endTs) {
        this.endTs = endTs;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public void addElement(Transaction transaction) {
        accumulator.add(new BigDecimal(transaction.getPaymentAmount()));
    }

    public Rule.SimpleAccumulator getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(Rule.SimpleAccumulator accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public String toString() {
        return "CustomWindow{" +
                "groupKey='" + groupKey + '\'' +
                ", startTs=" + startTs +
                ", endTs=" + endTs +
                ", accumulator=" + accumulator.getClass().getName() +
                ", threshold=" + rule.getThreshold() +
                ", realValue=" + accumulator.getLocalValue() +
                '}';
    }
}
