package com.test.demo;

import org.apache.flink.api.java.functions.KeySelector;

public class TranRuleKeySelector implements KeySelector<TranRule, String> {
    @Override
    public String getKey(TranRule value) throws Exception {
        return value.getKey();
    }
}
