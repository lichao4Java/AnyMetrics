package org.anymetrics.core.collector;

import org.anymetrics.core.task.PipelineTaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Collector<T extends CollectorConfig> {

    protected static final Logger logger = LoggerFactory.getLogger("collector");

    protected T getCollectorConfig() {
        return (T) PipelineTaskContext.getContext().getTask().getConfigTask().getCollector();
    }

    public abstract void connect();

    public abstract void destory();

    public abstract void collect() throws Exception;

}
