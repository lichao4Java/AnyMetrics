package org.anymetrics.core.task;


import com.alibaba.fastjson.JSON;
import org.anymetrics.core.collector.Collector;
import org.anymetrics.core.datasource.DataSource;
import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PipelineTask {

    private static final Logger logger = LoggerFactory.getLogger("PipelineTask");

    private ConfigTask configTask;

    private DataSource dataSource;

    private Collector collector;

    private RuleFilter filter;

    /**
     * 0 stop
     * 1 running
     * 2 error
     */
    private int status;

    public synchronized void start() {

        PipelineTaskContext context = new PipelineTaskContext(this);

        PipelineTaskLog log = context.getLog();

        if(isRunning()) {
            log.trace(configTask.getName() + " has started!!!");
            return;
        }

        log.trace( "starting");

        try {
            // connect
            dataSource.connect();
            log.trace("dataSource connected");
        } catch(Exception e) {
            log.trace("dataSource connected faild - cause : " + e.getMessage());
            throw e;
        }

        try {
            // fetch data
            dataSource.fetch(new FetchCallback() {
                @Override
                public void callback(PipelineTaskContext context) throws Exception {

                    boolean resetContext = false;
                    if(PipelineTaskContext.getContext() == null) {
                        PipelineTaskContext.setContext(context);
                        resetContext = true;
                    }

                    try {
                        try {
                            // 1 filter
                            if(filter != null) {
                                filter.doFilter();
                                log.trace("fetch data : " + JSON.toJSONString(context.getFetchCallbackData()));
                            }
                        } catch(Exception e) {
                            log.trace("doFilter faild - cause : " + e.getMessage());
                            throw e;
                        }

                        try {
                            log.trace("start collector");

                            // 2 collector
                            collector.connect();
                            collector.collect();

                            log.trace("collector finish");

                        } catch(Exception e) {
                            log.trace("collector faild - cause : " + e.getMessage());
                            throw e;
                        }
                    } catch(Exception e) {
                        throw e;
                    } finally {
                        if(resetContext) {
                            PipelineTaskContext.removeContext();
                        }
                    }
                }
            });
        } catch(Exception e) {
            setError();
            log.trace("fetch data faild - cause : " + e.getMessage());
            throw e;
        }

        setRunning();
    }

    public synchronized void stop() {

        PipelineTaskContext context = PipelineTaskContext.getTaskContext(configTask.getId());

        if(context == null) {
            return;
        }

        PipelineTaskLog log = context.getLog();

        if(isStop()) {
            log.trace(configTask.getName() + " has stoped!!!");
            return;
        }


        log.trace("stoping");

        try {
            dataSource.destory();
            log.trace("destory datasource");
        } catch(Exception e) {
            setError();
            log.trace("destory datasource faild - cause : " + e.getMessage());
            throw e;
        }

        try {
            collector.destory();
            log.trace("destory collector");
        } catch(Exception e) {
            setError();
            log.trace("destory collector faild - cause : " + e.getMessage());
            throw e;
        }

        log.trace("stoped");

        PipelineTaskContext.removeTaskContextMap(configTask.getId());
        PipelineTaskContext.removeContext();
        setStop();
    }


    public boolean isRunning() {
        return this.status == 1;
    }

    public boolean isStop() {
        return this.status == 0;
    }

    public void setError() {
        this.status = 2;
    }

    public void setRunning() {
        this.status = 1;
    }

    public void setStop() {
        this.status = 0;
    }


    public ConfigTask getConfigTask() {
        return configTask;
    }

    public void setConfigTask(ConfigTask configTask) {
        this.configTask = configTask;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public RuleFilter getFilter() {
        return filter;
    }

    public void setFilter(RuleFilter filter) {
        this.filter = filter;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}