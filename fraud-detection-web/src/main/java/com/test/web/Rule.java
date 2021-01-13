package com.test.web;


import java.math.BigDecimal;

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
