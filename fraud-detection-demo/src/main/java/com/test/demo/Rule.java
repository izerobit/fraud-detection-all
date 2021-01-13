package com.test.demo;

import org.apache.commons.cli.*;
import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.math.BigDecimal;

//@TypeInfo(MyRuleTypeInfoFactory.class)
public class Rule {

    public static final Rule[] rule_list = new Rule[2];
    static {
        Rule r1 = new Rule(1, RuleState.UPSERT,
                "paymentAmount", AggregatorType.SUM,
                new BigDecimal(100), "test", "paymentType",
                2, Long.MAX_VALUE);
        Rule r2 = new Rule(2, RuleState.UPSERT,
                "paymentAmount", AggregatorType.AVG,
                new BigDecimal(1), "test", "paymentType",
                5, Long.MAX_VALUE);
        rule_list[0] = r1;
        rule_list[1] = r2;
    }

    private int ruleId;
    private RuleState ruleState;
    private String aggField;
    private AggregatorType aggregatorType;
    private BigDecimal threshold;
    private String appName;
    private String groupingField;
    private Integer windowMinutes;
    private long timestamp;
    private ControlType controlType = ControlType.NORMAL;

    public Rule(){}
    public Rule(int ruleId,
                RuleState ruleState,
                String aggField,
                AggregatorType aggregatorType,
                BigDecimal threshold,
                String appName,
                String groupingField,
                Integer windowMinutes,
                long timestamp){
        this.ruleId = ruleId;
        this.ruleState = ruleState;
        this.aggField = aggField;
        this.aggregatorType = aggregatorType;
        this.threshold = threshold;
        this.appName = appName;
        this.groupingField = groupingField;
        this.windowMinutes = windowMinutes;
        this.timestamp = timestamp;
    }

    public SimpleAccumulator<BigDecimal> accumulator(){
        switch (aggregatorType){
            case SUM:
                return new SumAggregator();
            case AVG:
                return new AvgAggregator();
            default:
                throw new RuntimeException("not support accumulator {" + aggregatorType.name() + "}");
        }
    }

    public Long windowMillis() {
        return Time.minutes(windowMinutes).toMilliseconds();
    }

    public interface SimpleAccumulator<T extends BigDecimal> extends Accumulator<T,T> {}

    public static class AvgAggregator implements SimpleAccumulator<BigDecimal>{

        private BigDecimal sum = BigDecimal.ZERO;
        private BigDecimal count = BigDecimal.ZERO;

        @Override
        public void add(BigDecimal value) {
            sum = sum.add(value);
            count = count.add(BigDecimal.ONE);
        }

        @Override
        public BigDecimal getLocalValue() {
            if (count == BigDecimal.ZERO){
                return BigDecimal.ZERO;
            }
            return sum.divide(count, 3, BigDecimal.ROUND_CEILING);
        }

        @Override
        public void resetLocal() {
            sum = BigDecimal.ZERO;
            count = BigDecimal.ZERO;
        }

        @Override
        public void merge(Accumulator<BigDecimal, BigDecimal> other) {
            if (other instanceof AvgAggregator){
                AvgAggregator r = (AvgAggregator) other;
                sum = sum.add(r.sum);
                count = count.add(r.count);
            }else {
                throw new RuntimeException("do not support different accumulator");
            }
        }

        @Override
        public Accumulator<BigDecimal, BigDecimal> clone() {
            return null;
        }
    }

    public static class SumAggregator implements SimpleAccumulator<BigDecimal> {

        private long count;
        private BigDecimal sum = BigDecimal.ZERO;

        @Override
        public void add(BigDecimal value) {
            count += 1;
            sum = sum.add(value);
        }

        @Override
        public BigDecimal getLocalValue() {
            return count == 0 ? BigDecimal.ZERO : sum;
        }

        @Override
        public void resetLocal() {
            count = 0;
            sum = BigDecimal.ZERO;
        }

        @Override
        public void merge(Accumulator<BigDecimal, BigDecimal> other) {
            SumAggregator sumAggregator = (SumAggregator) other;
            count += sumAggregator.count;
            sum = sum.add(sumAggregator.sum);
        }

        @Override
        public Accumulator<BigDecimal, BigDecimal> clone() {
            SumAggregator aggregator = new SumAggregator();
            aggregator.count = count;
            aggregator.sum = sum;
            return aggregator;
        }
    }

    public enum ControlType{
        EXPORT_RULES_CURRENT, EXPORT_WINDOW_CURRENT, NORMAL
    }

    public enum AggregatorType{
        SUM, MAX, COUNT, MIN, AVG
    }

    public static enum RuleState{
        DELETE, UPSERT
    }
    // --------------


    public String getGroupingField() {
        return groupingField;
    }

    public void setGroupingField(String groupingField) {
        this.groupingField = groupingField;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public RuleState getRuleState() {
        return ruleState;
    }

    public void setRuleState(RuleState ruleState) {
        this.ruleState = ruleState;
    }

    public String getAggField() {
        return aggField;
    }

    public void setAggField(String aggField) {
        this.aggField = aggField;
    }

    public AggregatorType getAggregatorType() {
        return aggregatorType;
    }

    public void setAggregatorType(AggregatorType aggregatorType) {
        this.aggregatorType = aggregatorType;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;

    }

    public Integer getWindowMinutes() {
        return windowMinutes;
    }

    public void setWindowMinutes(Integer windowMinutes) {
        this.windowMinutes = windowMinutes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "ruleId=" + ruleId +
                ", ruleState=" + ruleState +
                ", aggField='" + aggField + '\'' +
                ", aggregatorType=" + aggregatorType +
                ", threshold=" + threshold +
                ", appName='" + appName + '\'' +
                ", groupingField='" + groupingField + '\'' +
                ", windowMinutes=" + windowMinutes +
                ", timestamp=" + timestamp +
                ", controlType=" + controlType +
                '}';
    }
}
