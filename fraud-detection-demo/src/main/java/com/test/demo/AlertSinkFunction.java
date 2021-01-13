package com.test.demo;

import org.apache.flink.runtime.state.CheckpointListener;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class AlertSinkFunction extends RichSinkFunction<Alert>
        implements CheckpointedFunction, CheckpointListener {
    @Override
    public void notifyCheckpointComplete(long checkpointId) throws Exception {

    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        // write data to
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        // read data from
//        context.getOperatorStateStore().
    }

    public void invoke(Alert value, Context ctx) throws Exception {

        long wm = ctx.currentWatermark();
    }
}
