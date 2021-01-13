package com.test.demo;

public class GraphResult {

    private Rule rule;
    private CustomWindow customWindow;
    private boolean completeness;
    private TriggerType triggerType;
    private boolean modified = false;

    public GraphResult(){}

    public GraphResult(Rule rule, CustomWindow customWindow, boolean completeness) {
        this.rule = rule;
        this.customWindow = customWindow;
        this.completeness = completeness;
    }

    public GraphResult(Rule rule, CustomWindow customWindow) {
        this.rule = rule;
        this.customWindow = customWindow;
        completeness = false;
    }

    public static enum TriggerType{

        TIME_MIN(1), COUNT(1);
        private int value;
        TriggerType(int value){
            this.value = value;
        }
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public CustomWindow getCustomWindow() {
        return customWindow;
    }

    public void setCustomWindow(CustomWindow customWindow) {
        this.customWindow = customWindow;
    }

    public boolean isCompleteness() {
        return completeness;
    }

    public void setCompleteness(boolean completeness) {
        this.completeness = completeness;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "GraphResult{" +
                "rule=" + rule +
                ", customWindow=" + customWindow +
                ", completeness=" + completeness +
                ", triggerType=" + triggerType +
                '}';
    }
}
