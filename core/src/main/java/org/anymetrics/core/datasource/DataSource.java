package org.anymetrics.core.datasource;

import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.rule.RuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataSource<T extends DataSourceConfig, E extends RuleConfig> {

    protected Logger logger = LoggerFactory.getLogger("DataSource");

    protected T getDataSourceConfig() {
        return (T) PipelineTaskContext.getContext().getTask().getConfigTask().getDataSource();
    }

    protected T getDataSourceConfig(PipelineTaskContext context) {
        return (T) context.getTask().getConfigTask().getDataSource();
    }

    protected E getRuleConfig() {
       return (E) PipelineTaskContext.getContext().getTask().getConfigTask().getRule();
    }

    protected E getRuleConfig(PipelineTaskContext context) {
        return (E) context.getTask().getConfigTask().getRule();
    }

    public abstract void connect();

    public abstract void destory();

    public abstract void fetch(FetchCallback dataCallback);


}
