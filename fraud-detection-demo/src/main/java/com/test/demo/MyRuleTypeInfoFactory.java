package com.test.demo;

import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.lang.reflect.Type;
import java.util.Map;

public class MyRuleTypeInfoFactory extends TypeInfoFactory<Rule> {
    @Override
    public TypeInformation<Rule> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {
//        genericParameters.get
//        return new RuleTypeInfo();
        return null;
    }
}
