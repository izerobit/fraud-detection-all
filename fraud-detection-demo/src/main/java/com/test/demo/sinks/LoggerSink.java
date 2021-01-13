package com.test.demo.sinks;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerSink<IN> extends RichSinkFunction<IN> {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LoggerSink.class);

    public void invoke(IN record, Context context) {
        try {
            String json = mapper.writeValueAsString(record);
            logger.info(json);
        } catch (JsonProcessingException e) {
            logger.warn("json parse error", e);
        }
    }
}
