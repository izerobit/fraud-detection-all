package com.test.demo.sinks;

import com.test.demo.GraphResult;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class InfluxDBSink extends RichSinkFunction<GraphResult> {

    private static final Logger logger = LoggerFactory.getLogger(InfluxDBSink.class);

    private transient InfluxDB influxDBClient;

    private final String influxUrl;

    public InfluxDBSink(String influxUrl){
        this.influxUrl = influxUrl;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        influxDBClient = InfluxDBFactory.connect(influxUrl);
        influxDBClient.setDatabase("string");
    }

    public void invoke(GraphResult record, Context context) {

        Point.Builder builder = Point.measurement(record.getCustomWindow().getRule().getAppName())
                .addField(record.getCustomWindow().getGroupKey(), (BigDecimal)record.getCustomWindow().getAccumulator().getLocalValue())
                .tag("tag", "none")
                .time(record.getCustomWindow().getEndTs(), TimeUnit.MILLISECONDS);
        influxDBClient.write(builder.build());
    }

    @Override
    public void close() {
        influxDBClient.close();
    }
}
