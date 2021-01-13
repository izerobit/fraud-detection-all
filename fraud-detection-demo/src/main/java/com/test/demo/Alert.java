package com.test.demo;

import java.math.BigDecimal;

public class Alert {

    private String alertId;
    private BigDecimal alertThreshold;
    private BigDecimal actureValue;


    public Alert(){}

    public Alert(String ruleId, BigDecimal threshold, BigDecimal localValue) {
        this.alertId = ruleId;
        this.alertThreshold = threshold;
        this.actureValue = localValue;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public BigDecimal getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(BigDecimal alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public BigDecimal getActureValue() {
        return actureValue;
    }

    public void setActureValue(BigDecimal actureValue) {
        this.actureValue = actureValue;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", alertThreshold=" + alertThreshold +
                ", actureValue=" + actureValue +
                '}';
    }
}
