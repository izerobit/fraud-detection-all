package com.test.demo;

import com.test.demo.processors.AlertRuleProcess;
import com.test.demo.processors.GraphProcess;
import com.test.demo.processors.TranRuleProcess;
import com.test.demo.sinks.LoggerSink;
import com.test.demo.wmstrategy.TransactionWatermarkStrategy;
import com.test.demo.wmstrategy.UpdateRuleWatermarkStrategy;
import com.test.kafka.KafkaUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.test.demo.Constants.INFLUX_URL;


public class Starter {

    private static final Logger logger = LoggerFactory.getLogger(Starter.class);

    public static final MapStateDescriptor<Integer, Rule> rulesDescriptor =
            new MapStateDescriptor<>(
                    "rules", BasicTypeInfo.INT_TYPE_INFO, TypeInformation.of(new TypeHint<Rule>(){}));

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static final OutputTag<CustomWindow> windowStateOutputTag = new OutputTag<CustomWindow>("window-state-export"){};
    public static final OutputTag<Rule> ruleOutputTag = new OutputTag<Rule>("rule-export"){};

    public static final String GROUP_ID = "group-id-3";

    public static void main(String[] args) throws Exception {
        logger.info("start ....");

        Options options = new Options();
        options.addOption("kafkahost", "kafkahost", true, "kafkahost from cmd");
        options.addOption("influx", "influx", true, "influx from cmd");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        String kafkaHost = cmd.getOptionValue("kafkahost");
        String influx = cmd.getOptionValue("influx");
        boolean isLocal = false;
        if (kafkaHost == null){
            isLocal = true;
            kafkaHost = "localhost";
            influx = "http://localhost:8086";
        }
        logger.info("kafkaHost value is {}", kafkaHost);
        logger.info("influx value is {}", influx);

        Configuration config = new Configuration();
        config.setString(INFLUX_URL, influx);
        StreamExecutionEnvironment env;
        if (isLocal){
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(config);
        }else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }
        env.enableCheckpointing(2 * 60 * 1000, CheckpointingMode.EXACTLY_ONCE);
        env.getConfig().setAutoWatermarkInterval(60_000);
        env.setParallelism(1);

        FlinkKafkaConsumer<String> ruleKafkaConsumer = new FlinkKafkaConsumer<String>(Constants.RULE, new SimpleStringSchema(),
                KafkaUtils.getConsumerProp(kafkaHost, GROUP_ID + "-rule"));
        ruleKafkaConsumer.setStartFromLatest();

        BroadcastStream<Rule> ruleStream = env.addSource(ruleKafkaConsumer)
                .name("rule from kafka")
                .map((MapFunction<String, Rule>) value -> objectMapper.readValue(value, Rule.class))
                .name("rule json")
                .assignTimestampsAndWatermarks(new UpdateRuleWatermarkStrategy())
                .name("update rule watermark")
                .broadcast(rulesDescriptor);

//        BroadcastStream<Rule> ruleStream = env
//                .fromElements(Rule.rule_list)
//                .name("rule list")
//                .assignTimestampsAndWatermarks(new RuleWatermarkStrategy())
//                .name("rule watermark")
////                .union(ruleUpdateStream)
//                .broadcast(rulesDescriptor);

        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>(Constants.TRANSACTION, new SimpleStringSchema(),
                KafkaUtils.getConsumerProp(kafkaHost, GROUP_ID + "-tran"));
        flinkKafkaConsumer.setStartFromLatest();
        SingleOutputStreamOperator<TranRule> tranRuleStringKeyedStream = env.addSource(flinkKafkaConsumer)
                .map(new MapFunction<String, Transaction>() {
                    @Override
                    public Transaction map(String value) throws Exception {
                        return objectMapper.readValue(value, Transaction.class);
                    }
                })
                .uid("json map uid")
                .name("json mapper name")
                .assignTimestampsAndWatermarks(new TransactionWatermarkStrategy())
                .name("transaction watermark")
                .connect(ruleStream)
                .process(new TranRuleProcess())
                .uid("TranRuleProcess")
                .name("TranRuleProcess name");

        KeyedStream<TranRule, String> stream = tranRuleStringKeyedStream.keyBy(new TranRuleKeySelector());
        BroadcastConnectedStream<TranRule, Rule> subStream = stream.connect(ruleStream);

        SingleOutputStreamOperator<Alert> alertStream = subStream.process(new AlertRuleProcess())
                .uid("AlertRuleProcess")
                .name("AlertRuleProcess name");
        alertStream.addSink(new PrintSinkFunction<>())
                .name("alert sink");

        tranRuleStringKeyedStream.getSideOutput(ruleOutputTag)
                .addSink(new PrintSinkFunction<>())
                .name("rule output");
        alertStream.getSideOutput(windowStateOutputTag)
                .addSink(new PrintSinkFunction<>())
                .name("window state output");

        subStream.process(new GraphProcess())
                .name("graph process")
//                .addSink(new InfluxDBSink(influx))
                .addSink(new LoggerSink<>())
                .name("graph sink");
        logger.info(env.getExecutionPlan());
        env.execute("fraud detection");
    }
}
