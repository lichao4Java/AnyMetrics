package org.anymetrics.core.datasource.bounded;


import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.rule.BoundedRuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ScheduleBoundedDataSource<T extends DataSourceConfig, E extends BoundedRuleConfig> extends BoundedDataSource<T, BoundedRuleConfig> {

    private static AtomicInteger threadCounter = new AtomicInteger();

    private ScheduledExecutorService scheduledExecutor = newScheduledExecutorService();

    private ScheduledExecutorService newScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactory(){
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("AnyMetrics-Schedule-Thread-" + threadCounter.getAndIncrement());
                return thread;
            }
        });
    }

    @Override
    public void destory() {

        try {
            scheduledExecutor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // shutdownNow after 10 seconds
        try {
            while(!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }

            scheduledExecutor = newScheduledExecutorService();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private PipelineTaskContext parentContext;

    @Override
    public void fetch(FetchCallback dataCallback) {

        if(parentContext == null) {
            parentContext = PipelineTaskContext.getContext();
        }

        Integer interval = getRuleConfig(parentContext).getInterval();

        scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {

                try {
                    //do schedule
                    scheduleFetch(parentContext, dataCallback);

                } catch (Exception e) {
                    e.printStackTrace();
                    parentContext.getLog().trace(e.toString());
                }

                try {
                    //schedule next
                    fetch(dataCallback);

                } catch (Exception e) {
                    e.printStackTrace();
                    parentContext.getTask().setError();
                    parentContext.getLog().trace(e.toString());
                }
            }
        }, interval, TimeUnit.SECONDS);

    }

    public abstract void scheduleFetch(PipelineTaskContext context, FetchCallback dataCallback);

}
