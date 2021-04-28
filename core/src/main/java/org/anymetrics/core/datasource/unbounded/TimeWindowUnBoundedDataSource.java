package org.anymetrics.core.datasource.unbounded;

import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.UnboundedRuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间窗口 DataSource
 * @param <T>
 * @param <E>
 */
public abstract class TimeWindowUnBoundedDataSource<T extends DataSourceConfig, E extends UnboundedRuleConfig> extends UnboundedDataSource<T, E> {

    private volatile AtomicBoolean running = new AtomicBoolean();

    private static AtomicInteger threadCounter = new AtomicInteger();

    private ExecutorService bossExecutorService = Executors.newSingleThreadExecutor(new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("AnyMetrics-TimeWindow-Consumer-Boss" + threadCounter.getAndIncrement());
            return thread;
        }
    });


    private ExecutorService workExecutorService = Executors.newCachedThreadPool(new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("AnyMetrics-TimeWindow-Consumer-Work" + threadCounter.getAndIncrement());
            return thread;
        }
    });

    @Override
    public void connect() {
    }

    @Override
    public void destory() {

        running.set(false);

        // shutdown
        bossExecutorService.shutdown();
        workExecutorService.shutdown();

        // shutdownNow after 10 seconds
        try {
            if(!bossExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                bossExecutorService.shutdownNow();
            }
            if(!workExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                workExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void fetch(FetchCallback callback) {

        UnboundedRuleConfig ruleConfig = getRuleConfig();

        startBossThread(ruleConfig, callback);

    }

    public abstract List<Object> poll();


    private void startBossThread(UnboundedRuleConfig ruleConfig, FetchCallback callback) {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        bossExecutorService.submit(new Runnable() {
            @Override
            public void run() {

                List<Object> fetchData = new ArrayList<>();

                // seconds
                Integer timeWindow = ruleConfig.getTimeWindow();

                running.set(true);

                // init
                long startOfMillis = System.currentTimeMillis();

                while (running.get() && !Thread.interrupted()) {

                    logger.debug("boss thread running");

                    List<Object> poll = poll();
                    if(poll != null && poll.size() > 0) {
                        fetchData.addAll(poll);
                    }

                    long endOfMillis = System.currentTimeMillis();

                    if((endOfMillis  - startOfMillis) / 1000 >= timeWindow) {

                        // reset
                        startOfMillis = System.currentTimeMillis();

                        dispatchWorkThread(context, callback, fetchData);

                    }
                }

                logger.info("boss thread closed");

            }

        });
    }


    private void dispatchWorkThread(PipelineTaskContext context, FetchCallback callback, List<Object> fetchData) {

        ArrayList<Object> fetchDataTemp = new ArrayList<>(fetchData);
        fetchData.clear();

        ArrayList<FetchData> fetchCallbackData = new ArrayList<>();

        workExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                logger.info("work thread running");

                // trans
                for(Object object : fetchDataTemp) {

                    FetchData data = new FetchData();
                    data.setFetchData(object);
                    fetchCallbackData.add(data);

                }
                context.setFetchCallbackData(fetchCallbackData);
                try {
                    callback.callback(context);
                } catch (Exception e) {
                    e.printStackTrace();
                    context.getTask().setError();
                    context.getLog().trace(e.toString());
                }

            }
        });

    }
}
