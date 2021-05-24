package org.anymetrics.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PipelineTaskLog implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger("PipelineTaskLog");
    private static final long serialVersionUID = -2073424698752121062L;

    /**
     * 最大日志条数
     */
    private static final int MAX_LOGS = Integer.parseInt(System.getProperty("maxLogs", "100"));

    /**
     * 是否打印日志
     */
    private static boolean TRACE_LOG_PRINT = Boolean.parseBoolean(System.getProperty("traceLog", "false"));

    private String taskName;

    public PipelineTaskLog(String taskName) {
        this.taskName = taskName;
    }


    private List<String> logs = new ArrayList<>(MAX_LOGS);

    public void trace(String log) {

        log = String.format("[%s] - %s", taskName, log);

        if(TRACE_LOG_PRINT) {
            logger.info(log);
        }

        if(logs.size() >= MAX_LOGS) {
            logs.clear();
        }

        logs.add(String.format("%s - %s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())), log));
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
